package com.github.adamyork.fx5p1d3r.service.url;

import com.github.adamyork.fx5p1d3r.ApplicationFormState;
import com.github.adamyork.fx5p1d3r.service.parse.DocumentParserService;
import com.github.adamyork.fx5p1d3r.service.progress.AlertService;
import com.github.adamyork.fx5p1d3r.service.progress.ApplicationProgressService;
import com.github.adamyork.fx5p1d3r.service.progress.ProgressType;
import com.github.adamyork.fx5p1d3r.service.transform.TransformService;
import com.github.adamyork.fx5p1d3r.service.url.data.DocumentListWithMemo;
import javafx.concurrent.WorkerStateEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jooq.lambda.tuple.Tuple3;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.context.MessageSource;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;

/**
 * Created by Adam York on 2/28/2017.
 * Copyright 2017
 */
public class RecursiveLinksFollower extends BaseCrawler implements LinksFollower {

    private static final Logger logger = LogManager.getLogger(RecursiveLinksFollower.class);

    private ExecutorService executorService;

    public RecursiveLinksFollower(final UrlServiceFactory urlServiceFactory,
                                  final ApplicationFormState applicationFormState,
                                  final UrlService urlService,
                                  final MessageSource messageSource,
                                  final AlertService alertService,
                                  final TransformService jsonTransformer,
                                  final TransformService csvTransformer,
                                  final ApplicationProgressService progressService,
                                  final LinksFollower linksFollower,
                                  final DocumentParserService documentParserService) {
        super(urlServiceFactory, applicationFormState,
                urlService, messageSource, alertService,
                jsonTransformer, csvTransformer, progressService, linksFollower, documentParserService);
    }

    @Override
    public void traverse(final List<URL> urls,
                         final ExecutorService executorService,
                         final int currentDepth,
                         final int maxDepth,
                         final int threadPoolSize) {
        progressService.updateProgress(ProgressType.LINKS);
        this.executorService = executorService;
        final List<URL> filtered = filterByRegex(urls);
        logger.debug("Following linked documents " + filtered);
        final ConcurrentUrlTask concurrentUrlTask = urlServiceFactory.getConcurrentServiceForUrls(filtered, currentDepth,
                maxDepth, threadPoolSize);
        concurrentUrlTask.setOnSucceeded(this::onDocumentsRetrieved);
        try {
            executorService.submit(concurrentUrlTask);
        } catch (final RejectedExecutionException exception) {
            logger.debug("Link following in progress aborted.");
            logger.debug("Crawl Complete");
            progressService.updateProgress(ProgressType.COMPLETE);
        }

    }

    public void onDocumentsRetrieved(final WorkerStateEvent workerStateEvent) {
        logger.debug("Document retrieved from link follow");
        final DocumentListWithMemo memo = (DocumentListWithMemo) workerStateEvent.getSource().getValue();
        final List<Document> documents = memo.getDocuments().stream()
                .filter(Objects::nonNull).collect(Collectors.toList());
        assertDocumentsSize(documents);
        final List<Tuple3<List<Elements>, Document, List<URL>>> processed = processAllDocuments(documents);
        final List<URL> flattened = processed.stream()
                .flatMap(objects -> objects.v3.stream())
                .collect(Collectors.toList());
        if (memo.getCurrentDepth() < memo.getMaxDepth()) {
            logger.debug("Current depth " + memo.getCurrentDepth() + " is not max depth " + memo.getMaxDepth() + "; recurse");
            traverse(flattened, executorService, memo.getCurrentDepth() + 1, memo.getMaxDepth(), memo.getThreadPoolSize());
        } else {
            logger.debug("Crawl Complete");
            progressService.updateProgress(ProgressType.COMPLETE);
        }
    }

    private List<URL> filterByRegex(final List<URL> urls) {
        try {
            final String followPattern = applicationFormState.getLinkFollowPattern().trim();
            final Pattern pattern = Pattern.compile(followPattern);
            return urls.stream().filter(url -> {
                final String urlString = url.toString();
                final Matcher matcher = pattern.matcher(urlString);
                return matcher.matches();
            }).collect(Collectors.toList());
        } catch (final PatternSyntaxException exception) {
            alertService.warn(messageSource.getMessage("alert.invalid.regex.header", null, Locale.getDefault()),
                    messageSource.getMessage("alert.invalid.regex.content", null, Locale.getDefault()));
            logger.debug("Invalid link following regular expression");
            return new ArrayList<>();
        }
    }

}
