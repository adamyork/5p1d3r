package com.github.adamyork.fx5p1d3r.service.url;

import com.github.adamyork.fx5p1d3r.ApplicationFormState;
import com.github.adamyork.fx5p1d3r.service.parse.DocumentParserService;
import com.github.adamyork.fx5p1d3r.service.progress.AlertService;
import com.github.adamyork.fx5p1d3r.service.progress.ApplicationProgressService;
import com.github.adamyork.fx5p1d3r.service.transform.TransformService;
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
public class SingleThreadCrawler extends BaseCrawler implements ThreadService {

    private static final Logger logger = LogManager.getLogger(SingleThreadCrawler.class);

    public SingleThreadCrawler(final UrlServiceFactory urlServiceFactory,
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
    public void execute(final List<URL> urls) {
        executorService = Executors.newFixedThreadPool(1);
        progressService.addListener(this);
        final ThrottledUrlTask throttledUrlTask = urlServiceFactory.getThrottledServiceForUrls(urls);
        throttledUrlTask.setOnSucceeded(this::onDocumentsRetrieved);
        logger.debug("Submitting url work");
        executorService.submit(throttledUrlTask);
    }

    @SuppressWarnings("unchecked")
    void onDocumentsRetrieved(final WorkerStateEvent workerStateEvent) {
        final List<Document> documents = (List<Document>) workerStateEvent.getSource().getValue();
        logger.debug(documents.size() + " documents retrieved");
        processAllDocuments(documents);
    }


}
