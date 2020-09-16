package com.github.adamyork.fx5p1d3r.application.service.thread;

import com.github.adamyork.fx5p1d3r.application.service.io.DocumentParserService;
import com.github.adamyork.fx5p1d3r.common.model.ApplicationFormState;
import com.github.adamyork.fx5p1d3r.common.service.*;
import com.github.adamyork.fx5p1d3r.common.service.progress.ApplicationProgressService;
import javafx.concurrent.WorkerStateEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

    private static final Logger logger = LogManager.getLogger(SingleThreadService.class);

    public SingleThreadService(final UrlServiceFactory urlServiceFactory,
                               final ApplicationFormState applicationFormState,
                               final OutputService outputService,
                               final MessageSource messageSource,
                               final AlertService alertService,
                               final DocumentParserService jsonDocumentParser,
                               final DocumentParserService csvDocumentParser,
                               final ApplicationProgressService progressService,
                               final LinksFollower linksFollower) {
        super(urlServiceFactory, applicationFormState,
                outputService, messageSource, alertService,
                jsonDocumentParser, csvDocumentParser, progressService, linksFollower);
    }

    @Override
    public void execute(final List<URL> urls) {
        executorService = Executors.newFixedThreadPool(1);
        progressService.addListener(this);
        final ThrottledUrlService throttledUrlService = urlServiceFactory.getThrottledServiceForUrls(urls);
        throttledUrlService.setOnSucceeded(this::onDocumentsRetrieved);
        logger.debug("Submitting url work");
        executorService.submit(throttledUrlService);
    }

    @SuppressWarnings("unchecked")
    void onDocumentsRetrieved(final WorkerStateEvent workerStateEvent) {
        final List<Document> documentList = (List<Document>) workerStateEvent.getSource().getValue();
        logger.debug(documentList.size() + " documents retrieved");
        processDocuments(documentList);
    }

}
