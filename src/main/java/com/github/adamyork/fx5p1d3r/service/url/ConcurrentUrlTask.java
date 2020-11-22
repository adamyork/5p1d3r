package com.github.adamyork.fx5p1d3r.service.url;

import com.github.adamyork.fx5p1d3r.ApplicationFormState;
import com.github.adamyork.fx5p1d3r.LogDirectoryHelper;
import com.github.adamyork.fx5p1d3r.service.progress.ApplicationProgressService;
import com.github.adamyork.fx5p1d3r.service.progress.ProgressType;
import com.github.adamyork.fx5p1d3r.service.url.data.DocumentListWithMemo;
import javafx.concurrent.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jooq.lambda.Unchecked;
import org.jsoup.nodes.Document;

import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * Created by Adam York on 2/24/2017.
 * Copyright 2017
 */
public class ConcurrentUrlTask extends Task<DocumentListWithMemo> {

    private static final Logger logger = LogManager.getLogger(ConcurrentUrlTask.class);

    private final List<URL> urls;
    private final ApplicationProgressService progressService;
    private final int threadPoolSize;
    private final int currentDepth;
    private final int maxDepth;
    private final ApplicationFormState applicationFormState;

    public ConcurrentUrlTask(final List<URL> urls,
                             final int threadPoolSize,
                             final int currentDepth,
                             final int maxDepth,
                             final ApplicationProgressService progressService,
                             final ApplicationFormState applicationFormState) {
        this.urls = urls;
        this.threadPoolSize = threadPoolSize;
        this.currentDepth = currentDepth;
        this.maxDepth = maxDepth;
        this.progressService = progressService;
        this.applicationFormState = applicationFormState;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected DocumentListWithMemo call() {
        LogDirectoryHelper.manage();
        if (applicationFormState.throttling()) {
            final long requestDelay = applicationFormState.getThrottleMs().getValue();
            logger.debug("Waiting before batch fetch " + requestDelay);
            progressService.updateProgress(ProgressType.RETRIEVED);
            Unchecked.consumer(o -> Thread.sleep(requestDelay)).accept(null);
        }
        logger.debug("Calling all urls");
        final List<UrlServiceCallable> tasks = urls.stream()
                .map(url -> new UrlServiceCallable(url, progressService))
                .collect(Collectors.toList());
        final ExecutorService executorService = Executors.newFixedThreadPool(threadPoolSize);
        final List<Document> documents = tasks.parallelStream()
                .map(documentTask -> {
                    LogDirectoryHelper.manage();
                    try {
                        executorService.submit(documentTask).get();
                        return documentTask.get();
                    } catch (final InterruptedException | ExecutionException e) {
                        logger.warn("Error in async document retrieval " + documentTask.getUrl());
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        final DocumentListWithMemo memo = new DocumentListWithMemo();
        memo.setDocuments(documents);
        memo.setThreadPoolSize(threadPoolSize);
        memo.setCurrentDepth(currentDepth + 1);
        memo.setMaxDepth(maxDepth);
        return memo;
    }

}
