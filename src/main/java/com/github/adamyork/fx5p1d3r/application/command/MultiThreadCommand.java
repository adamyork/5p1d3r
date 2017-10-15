package com.github.adamyork.fx5p1d3r.application.command;

import com.github.adamyork.fx5p1d3r.application.view.query.cell.DOMQuery;
import com.github.adamyork.fx5p1d3r.common.OutputManager;
import com.github.adamyork.fx5p1d3r.common.command.ApplicationCommand;
import com.github.adamyork.fx5p1d3r.common.command.CommandMap;
import com.github.adamyork.fx5p1d3r.common.command.ParserCommand;
import com.github.adamyork.fx5p1d3r.common.model.ApplicationFormState;
import com.github.adamyork.fx5p1d3r.common.model.OutputFileType;
import com.github.adamyork.fx5p1d3r.common.service.AbortService;
import com.github.adamyork.fx5p1d3r.common.service.ConcurrentURLService;
import com.github.adamyork.fx5p1d3r.common.service.URLServiceFactory;
import com.github.adamyork.fx5p1d3r.common.service.progress.ProgressService;
import com.github.adamyork.fx5p1d3r.common.service.progress.ProgressType;
import javafx.collections.ObservableList;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Adam York on 2/28/2017.
 * Copyright 2017
 */
@Component
public class MultiThreadCommand implements ApplicationCommand, Observer {

    private final ApplicationFormState applicationFormState;
    private final OutputManager outputManager;
    private final ProgressService progressService;
    private final AbortService abortService;
    private final URLServiceFactory urlServiceFactory;
    private final CommandMap<Boolean, ApplicationCommand> followLinksCommandMap;
    private final CommandMap<OutputFileType, ParserCommand> parserCommandMap;

    protected ExecutorService executorService;

    @Autowired
    public MultiThreadCommand(final URLServiceFactory urlServiceFactory,
                              final ApplicationFormState applicationFormState,
                              final OutputManager outputManager,
                              final ProgressService progressService,
                              final AbortService abortService,
                              @Qualifier("FollowLinksCommandMap") final CommandMap<Boolean, ApplicationCommand> followLinksCommandMap,
                              @Qualifier("ParserCommandMap") final CommandMap<OutputFileType, ParserCommand> parserCommandMap) {
        this.urlServiceFactory = urlServiceFactory;
        this.applicationFormState = applicationFormState;
        this.outputManager = outputManager;
        this.progressService = progressService;
        this.abortService = abortService;
        this.followLinksCommandMap = followLinksCommandMap;
        this.parserCommandMap = parserCommandMap;
    }

    @Override
    public void execute() {
        //no-op
    }

    @Override
    public void execute(final List<URL> urls) {
        final int threadPoolSize = Integer.parseInt(applicationFormState.getMultiThreadMax().toString());
        executorService = Executors.newFixedThreadPool(1);
        final ConcurrentURLService concurrentURLService = urlServiceFactory.getConcurrentServiceForURLs(urls, threadPoolSize - 1);
        concurrentURLService.setCallbackObject(this);
        executorService.submit(concurrentURLService);
    }

    @Override
    public void execute(final List<URL> urls, final ExecutorService executorService) {
        //no-op
    }

    @SuppressWarnings({"unchecked", "Duplicates"})
    public void onDocumentsRetrieved(final List<Document> documents) {
        final ObservableList<DOMQuery> domQueryObservableList = applicationFormState.getDomQueryObservableList();
        if (documents.size() == 0) {
            //TODO throw alert no documents retrieved
            progressService.updateProgress(ProgressType.COMPLETE);
        }
        documents.forEach(document -> {
            domQueryObservableList.forEach(domQuery -> {
                final String domQueryString = domQuery.getQuery();
                parserCommandMap.getCommand(applicationFormState.getOutputFileType()).execute(document, domQueryString);
            });
            final Elements linksElementsList = document.select("a");
            final List<URL> linksList = outputManager.getURLListFromElements(linksElementsList);
            followLinksCommandMap.getCommand(applicationFormState.followLinks()).execute(linksList, executorService);
        });
        abortService.deleteObserver(this);
    }

    @Override
    public void update(final Observable observable, final Object arg) {
        if (executorService != null) {
            executorService.shutdown();
            abortService.clear();
        }
    }
}