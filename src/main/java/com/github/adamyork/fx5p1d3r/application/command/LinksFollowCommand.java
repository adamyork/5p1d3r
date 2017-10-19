package com.github.adamyork.fx5p1d3r.application.command;

import com.github.adamyork.fx5p1d3r.application.view.query.cell.DOMQuery;
import com.github.adamyork.fx5p1d3r.common.OutputManager;
import com.github.adamyork.fx5p1d3r.common.command.ApplicationCommand;
import com.github.adamyork.fx5p1d3r.common.command.CommandMap;
import com.github.adamyork.fx5p1d3r.common.command.ParserCommand;
import com.github.adamyork.fx5p1d3r.common.model.ApplicationFormState;
import com.github.adamyork.fx5p1d3r.common.model.OutputFileType;
import com.github.adamyork.fx5p1d3r.common.service.AlertService;
import com.github.adamyork.fx5p1d3r.common.service.ConcurrentURLService;
import com.github.adamyork.fx5p1d3r.common.service.URLServiceFactory;
import com.github.adamyork.fx5p1d3r.common.service.progress.ProgressService;
import com.github.adamyork.fx5p1d3r.common.service.progress.ProgressType;
import javafx.collections.ObservableList;
import javafx.concurrent.WorkerStateEvent;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;

/**
 * Created by Adam York on 2/28/2017.
 * Copyright 2017
 */
@Component
public class LinksFollowCommand implements ApplicationCommand {

    private final ApplicationFormState applicationFormState;
    private final URLServiceFactory urlServiceFactory;
    private final CommandMap<OutputFileType, ParserCommand> parserCommandMap;
    private final OutputManager outputManager;
    private final ProgressService progressService;
    private final AlertService alertService;

    private int currentDepth;
    private ExecutorService executorService;
    private int maxDepth;
    private int threadPoolSize;

    @Autowired
    public LinksFollowCommand(final ApplicationFormState applicationFormState,
                              final URLServiceFactory urlServiceFactory,
                              @Qualifier("ParserCommandMap") final CommandMap<OutputFileType, ParserCommand> parserCommandMap,
                              final OutputManager outputManager,
                              final ProgressService progressService,
                              final AlertService alertService) {
        this.applicationFormState = applicationFormState;
        this.urlServiceFactory = urlServiceFactory;
        this.parserCommandMap = parserCommandMap;
        this.outputManager = outputManager;
        this.progressService = progressService;
        this.alertService = alertService;
    }

    @Override
    public void execute() {
        //no-op
    }

    @Override
    public void execute(final List<URL> urls) {
        progressService.updateProgress(ProgressType.LINKS);
        threadPoolSize = Integer.parseInt(applicationFormState.getMultiThreadMax().toString());
        executorService = Executors.newFixedThreadPool(threadPoolSize);
        execute(urls, executorService);
    }

    @Override
    public void execute(final List<URL> urls, final ExecutorService executorService) {
        progressService.updateProgress(ProgressType.LINKS);
        this.executorService = executorService;
        currentDepth++;
        maxDepth = applicationFormState.getFollowLinksDepth().getValue();
        final List<URL> filtered = filterByRegex(urls);
        final ConcurrentURLService concurrentURLService = urlServiceFactory.getConcurrentServiceForURLs(filtered, threadPoolSize);
        executorService.submit(concurrentURLService);
        concurrentURLService.setOnSucceeded(this::onDocumentsRetrieved);
    }

    @SuppressWarnings("unchecked")
    private void onDocumentsRetrieved(final WorkerStateEvent workerStateEvent) {
        final List<Document> documents = (List<Document>) workerStateEvent.getSource().getValue();
        final ObservableList<DOMQuery> domQueryObservableList = applicationFormState.getDomQueryObservableList();
        final List<List<URL>> allLinks = new ArrayList<>();
        if (documents.size() == 0) {
            alertService.warn("No Documents.", "No documents found at link depth " + currentDepth + ". Output maybe empty.");
        }
        documents.forEach(document -> {
            final List<Elements> elementsList = new ArrayList<>();
            domQueryObservableList.forEach(domQuery -> {
                final String domQueryString = domQuery.getQuery();
                final Elements parsedElements = parserCommandMap.getCommand(applicationFormState.getOutputFileType())
                        .execute(document, domQueryString);
                elementsList.add(parsedElements);
            });
            final Elements linksElementsList = document.select("a");
            final List<URL> linksList = outputManager.getURLListFromElements(linksElementsList);
            allLinks.add(linksList);
        });
        if (currentDepth < maxDepth) {
            final List<URL> flattened = allLinks.stream().flatMap(List::stream).collect(Collectors.toList());
            execute(flattened, executorService);
        } else {
            currentDepth = 0;
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
            alertService.warn("Invalid Regex.", "Link follow regex is invalid.");
            exception.printStackTrace();
            return new ArrayList<>();
        }
    }
}
