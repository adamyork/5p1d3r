package com.github.adamyork.fx5p1d3r.service.url;

import com.github.adamyork.fx5p1d3r.ApplicationFormState;
import com.github.adamyork.fx5p1d3r.service.parse.DocumentParserService;
import com.github.adamyork.fx5p1d3r.service.progress.AlertService;
import com.github.adamyork.fx5p1d3r.service.progress.ProgressService;
import com.github.adamyork.fx5p1d3r.service.url.data.DocumentListWithMemo;
import javafx.concurrent.WorkerStateEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.nodes.Document;
import org.springframework.context.MessageSource;

import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * Created by Adam York on 2/28/2017.
 * Copyright 2017
 */
public class MultiThreadedCrawler extends BaseCrawler implements CrawlerService {

    private static final Logger logger = LogManager.getLogger(MultiThreadedCrawler.class);

    public MultiThreadedCrawler(final UrlServiceFactory urlServiceFactory,
                                final ApplicationFormState applicationFormState,
                                final UrlService urlService,
                                final MessageSource messageSource,
                                final AlertService alertService,
                                final ProgressService progressService,
                                final LinksFollower linksFollower,
                                final DocumentParserService documentParserService) {
        super(urlServiceFactory, applicationFormState,
                urlService, messageSource, alertService, progressService, linksFollower, documentParserService);
    }

    @SuppressWarnings("DuplicatedCode")
    @Override
    public void execute(final List<URL> urls) {
        executorService = Executors.newFixedThreadPool(1);
        progressService.addListener(this);
        final int threadPoolSize = Integer.parseInt(applicationFormState.getMultiThreadMax().toString());
        final ConcurrentUrlTask concurrentUrlTask = urlServiceFactory.getConcurrentServiceForUrls(urls, 1,
                urls.size(), threadPoolSize - 1);
        concurrentUrlTask.setOnSucceeded(this::onDocumentsRetrieved);
        logger.info("Submitting url list work");
        executorService.submit(concurrentUrlTask);
    }

    @Override
    public void close() {
        logger.info("closing multi-thread crawler");
        if(executorService != null){
            logger.info("executor service not shutdown, shutting down");
            executorService.shutdownNow();
            executorService = null;
        }
        logger.info("closing multi-thread crawler complete");
    }

    public void onDocumentsRetrieved(final WorkerStateEvent workerStateEvent) {
        final DocumentListWithMemo memo = (DocumentListWithMemo) workerStateEvent.getSource().getValue();
        final List<Document> documents = memo.getDocuments().stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        logger.info(documents.size() + " documents retrieved");
        processAllDocuments(documents, Optional.empty());
    }

}