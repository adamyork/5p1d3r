package com.github.adamyork.fx5p1d3r.application.service.thread;

import com.github.adamyork.fx5p1d3r.application.service.io.DocumentParserService;
import com.github.adamyork.fx5p1d3r.common.service.OutputService;
import com.github.adamyork.fx5p1d3r.common.model.ApplicationFormState;
import com.github.adamyork.fx5p1d3r.common.model.DocumentListWithMemo;
import com.github.adamyork.fx5p1d3r.common.service.AbortService;
import com.github.adamyork.fx5p1d3r.common.service.AlertService;
import com.github.adamyork.fx5p1d3r.common.service.ConcurrentUrlService;
import com.github.adamyork.fx5p1d3r.common.service.UrlServiceFactory;
import com.github.adamyork.fx5p1d3r.common.service.progress.ProgressService;
import javafx.concurrent.WorkerStateEvent;
import org.jsoup.nodes.Document;
import org.springframework.context.MessageSource;

import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * Created by Adam York on 2/28/2017.
 * Copyright 2017
 */
public class MultiThreadService extends BaseThreadService {

    public MultiThreadService(final UrlServiceFactory urlServiceFactory,
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
        final int threadPoolSize = Integer.parseInt(applicationFormState.getMultiThreadMax().toString());
        final ConcurrentUrlService concurrentUrlService = urlServiceFactory.getConcurrentServiceForUrls(urls, 1,
                applicationFormState.getFollowLinksDepth().getValue(), threadPoolSize - 1);
        concurrentUrlService.setOnSucceeded(this::onDocumentsRetrieved);
        executorService.submit(concurrentUrlService);
    }

    public void onDocumentsRetrieved(final WorkerStateEvent workerStateEvent) {
        final DocumentListWithMemo memo = (DocumentListWithMemo) workerStateEvent.getSource().getValue();
        final List<Document> documents = memo.getDocuments().stream()
                .filter(Objects::nonNull).collect(Collectors.toList());
        processDocuments(documents);
    }

}