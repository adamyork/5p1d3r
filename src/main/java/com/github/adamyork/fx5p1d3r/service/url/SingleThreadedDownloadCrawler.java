package com.github.adamyork.fx5p1d3r.service.url;

import com.github.adamyork.fx5p1d3r.service.progress.ProgressService;
import com.github.adamyork.fx5p1d3r.service.progress.ProgressType;
import javafx.concurrent.WorkerStateEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Adam York on 2/19/2022.
 * Copyright 2022
 */
public class SingleThreadedDownloadCrawler implements CrawlerService, PropertyChangeListener {

    private static final Logger logger = LogManager.getLogger(SequentialUrlTask.class);

    private final UrlServiceFactory urlServiceFactory;
    private final ProgressService progressService;
    private ExecutorService executorService;

    public SingleThreadedDownloadCrawler(final UrlServiceFactory urlServiceFactory,
                                         final ProgressService progressService) {
        this.urlServiceFactory = urlServiceFactory;
        this.progressService = progressService;
    }

    @Override
    public void execute(final List<URL> urls) {
        executorService = Executors.newFixedThreadPool(1);
        progressService.addListener(this);
        final SequentialDownloadTask task = urlServiceFactory.getSequentialDownloadServiceForUrls(urls);
        task.setOnSucceeded(this::onDocumentsRetrieved);
        logger.debug("Submitting url work for download");
        executorService.submit(task);
    }

    private void onDocumentsRetrieved(final WorkerStateEvent workerStateEvent) {
        progressService.updateProgress(ProgressType.COMPLETE);
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
