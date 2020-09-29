package com.github.adamyork.fx5p1d3r.service.url;

import com.github.adamyork.fx5p1d3r.ApplicationFormState;
import com.github.adamyork.fx5p1d3r.service.output.CsvOutputTask;
import com.github.adamyork.fx5p1d3r.service.output.JsonOutputTask;
import com.github.adamyork.fx5p1d3r.service.output.data.OutputFileType;
import com.github.adamyork.fx5p1d3r.service.parse.DocumentParserService;
import com.github.adamyork.fx5p1d3r.service.progress.AlertService;
import com.github.adamyork.fx5p1d3r.service.progress.ApplicationProgressService;
import com.github.adamyork.fx5p1d3r.service.progress.ProgressType;
import com.github.adamyork.fx5p1d3r.service.transform.TransformService;
import com.github.adamyork.fx5p1d3r.service.url.data.DocumentListWithMemo;
import com.github.adamyork.fx5p1d3r.view.query.cell.DomQuery;
import javafx.collections.ObservableList;
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
public class RecursiveLinksFollower extends BaseThreadService implements LinksFollower {

    private static final Logger logger = LogManager.getLogger(RecursiveLinksFollower.class);

    private int total;
    private int count;
    private List<URL> linksList;
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
        final ObservableList<DomQuery> domQueryObservableList = applicationFormState.getDomQueryObservableList();
        if (documents.size() == 0) {
            final String header = messageSource.getMessage("alert.no.documents.header", null, Locale.getDefault());
            final String content = messageSource.getMessage("alert.no.documents.content", null, Locale.getDefault());
            alertService.warn(header, content);
            logger.debug("No documents to process");
        }
        final List<Tuple3<List<Elements>, Document, List<URL>>> processed = process(documents);
        final List<Object> transformed = processed.stream()
                .flatMap(object -> transform(object.v1, object.v2).stream())
                .collect(Collectors.toList());
        transformed.forEach(result -> {
            if (applicationFormState.getOutputFileType().equals(OutputFileType.JSON)) {
                final JsonOutputTask outputTask = new JsonOutputTask(applicationFormState, progressService, result);
                outputTask.setOnSucceeded(this::onResultWritten);
                executorService.submit(outputTask);
            } else {
                final CsvOutputTask outputTask = new CsvOutputTask(applicationFormState, progressService, (String[]) result);
                outputTask.setOnSucceeded(this::onResultWritten);
                executorService.submit(outputTask);
            }
        });
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

    void onResultWritten(final WorkerStateEvent workerStateEvent) {
        count++;
        if (total == count) {
            if (progressService.getCurrentProgressType().equals(ProgressType.ABORT)) {
                logger.debug("Link following Aborted.");
                logger.debug("Crawl completed");
                progressService.updateProgress(ProgressType.COMPLETE);
            }
            if (applicationFormState.followLinks()) {
                logger.debug("Link following enabled.");
                linksFollower.traverse(linksList, executorService, 1,
                        applicationFormState.getFollowLinksDepth().getValue(),
                        Integer.parseInt(applicationFormState.getMultiThreadMax().toString()) - 1);
            } else {
                logger.debug("Link following disabled.");
                logger.debug("Crawl completed");
                progressService.updateProgress(ProgressType.COMPLETE);
            }
        }
    }
}
