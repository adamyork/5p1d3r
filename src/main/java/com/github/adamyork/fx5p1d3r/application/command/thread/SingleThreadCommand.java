package com.github.adamyork.fx5p1d3r.application.command.thread;

import com.github.adamyork.fx5p1d3r.application.view.query.cell.DomQuery;
import com.github.adamyork.fx5p1d3r.common.OutputManager;
import com.github.adamyork.fx5p1d3r.common.command.ApplicationCommand;
import com.github.adamyork.fx5p1d3r.common.command.CommandMap;
import com.github.adamyork.fx5p1d3r.common.command.alert.AlertCommand;
import com.github.adamyork.fx5p1d3r.common.command.io.ExecutorCommand;
import com.github.adamyork.fx5p1d3r.common.command.io.ParserCommand;
import com.github.adamyork.fx5p1d3r.common.model.ApplicationFormState;
import com.github.adamyork.fx5p1d3r.common.model.OutputFileType;
import com.github.adamyork.fx5p1d3r.common.service.AbortService;
import com.github.adamyork.fx5p1d3r.common.service.ThrottledUrlService;
import com.github.adamyork.fx5p1d3r.common.service.UrlServiceFactory;
import javafx.collections.ObservableList;
import javafx.concurrent.WorkerStateEvent;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Adam York on 2/28/2017.
 * Copyright 2017
 */
@Component
public class SingleThreadCommand implements ApplicationCommand, Observer {

    private final CommandMap<Boolean, ApplicationCommand> followLinksCommandMap;
    private final CommandMap<OutputFileType, ParserCommand> parserCommandMap;
    private final CommandMap<Boolean, AlertCommand> warnCommandMap;
    private final CommandMap<Boolean, ExecutorCommand> executorCommandMap;
    private final ApplicationFormState applicationFormState;
    private final OutputManager outputManager;
    private final AbortService abortService;
    private final UrlServiceFactory urlServiceFactory;
    private final MessageSource messageSource;

    private ExecutorService executorService;

    @Inject
    public SingleThreadCommand(@Qualifier("FollowLinksCommandMap") final CommandMap<Boolean, ApplicationCommand> followLinksCommandMap,
                               @Qualifier("ParserCommandMap") final CommandMap<OutputFileType, ParserCommand> parserCommandMap,
                               @Qualifier("WarnCommandMap") final CommandMap<Boolean, AlertCommand> warnCommandMap,
                               @Qualifier("ExecutorCleanUpCommandMap") final CommandMap<Boolean, ExecutorCommand> executorCommandMap,
                               final UrlServiceFactory urlServiceFactory,
                               final ApplicationFormState applicationFormState,
                               final OutputManager outputManager,
                               final AbortService abortService,
                               final MessageSource messageSource) {
        this.followLinksCommandMap = followLinksCommandMap;
        this.parserCommandMap = parserCommandMap;
        this.warnCommandMap = warnCommandMap;
        this.executorCommandMap = executorCommandMap;
        this.urlServiceFactory = urlServiceFactory;
        this.applicationFormState = applicationFormState;
        this.outputManager = outputManager;
        this.abortService = abortService;
        this.messageSource = messageSource;
    }

    @Override
    @SuppressWarnings("EmptyMethod")
    public void execute() {
        //no-op
    }

    @Override
    public void execute(final List<URL> urls) {
        executorService = Executors.newFixedThreadPool(1);
        abortService.addObserver(this);
        final ThrottledUrlService throttledUrlService = urlServiceFactory.getThrottledServiceForUrls(urls);
        throttledUrlService.setOnSucceeded(this::onDocumentsRetrieved);
        executorService.submit(throttledUrlService);
    }

    @Override
    @SuppressWarnings("EmptyMethod")
    public void execute(final List<URL> urls,
                        final ExecutorService executorService,
                        final int currentDepth,
                        final int maxDepth,
                        final int threadPoolsSize) {
        //no-op
    }


    @SuppressWarnings({"unchecked", "Duplicates", "WeakerAccess"})
    void onDocumentsRetrieved(final WorkerStateEvent workerStateEvent) {
        final List<Document> documents = (List<Document>) workerStateEvent.getSource().getValue();
        final ObservableList<DomQuery> domQueryObservableList = applicationFormState.getDomQueryObservableList();
        warnCommandMap.getCommand(documents.size() == 0)
                .execute(messageSource.getMessage("alert.no.documents.header", null, Locale.getDefault()),
                        messageSource.getMessage("alert.no.documents.content", null, Locale.getDefault()));
        documents.forEach(document -> {
            domQueryObservableList.forEach(domQuery -> {
                final String domQueryString = domQuery.getQuery();
                parserCommandMap.getCommand(applicationFormState.getOutputFileType()).execute(document, domQueryString);
            });
            final Elements linksElementsList = document.select("a");
            final List<URL> linksList = outputManager.getUrlListFromElements(linksElementsList);
            followLinksCommandMap.getCommand(applicationFormState.followLinks()).execute(linksList, executorService, 1,
                    applicationFormState.getFollowLinksDepth().getValue(),
                    Integer.parseInt(applicationFormState.getMultiThreadMax().toString()) - 1);
        });
        abortService.deleteObserver(this);
    }

    @Override
    public void update(final Observable observable, final Object arg) {
        executorCommandMap.getCommand(executorService != null).execute(executorService);
    }
}
