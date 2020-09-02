package com.github.adamyork.fx5p1d3r.application.service.thread;

import com.github.adamyork.fx5p1d3r.application.service.io.DocumentParserService;
import com.github.adamyork.fx5p1d3r.application.view.query.cell.DomQuery;
import com.github.adamyork.fx5p1d3r.common.model.ApplicationFormState;
import com.github.adamyork.fx5p1d3r.common.model.DocumentListWithMemo;
import com.github.adamyork.fx5p1d3r.common.service.AlertService;
import com.github.adamyork.fx5p1d3r.common.service.ConcurrentUrlService;
import com.github.adamyork.fx5p1d3r.common.service.OutputService;
import com.github.adamyork.fx5p1d3r.common.service.UrlServiceFactory;
import com.github.adamyork.fx5p1d3r.common.service.progress.ProgressService;
import com.github.adamyork.fx5p1d3r.common.service.progress.ProgressType;
import javafx.collections.ObservableList;
import javafx.concurrent.WorkerStateEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.context.MessageSource;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;

/**
 * Created by Adam York on 2/28/2017.
 * Copyright 2017
 */
public class RecursiveLinksFollower extends BaseObservableProcessor implements LinksFollower {

    private static final Logger logger = LogManager.getLogger(RecursiveLinksFollower.class);

    private final ApplicationFormState applicationFormState;
    private final UrlServiceFactory urlServiceFactory;
    private final OutputService outputService;
    private final ProgressService progressService;
    private final AlertService alertService;
    private final MessageSource messageSource;
    private final DocumentParserService jsonDocumentParser;
    private final DocumentParserService csvDocumentParser;

    private ExecutorService executorService;

    public RecursiveLinksFollower(final ApplicationFormState applicationFormState,
                                  final UrlServiceFactory urlServiceFactory,
                                  final OutputService outputService,
                                  final ProgressService progressService,
                                  final AlertService alertService,
                                  final MessageSource messageSource,
                                  final DocumentParserService jsonDocumentParser,
                                  final DocumentParserService csvDocumentParser) {
        this.applicationFormState = applicationFormState;
        this.urlServiceFactory = urlServiceFactory;
        this.outputService = outputService;
        this.progressService = progressService;
        this.alertService = alertService;
        this.messageSource = messageSource;
        this.jsonDocumentParser = jsonDocumentParser;
        this.csvDocumentParser = csvDocumentParser;
    }

    @Override
    public void traverse(final List<URL> urls,
                         final ExecutorService executorService,
                         final int currentDepth,
                         final int maxDepth,
                         final int threadPoolSize) {
        progressService.updateProgress(ProgressType.LINKS);
        this.executorService = executorService;
        final List<URL> filtered = filterByRegex(urls);
        logger.debug("Following linked documents " + filtered);
        final ConcurrentUrlService concurrentUrlService = urlServiceFactory.getConcurrentServiceForUrls(filtered, currentDepth,
                maxDepth, threadPoolSize);
        concurrentUrlService.setOnSucceeded(this::onDocumentsRetrieved);
        executorService.submit(concurrentUrlService);
    }

    @SuppressWarnings("DuplicatedCode")
    public void onDocumentsRetrieved(final WorkerStateEvent workerStateEvent) {
        logger.debug("Document retrieved from link follow");
        final DocumentListWithMemo memo = (DocumentListWithMemo) workerStateEvent.getSource().getValue();
        final List<Document> documents = memo.getDocuments().stream()
                .filter(Objects::nonNull).collect(Collectors.toList());
        final ObservableList<DomQuery> domQueryObservableList = getDomQueryList(applicationFormState);
        if (documents.size() == 0) {
            final String header = messageSource.getMessage("alert.no.documents.header", null, Locale.getDefault());
            final String content = messageSource.getMessage("alert.no.documents.content", null, Locale.getDefault());
            alertService.warn(header, content);
            logger.debug("No documents to process");
        }
        final List<List<URL>> allLinks = documents.stream().map(doc -> {
            parseQueries(domQueryObservableList, applicationFormState, jsonDocumentParser, csvDocumentParser, doc);
            final Elements linksElementsList = doc.select("a");
            return outputService.getUrlListFromElements(linksElementsList);
        }).collect(Collectors.toList());
        final List<URL> flattened = allLinks.stream().flatMap(List::stream).collect(Collectors.toList());
        if (memo.getCurrentDepth() < memo.getMaxDepth()) {
            logger.debug("current depth " + memo.getCurrentDepth() + " is not max depth " + memo.getMaxDepth() + "; recurse");
            traverse(flattened, executorService, memo.getCurrentDepth() + 1, memo.getMaxDepth(), memo.getThreadPoolSize());
        } else {
            logger.debug("Document retrieved from link follow");
            progressService.updateProgress(ProgressType.COMPLETE);
        }
    }

    private List<URL> filterByRegex(final List<URL> urls) {
        try {
            final Pattern pattern = Pattern.compile(applicationFormState.getLinkFollowPattern());
            return urls.stream().filter(url -> {
                final String urlString = url.toString();
                final Matcher matcher = pattern.matcher(urlString);
                return matcher.matches();
            }).collect(Collectors.toList());
        } catch (final PatternSyntaxException exception) {
            alertService.warn(messageSource.getMessage("alert.invalid.regex.header", null, Locale.getDefault()),
                    messageSource.getMessage("alert.invalid.regex.content", null, Locale.getDefault()));
            return new ArrayList<>();
        }
    }
}
