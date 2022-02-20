package com.github.adamyork.fx5p1d3r.service.url;

import com.github.adamyork.fx5p1d3r.ApplicationFormState;
import com.github.adamyork.fx5p1d3r.service.output.CsvOutputTask;
import com.github.adamyork.fx5p1d3r.service.output.JsonOutputTask;
import com.github.adamyork.fx5p1d3r.service.output.data.OutputFileType;
import com.github.adamyork.fx5p1d3r.service.parse.DocumentParserService;
import com.github.adamyork.fx5p1d3r.service.progress.AlertService;
import com.github.adamyork.fx5p1d3r.service.progress.ProgressService;
import com.github.adamyork.fx5p1d3r.service.progress.ProgressType;
import com.github.adamyork.fx5p1d3r.service.transform.CsvTransformTask;
import com.github.adamyork.fx5p1d3r.service.transform.JsonTransformTask;
import com.github.adamyork.fx5p1d3r.service.url.data.DocumentListWithMemo;
import com.github.adamyork.fx5p1d3r.view.query.cell.DomQuery;
import javafx.collections.ObservableList;
import javafx.concurrent.WorkerStateEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple3;
import org.jooq.lambda.tuple.Tuple4;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.context.MessageSource;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.stream.Collectors;

/**
 * Created by Adam York on 8/28/2020.
 * Copyright 2020
 */
