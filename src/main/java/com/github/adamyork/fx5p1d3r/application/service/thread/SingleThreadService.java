package com.github.adamyork.fx5p1d3r.application.service.thread;

import com.github.adamyork.fx5p1d3r.application.service.io.DocumentParserService;
import com.github.adamyork.fx5p1d3r.common.service.OutputService;
import com.github.adamyork.fx5p1d3r.common.model.ApplicationFormState;
import com.github.adamyork.fx5p1d3r.common.service.AbortService;
import com.github.adamyork.fx5p1d3r.common.service.AlertService;
import com.github.adamyork.fx5p1d3r.common.service.ThrottledUrlService;
import com.github.adamyork.fx5p1d3r.common.service.UrlServiceFactory;
import com.github.adamyork.fx5p1d3r.common.service.progress.ProgressService;
import javafx.concurrent.WorkerStateEvent;
import org.jsoup.nodes.Document;
import org.springframework.context.MessageSource;

import java.net.URL;
import java.util.List;
import java.util.concurrent.Executors;

/**
 * Created by Adam York on 2/28/2017.
 * Copyright 2017
 */
public class SingleThreadService extends BaseThreadService {

    public SingleThreadService(final UrlServiceFactory urlServiceFactory,
                               final ApplicationFormState applicationFormState,
                               final OutputService outputService,
                               final AbortService abortService,
                               final MessageSource messageSource,
                               final AlertService alertService,
                               final DocumentParserService jsonDocumentParser,
                               final DocumentParserService csvDocumentParser,
                               final ProgressService progressService,
                               final LinksFollower linksFollower) {
        super(urlServiceFactory, applicationFormState,
                outputService, abortService, messageSource, alertService,
                jsonDocumentParser, csvDocumentParser, progressService, linksFollower);
    }

    @Override
    public void execute(final List<URL> urls) {
        executorService = Executors.newFixedThreadPool(1);
        abortService.addObserver(this);
        final ThrottledUrlService throttledUrlService = urlServiceFactory.getThrottledServiceForUrls(urls);
        throttledUrlService.setOnSucceeded(this::onDocumentsRetrieved);
        executorService.submit(throttledUrlService);
    }

    @SuppressWarnings("unchecked")
    void onDocumentsRetrieved(final WorkerStateEvent workerStateEvent) {
        processDocuments((List<Document>) workerStateEvent.getSource().getValue());
    }

}
