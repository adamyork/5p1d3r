package com.github.adamyork.fx5p1d3r.service.url.download;

import com.github.adamyork.fx5p1d3r.ApplicationFormState;
import com.github.adamyork.fx5p1d3r.service.progress.ProgressService;
import com.github.adamyork.fx5p1d3r.service.progress.ProgressType;
import com.github.adamyork.fx5p1d3r.service.url.CrawlerService;
import com.github.adamyork.fx5p1d3r.service.url.UrlServiceFactory;
import javafx.concurrent.WorkerStateEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Adam York on 2/19/2022.
 * Copyright 2022
 */
public class MultiThreadedDownloadCrawler implements CrawlerService {

    private static final Logger logger = LogManager.getLogger(MultiThreadedDownloadCrawler.class);

    private final UrlServiceFactory urlServiceFactory;
    private final ProgressService progressService;
    private final ApplicationFormState applicationFormState;

    private ExecutorService executorService;

    public MultiThreadedDownloadCrawler(final UrlServiceFactory urlServiceFactory,
                                        final ProgressService progressService,
                                        final ApplicationFormState applicationFormState) {
        this.urlServiceFactory = urlServiceFactory;
        this.progressService = progressService;
        this.applicationFormState = applicationFormState;
    }

    @SuppressWarnings("DuplicatedCode")
    @Override
    public void execute(final List<URL> urls) {
        executorService = Executors.newFixedThreadPool(1);
        final int threadPoolSize = Integer.parseInt(applicationFormState.getMultiThreadMax().toString());
        final ConcurrentDownloadTask concurrentUrlTask = urlServiceFactory.getConcurrentDownloadServiceForUrls(urls, threadPoolSize);
        concurrentUrlTask.setOnSucceeded(this::onDocumentsRetrieved);
        logger.info("Submitting url download list work");
        executorService.submit(concurrentUrlTask);
    }

    private void onDocumentsRetrieved(final WorkerStateEvent workerStateEvent) {
        progressService.updateProgress(ProgressType.COMPLETE);
        if (executorService != null) {
            executorService.shutdownNow();
        }
    }

}
