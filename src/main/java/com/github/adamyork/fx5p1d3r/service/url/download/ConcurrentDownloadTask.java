package com.github.adamyork.fx5p1d3r.service.url.download;

import com.github.adamyork.fx5p1d3r.ApplicationFormState;
import com.github.adamyork.fx5p1d3r.LogDirectoryHelper;
import com.github.adamyork.fx5p1d3r.service.progress.ProgressService;
import com.github.adamyork.fx5p1d3r.service.progress.ProgressType;
import javafx.concurrent.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jooq.lambda.Unchecked;

import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * Created by Adam York on 2/19/2022.
 * Copyright 2022
 */
public class ConcurrentDownloadTask extends Task<List<Boolean>> {

    private static final Logger logger = LogManager.getLogger(ConcurrentDownloadTask.class);

    private final List<URL> urls;
    private final ProgressService progressService;
    private final int threadPoolSize;
    private final ApplicationFormState applicationFormState;

    public ConcurrentDownloadTask(final List<URL> urls,
                                  final int threadPoolSize,
                                  final ProgressService progressService,
                                  final ApplicationFormState applicationFormState) {
        this.urls = urls;
        this.threadPoolSize = threadPoolSize;
        this.progressService = progressService;
        this.applicationFormState = applicationFormState;
    }

    @Override
    protected List<Boolean> call() {
        LogDirectoryHelper.manage();
        if (applicationFormState.downloadThrottling()) {
            final long requestDelay = applicationFormState.getDownloadThrottleMs().getValue();
            logger.debug("Waiting before batch download " + requestDelay);
            progressService.updateProgress(ProgressType.RETRIEVED);
            Unchecked.consumer(o -> Thread.sleep(requestDelay)).accept(null);
        }
        logger.debug("Downloading all urls");
        final List<UrlDownloadCallable> tasks = urls.stream()
                .map(url -> new UrlDownloadCallable(url, progressService, applicationFormState)).toList();
        final ExecutorService executorService = Executors.newFixedThreadPool(threadPoolSize);
        return tasks.parallelStream()
                .map(documentTask -> {
                    LogDirectoryHelper.manage();
                    try {
                        executorService.submit(documentTask).get();
                        return documentTask.get();
                    } catch (final InterruptedException | ExecutionException e) {
                        logger.warn("Error in async document download " + documentTask.getUrl());
                        return null;
                    }
                })
                .collect(Collectors.toList());
    }
}