@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class BaseCrawler implements PropertyChangeListener {

    private static final Logger logger = LogManager.getLogger(BaseCrawler.class);

    protected final ApplicationFormState applicationFormState;
    protected final UrlService urlService;
    protected final UrlServiceFactory urlServiceFactory;
    protected final MessageSource messageSource;
    protected final AlertService alertService;
    protected final ProgressService progressService;
    private final DocumentParserService documentParser;
    protected LinksFollower linksFollower;
    protected ExecutorService executorService;


    public BaseCrawler(final UrlServiceFactory urlServiceFactory,
                       final ApplicationFormState applicationFormState,
                       final UrlService urlService,
                       final MessageSource messageSource,
                       final AlertService alertService,
                       final ProgressService progressService,
                       final LinksFollower linksFollower,
                       final DocumentParserService documentParser) {
        this.urlServiceFactory = urlServiceFactory;
        this.applicationFormState = applicationFormState;
        this.urlService = urlService;
        this.messageSource = messageSource;
        this.alertService = alertService;
        this.progressService = progressService;
        this.linksFollower = linksFollower;
        this.documentParser = documentParser;
    }

    public void setLinksFollower(final LinksFollower linksFollower) {
        this.linksFollower = linksFollower;
    }

    protected void processAllDocuments(final List<Document> documents,
                                       final Optional<DocumentListWithMemo> maybeMemo) {
        final List<Tuple3<List<Elements>, Document, List<URL>>> processed = process(documents);
        if (processed.isEmpty()) {
            logger.debug("Crawl Complete");
            progressService.updateProgress(ProgressType.COMPLETE);
            return;
        }
        executorService = Executors.newFixedThreadPool(1);
        transform(processed, maybeMemo);
    }

    private List<Tuple3<List<Elements>, Document, List<URL>>> process(final List<Document> documents) {
        logger.debug("Processing documents");
        final ObservableList<DomQuery> domQueryObservableList = applicationFormState.getDomQueryObservableList();
        if (!assertDocumentsSize(documents)) {
            return new ArrayList<>();
        }
        return documents.stream()
                .map(document -> {
                    final Elements linksElementsList = document.select("a");
                    final List<URL> linksList = urlService.getUrlListFromElements(linksElementsList);
                    final List<Elements> parsed = domQueryObservableList.stream()
                            .map(domQuery -> {
                                final String domQueryString = domQuery.getQuery();
                                logger.debug("Parsing document for " + domQueryString);
                                return documentParser.parse(document, domQueryString);
                            })
                            .collect(Collectors.toList());
                    return Tuple.tuple(parsed, document, linksList);
                })
                .collect(Collectors.toList());
    }

    protected boolean assertDocumentsSize(final List<Document> documents) {
        if (documents.size() == 0) {
            final String header = messageSource.getMessage("alert.no.documents.header", null, Locale.getDefault());
            final String content = messageSource.getMessage("alert.no.documents.content", null, Locale.getDefault());
            alertService.warn(header, content);
            logger.debug("No documents to process");
            return false;
        }
        return true;
    }

    protected void transform(final List<Tuple3<List<Elements>, Document, List<URL>>> processed,
                             final Optional<DocumentListWithMemo> maybeMemo) {
        if (applicationFormState.getOutputFileType().equals(OutputFileType.JSON)) {
            final JsonTransformTask transformTask = new JsonTransformTask(applicationFormState, progressService,
                    messageSource, alertService, processed, maybeMemo);
            transformTask.setOnSucceeded(this::onTransformed);
            executorService.submit(transformTask);
        } else {
            final CsvTransformTask transformTask = new CsvTransformTask(applicationFormState, progressService,
                    messageSource, alertService, processed, maybeMemo);
            transformTask.setOnSucceeded(this::onTransformed);
            executorService.submit(transformTask);
        }

    }

    @SuppressWarnings("unchecked")
    void onTransformed(final WorkerStateEvent workerStateEvent) {
        final List<Tuple4<List<Object>, Document, List<URL>, Optional<DocumentListWithMemo>>> transformed =
                (List<Tuple4<List<Object>, Document, List<URL>, Optional<DocumentListWithMemo>>>) workerStateEvent.getSource().getValue();
        if (applicationFormState.transformFailed()) {
            alertService.warn(messageSource.getMessage("alert.bad.transform.header", null, Locale.getDefault()),
                    messageSource.getMessage("alert.bad.transform.content", null, Locale.getDefault()));
        }
        if (applicationFormState.getOutputFileType().equals(OutputFileType.JSON)) {
            final JsonOutputTask outputTask = new JsonOutputTask(applicationFormState, progressService, transformed);
            outputTask.setOnSucceeded(this::onResultsWritten);
            try {
                executorService.submit(outputTask);
            } catch (final RejectedExecutionException exception) {
                logger.debug("Output aborted.");
            }
        } else {
            final CsvOutputTask outputTask = new CsvOutputTask(applicationFormState, progressService, transformed);
            outputTask.setOnSucceeded(this::onResultsWritten);
            try {
                executorService.submit(outputTask);
            } catch (final RejectedExecutionException exception) {
                logger.debug("Output aborted.");
            }
        }
    }

    @SuppressWarnings("unchecked")
    void onResultsWritten(final WorkerStateEvent workerStateEvent) {
        final List<Tuple4<List<Boolean>, Document, List<URL>, Optional<DocumentListWithMemo>>> result =
                (List<Tuple4<List<Boolean>, Document, List<URL>, Optional<DocumentListWithMemo>>>) workerStateEvent.getSource().getValue();
        if (progressService.getCurrentProgressType().equals(ProgressType.ABORT)) {
            logger.debug("Link following aborted");
            logger.debug("Crawl completed");
            progressService.updateProgress(ProgressType.COMPLETE);
        }
        if (applicationFormState.followLinks()) {
            logger.debug("Link following enabled");
            final List<URL> links = result.stream()
                    .flatMap(objects -> objects.v3.stream())
                    .distinct()
                    .collect(Collectors.toList());
            final DocumentListWithMemo memo = result.stream().findFirst()
                    .map(objects -> objects.v4.orElseGet(() -> {
                        final DocumentListWithMemo stubbed = new DocumentListWithMemo();
                        stubbed.setCurrentDepth(0);
                        stubbed.setMaxDepth(applicationFormState.getFollowLinksDepth().getValue());
                        return stubbed;
                    }))
                    .orElseGet(() -> {
                        final DocumentListWithMemo stubbed = new DocumentListWithMemo();
                        stubbed.setCurrentDepth(applicationFormState.getFollowLinksDepth().getValue());
                        stubbed.setMaxDepth(applicationFormState.getFollowLinksDepth().getValue());
                        return stubbed;
                    });
            if (memo.getCurrentDepth() < memo.getMaxDepth()) {
                logger.debug("Current depth " + memo.getCurrentDepth() + " is not max depth " + memo.getMaxDepth() + "; traverse");
                linksFollower.traverse(links, executorService, memo.getCurrentDepth(),
                        applicationFormState.getFollowLinksDepth().getValue(),
                        Integer.parseInt(applicationFormState.getMultiThreadMax().toString()) - 1);
            } else {
                logger.debug("Crawl Complete");
                progressService.updateProgress(ProgressType.COMPLETE);
            }
        } else {
            logger.debug("Link following disabled");
            logger.debug("Crawl completed");
            progressService.updateProgress(ProgressType.COMPLETE);
        }
    }

    @Override
    public void propertyChange(final PropertyChangeEvent evt) {
        if (progressService.getCurrentProgressType().equals(ProgressType.ABORT) ||
                progressService.getCurrentProgressType().equals(ProgressType.COMPLETE)) {
            if (executorService != null) {
                executorService.shutdownNow();
            }
            progressService.removeListener(this);
        }
    }

}
