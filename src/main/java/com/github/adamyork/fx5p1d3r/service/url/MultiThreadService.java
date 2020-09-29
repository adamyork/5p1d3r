package com.github.adamyork.fx5p1d3r.service.url;

import com.github.adamyork.fx5p1d3r.ApplicationFormState;
import com.github.adamyork.fx5p1d3r.service.output.CsvOutputTask;
import com.github.adamyork.fx5p1d3r.service.output.JsonOutputTask;
import com.github.adamyork.fx5p1d3r.service.output.data.OutputFileType;
import com.github.adamyork.fx5p1d3r.service.parse.DocumentParserService;
import com.github.adamyork.fx5p1d3r.service.progress.AlertService;
import com.github.adamyork.fx5p1d3r.service.progress.ApplicationProgressService;
import com.github.adamyork.fx5p1d3r.service.progress.ProgressType;
import com.github.adamyork.fx5p1d3r.service.transform.TransformService;
import com.github.adamyork.fx5p1d3r.service.url.data.DocumentListWithMemo;
import javafx.concurrent.WorkerStateEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jooq.lambda.tuple.Tuple3;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
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
public class MultiThreadService extends BaseThreadService implements ThreadService {

    private static final Logger logger = LogManager.getLogger(MultiThreadService.class);

    private int total;
    private int count;
    private List<URL> linksList;

    public MultiThreadService(final UrlServiceFactory urlServiceFactory,
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
        final int threadPoolSize = Integer.parseInt(applicationFormState.getMultiThreadMax().toString());
        final ConcurrentUrlTask concurrentUrlTask = urlServiceFactory.getConcurrentServiceForUrls(urls, 1,
                urls.size(), threadPoolSize - 1);
        concurrentUrlTask.setOnSucceeded(this::onDocumentsRetrieved);
        logger.info("Submitting url list work");
        executorService.submit(concurrentUrlTask);
    }

    public void onDocumentsRetrieved(final WorkerStateEvent workerStateEvent) {
        final DocumentListWithMemo memo = (DocumentListWithMemo) workerStateEvent.getSource().getValue();
        final List<Document> documents = memo.getDocuments().stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        logger.info(documents.size() + " documents retrieved");
        final List<Tuple3<List<Elements>, Document, List<URL>>> processed = process(documents);
        final List<Object> transformed = processed.stream()
                .flatMap(object -> transform(object.v1, object.v2).stream())
                .collect(Collectors.toList());
        transformed.forEach(result -> {
            if (applicationFormState.getOutputFileType().equals(OutputFileType.JSON)) {
                final JsonOutputTask outputTask = new JsonOutputTask(applicationFormState, progressService, result);
                outputTask.setOnSucceeded(this::onResultWritten);
                executorService.submit(outputTask);
            } else {
                final CsvOutputTask outputTask = new CsvOutputTask(applicationFormState, progressService, (String[]) result);
                outputTask.setOnSucceeded(this::onResultWritten);
                executorService.submit(outputTask);
            }
        });
    }

    void onResultWritten(final WorkerStateEvent workerStateEvent) {
        count++;
        if (total == count) {
            if (progressService.getCurrentProgressType().equals(ProgressType.ABORT)) {
                logger.debug("Link following Aborted.");
                logger.debug("Crawl completed");
                progressService.updateProgress(ProgressType.COMPLETE);
            }
            if (applicationFormState.followLinks()) {
                logger.debug("Link following enabled.");
                linksFollower.traverse(linksList, executorService, 1,
                        applicationFormState.getFollowLinksDepth().getValue(),
                        Integer.parseInt(applicationFormState.getMultiThreadMax().toString()) - 1);
            } else {
                logger.debug("Link following disabled.");
                logger.debug("Crawl completed");
                progressService.updateProgress(ProgressType.COMPLETE);
            }
        }
    }

}