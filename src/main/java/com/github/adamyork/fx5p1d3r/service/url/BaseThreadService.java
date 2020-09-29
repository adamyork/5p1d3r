package com.github.adamyork.fx5p1d3r.service.url;

import com.github.adamyork.fx5p1d3r.ApplicationFormState;
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
public class BaseThreadService implements PropertyChangeListener {

    private static final Logger logger = LogManager.getLogger(BaseThreadService.class);

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

    public BaseThreadService(final UrlServiceFactory urlServiceFactory,
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
        if (documents.size() == 0) {
            final String header = messageSource.getMessage("alert.no.documents.header", null, Locale.getDefault());
            final String content = messageSource.getMessage("alert.no.documents.content", null, Locale.getDefault());
            alertService.warn(header, content);
            logger.debug("No documents to process");
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
