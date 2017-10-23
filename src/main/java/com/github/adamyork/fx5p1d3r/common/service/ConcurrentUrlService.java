package com.github.adamyork.fx5p1d3r.common.service;

import com.github.adamyork.fx5p1d3r.common.command.DocumentRetrieveHandler;
import com.github.adamyork.fx5p1d3r.common.service.progress.ProgressService;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import org.jooq.lambda.Unchecked;
import org.jsoup.nodes.Document;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * Created by Adam York on 2/24/2017.
 * Copyright 2017
 */
public class ConcurrentUrlService extends Task<List<Document>> {

    private final List<URL> urls;
    private final ProgressService progressService;
    private DocumentRetrieveHandler handler;
    private int threadPoolSize;
    private int total = 0;
    private List<Document> documents;

    public ConcurrentUrlService(final List<URL> urls,
                                final int threadPoolSize,
                                final ProgressService progressService) {
        this.urls = urls;
        this.threadPoolSize = threadPoolSize;
        this.progressService = progressService;
    }

    @Override
    protected List<Document> call() throws Exception {
        documents = new ArrayList<>();
        final List<UrlServiceCallable> tasks = urls.stream().map(url -> {
            final UrlServiceCallable task = new UrlServiceCallable(url, progressService);
            task.setOnSucceeded(this::onMultiDocumentsRetrieved);
            return task;
        }).collect(Collectors.toList());
        final ExecutorService executorService = Executors.newFixedThreadPool(threadPoolSize);
        final List<Future<Document>> futures = tasks.parallelStream().map(documentTask -> (Future<Document>) executorService.submit(documentTask)).collect(Collectors.toList());
        final List<Document> documents = futures.stream().map(future -> Unchecked.function(a -> future.get()).apply(null)).collect(Collectors.toList());
        return documents;
    }

    public void setCallbackObject(final DocumentRetrieveHandler handler) {
        this.handler = handler;
    }

    void onMultiDocumentsRetrieved(final WorkerStateEvent workerStateEvent) {
        total++;
        documents.add((Document) workerStateEvent.getSource().getValue());
        //TODO COMMAND
        if (total == urls.size()) {
            total = 0;
            handler.onDocumentsRetrieved(documents);
        }
    }

}
