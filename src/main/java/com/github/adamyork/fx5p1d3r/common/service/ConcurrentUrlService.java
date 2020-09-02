package com.github.adamyork.fx5p1d3r.common.service;

import com.github.adamyork.fx5p1d3r.LogDirectoryHelper;
import com.github.adamyork.fx5p1d3r.common.model.DocumentListWithMemo;
import com.github.adamyork.fx5p1d3r.common.service.progress.ProgressService;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jooq.lambda.Unchecked;
import org.jsoup.nodes.Document;

import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * Created by Adam York on 2/24/2017.
 * Copyright 2017
 */
public class ConcurrentUrlService extends Task<DocumentListWithMemo> {

    private static final Logger logger = LogManager.getLogger(ConcurrentUrlService.class);

    private final List<URL> urls;
    private final ProgressService progressService;
    private final int threadPoolSize;
    private final int currentDepth;
    private final int maxDepth;

    public ConcurrentUrlService(final List<URL> urls,
                                final int threadPoolSize,
                                final int currentDepth,
                                final int maxDepth,
                                final ProgressService progressService) {
        this.urls = urls;
        this.threadPoolSize = threadPoolSize;
        this.currentDepth = currentDepth;
        this.maxDepth = maxDepth;
        this.progressService = progressService;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected DocumentListWithMemo call() {
        LogDirectoryHelper.manage();
        logger.debug("Calling all urls");
        final List<UrlServiceCallable> tasks = urls.stream().map(url -> {
            final UrlServiceCallable task = new UrlServiceCallable(url, progressService);
            task.setOnSucceeded(this::onMultiDocumentsRetrieved);
            return task;
        }).collect(Collectors.toList());
        final ExecutorService executorService = Executors.newFixedThreadPool(threadPoolSize);
        final List<Future<Document>> futures = tasks.parallelStream()
                .map(documentTask -> (Future<Document>) executorService.submit(documentTask))
                .collect(Collectors.toList());
        final List<Document> documents = futures.stream()
                .map(future -> Unchecked.function(a -> future.get()).apply(null))
                .collect(Collectors.toList());
        final DocumentListWithMemo memo = new DocumentListWithMemo();
        memo.setDocuments(documents);
        memo.setThreadPoolSize(threadPoolSize);
        memo.setCurrentDepth(currentDepth);
        memo.setMaxDepth(maxDepth);
        return memo;
    }

    @SuppressWarnings("WeakerAccess")
    void onMultiDocumentsRetrieved(final WorkerStateEvent workerStateEvent) {
        logger.debug("Multi documents retrieved");
        Unchecked.supplier(() -> this.get().getDocuments().add((Document) workerStateEvent.getSource().getValue())).get();
    }

}
