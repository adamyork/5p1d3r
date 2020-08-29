package com.github.adamyork.fx5p1d3r.application.service.thread;

import com.github.adamyork.fx5p1d3r.application.service.io.DocumentParserService;
import com.github.adamyork.fx5p1d3r.application.view.query.cell.DomQuery;
import com.github.adamyork.fx5p1d3r.common.model.ApplicationFormState;
import com.github.adamyork.fx5p1d3r.common.service.AbortService;
import com.github.adamyork.fx5p1d3r.common.service.AlertService;
import com.github.adamyork.fx5p1d3r.common.service.OutputService;
import com.github.adamyork.fx5p1d3r.common.service.UrlServiceFactory;
import com.github.adamyork.fx5p1d3r.common.service.progress.ProgressService;
import com.github.adamyork.fx5p1d3r.common.service.progress.ProgressType;
import javafx.collections.ObservableList;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.context.MessageSource;

import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ExecutorService;

/**
 * Created by Adam York on 8/28/2020.
 * Copyright 2020
 */
public class BaseThreadService extends BaseObservableProcessor implements ThreadService, Observer {

    protected final ApplicationFormState applicationFormState;
    protected final OutputService outputService;
    protected final AbortService abortService;
    protected final UrlServiceFactory urlServiceFactory;
    protected final MessageSource messageSource;
    protected final AlertService alertService;
    protected final DocumentParserService jsonDocumentParser;
    protected final DocumentParserService csvDocumentParser;
    protected final ProgressService progressService;
    protected final LinksFollower linksFollower;

    protected ExecutorService executorService;

    public BaseThreadService(final UrlServiceFactory urlServiceFactory,
                             final ApplicationFormState applicationFormState,
                             final OutputService outputService,
                             final AbortService abortService,
                             final MessageSource messageSource,
                             final AlertService alertService,
                             final DocumentParserService jsonDocumentParser,
                             final DocumentParserService csvDocumentParser,
                             final ProgressService progressService,
                             final LinksFollower linksFollower) {
        this.urlServiceFactory = urlServiceFactory;
        this.applicationFormState = applicationFormState;
        this.outputService = outputService;
        this.abortService = abortService;
        this.messageSource = messageSource;
        this.alertService = alertService;
        this.jsonDocumentParser = jsonDocumentParser;
        this.csvDocumentParser = csvDocumentParser;
        this.progressService = progressService;
        this.linksFollower = linksFollower;
    }

    @SuppressWarnings("DuplicatedCode")
    protected void processDocuments(final List<Document> documents) {
        final ObservableList<DomQuery> domQueryObservableList = getDomQueryList(applicationFormState);
        if (documents.size() == 0) {
            final String header = messageSource.getMessage("alert.no.documents.header", null, Locale.getDefault());
            final String content = messageSource.getMessage("alert.no.documents.content", null, Locale.getDefault());
            alertService.warn(header, content);
        }
        documents.forEach(document -> {
            parseQueries(domQueryObservableList, applicationFormState, jsonDocumentParser, csvDocumentParser, document);
            final Elements linksElementsList = document.select("a");
            final List<URL> linksList = outputService.getUrlListFromElements(linksElementsList);
            if (applicationFormState.followLinks()) {
                linksFollower.traverse(linksList, executorService, 1,
                        applicationFormState.getFollowLinksDepth().getValue(),
                        Integer.parseInt(applicationFormState.getMultiThreadMax().toString()) - 1);
            } else {
                progressService.updateProgress(ProgressType.COMPLETE);
            }
        });
        abortService.deleteObserver(this);
    }

    @Override
    public void update(Observable o, Object arg) {
        if (executorService != null) {
            executorService.shutdownNow();
            abortService.clear();
        }
        abortService.deleteObserver(this);
    }

    @Override
    public void execute(List<URL> urls) {

    }
}
