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
import com.github.adamyork.fx5p1d3r.view.query.cell.DomQuery;
import javafx.collections.ObservableList;
import javafx.concurrent.WorkerStateEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple3;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.context.MessageSource;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

/**
 * Created by Adam York on 8/28/2020.
 * Copyright 2020
 */
public class BaseCrawler implements PropertyChangeListener {

    private static final Logger logger = LogManager.getLogger(BaseCrawler.class);

    protected final ApplicationFormState applicationFormState;
    protected final UrlService urlService;
    protected final UrlServiceFactory urlServiceFactory;
    protected final MessageSource messageSource;
    protected final AlertService alertService;
    protected final TransformService jsonTransformer;
    protected final TransformService csvTransformer;
    protected final ApplicationProgressService progressService;
    protected final LinksFollower linksFollower;
    private final DocumentParserService documentParser;

    protected ExecutorService executorService;

    private int total;
    private int count;
    private List<URL> linksList;

    public BaseCrawler(final UrlServiceFactory urlServiceFactory,
                       final ApplicationFormState applicationFormState,
                       final UrlService urlService,
                       final MessageSource messageSource,
                       final AlertService alertService,
                       final TransformService jsonTransformer,
                       final TransformService csvTransformer,
                       final ApplicationProgressService progressService,
                       final LinksFollower linksFollower,
                       final DocumentParserService documentParser) {
        this.urlServiceFactory = urlServiceFactory;
        this.applicationFormState = applicationFormState;
        this.urlService = urlService;
        this.messageSource = messageSource;
        this.alertService = alertService;
        this.jsonTransformer = jsonTransformer;
        this.csvTransformer = csvTransformer;
        this.progressService = progressService;
        this.linksFollower = linksFollower;
        this.documentParser = documentParser;
    }

    protected List<Tuple3<List<Elements>, Document, List<URL>>> process(final List<Document> documents) {
        logger.debug("Processing documents");
        final ObservableList<DomQuery> domQueryObservableList = applicationFormState.getDomQueryObservableList();
        assertDocumentsSize(documents);
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

    protected void assertDocumentsSize(final List<Document> documents) {
        if (documents.size() == 0) {
            final String header = messageSource.getMessage("alert.no.documents.header", null, Locale.getDefault());
            final String content = messageSource.getMessage("alert.no.documents.content", null, Locale.getDefault());
            alertService.warn(header, content);
            logger.debug("No documents to process");
        }
    }

    protected final List<Tuple3<List<Elements>, Document, List<URL>>> processAllDocuments(final List<Document> documents) {
        final List<Tuple3<List<Elements>, Document, List<URL>>> processed = process(documents);
        final List<Object> transformed = processed.stream()
                .flatMap(object -> transform(object.v1, object.v2).stream())
                .collect(Collectors.toList());
        count = 0;
        total = transformed.size();
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
        return processed;
    }

    protected List<Object> transform(final List<Elements> allElements, final Document document) {
        return allElements.stream()
                .flatMap(elements -> {
                    if (applicationFormState.getOutputFileType().equals(OutputFileType.JSON)) {
                        return jsonTransformer.transform(elements, document)
                                .stream();
                    } else {
                        return csvTransformer.transform(elements, document)
                                .stream();
                    }
                })
                .collect(Collectors.toList());

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
