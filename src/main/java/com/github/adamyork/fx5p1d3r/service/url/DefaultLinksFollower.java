package com.github.adamyork.fx5p1d3r.service.url;

import com.github.adamyork.fx5p1d3r.ApplicationFormState;
import com.github.adamyork.fx5p1d3r.service.parse.DocumentParserService;
import com.github.adamyork.fx5p1d3r.service.progress.AlertService;
import com.github.adamyork.fx5p1d3r.service.progress.ApplicationProgressService;
import com.github.adamyork.fx5p1d3r.service.progress.ProgressService;
import com.github.adamyork.fx5p1d3r.service.progress.ProgressType;
import com.github.adamyork.fx5p1d3r.service.url.data.DocumentListWithMemo;
import javafx.concurrent.WorkerStateEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.nodes.Document;
import org.springframework.context.MessageSource;

import java.net.URL;
import java.util.*;
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
public class DefaultLinksFollower extends BaseCrawler implements LinksFollower {

    private static final Logger logger = LogManager.getLogger(DefaultLinksFollower.class);

    private ExecutorService executorService;

    public DefaultLinksFollower(final UrlServiceFactory urlServiceFactory,
                                final ApplicationFormState applicationFormState,
                                final UrlService urlService,
                                final MessageSource messageSource,
                                final AlertService alertService,
                                final ProgressService progressService,
                                final LinksFollower linksFollower,
                                final DocumentParserService documentParserService) {
        super(urlServiceFactory, applicationFormState,
                urlService, messageSource, alertService, progressService, linksFollower, documentParserService);
        setLinksFollower(this);
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
        processAllDocuments(documents, Optional.of(memo));
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
