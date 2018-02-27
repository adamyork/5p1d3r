package com.github.adamyork.fx5p1d3r.application.command.thread;

import com.github.adamyork.fx5p1d3r.application.view.query.cell.DomQuery;
import com.github.adamyork.fx5p1d3r.common.OutputManager;
import com.github.adamyork.fx5p1d3r.common.command.ApplicationCommand;
import com.github.adamyork.fx5p1d3r.common.command.CommandMap;
import com.github.adamyork.fx5p1d3r.common.command.alert.AlertCommand;
import com.github.adamyork.fx5p1d3r.common.command.io.ParserCommand;
import com.github.adamyork.fx5p1d3r.common.model.ApplicationFormState;
import com.github.adamyork.fx5p1d3r.common.model.DocumentListWithMemo;
import com.github.adamyork.fx5p1d3r.common.model.OutputFileType;
import com.github.adamyork.fx5p1d3r.common.service.AlertService;
import com.github.adamyork.fx5p1d3r.common.service.ConcurrentUrlService;
import com.github.adamyork.fx5p1d3r.common.service.UrlServiceFactory;
import com.github.adamyork.fx5p1d3r.common.service.progress.ProgressService;
import com.github.adamyork.fx5p1d3r.common.service.progress.ProgressType;
import javafx.collections.ObservableList;
import javafx.concurrent.WorkerStateEvent;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
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
@Component
public class LinksFollowCommand implements ApplicationCommand {

    private final CommandMap<OutputFileType, ParserCommand> parserCommandMap;
    private final CommandMap<Boolean, AlertCommand> warnCommandMap;
    private final CommandMap<Boolean, LinkRecursionCommand> linkRecursionCommandMap;
    private final ApplicationFormState applicationFormState;
    private final UrlServiceFactory urlServiceFactory;
    private final OutputManager outputManager;
    private final ProgressService progressService;
    private final AlertService alertService;
    private final MessageSource messageSource;

    private ExecutorService executorService;

    @Inject
    public LinksFollowCommand(@Qualifier("ParserCommandMap") final CommandMap<OutputFileType, ParserCommand> parserCommandMap,
                              @Qualifier("WarnCommandMap") final CommandMap<Boolean, AlertCommand> warnCommandMap,
                              @Qualifier("LinkRecursionCommandMap") final CommandMap<Boolean, LinkRecursionCommand> linkRecursionCommandMap,
                              final ApplicationFormState applicationFormState,
                              final UrlServiceFactory urlServiceFactory,
                              final OutputManager outputManager,
                              final ProgressService progressService,
                              final AlertService alertService,
                              final MessageSource messageSource) {
        this.parserCommandMap = parserCommandMap;
        this.warnCommandMap = warnCommandMap;
        this.linkRecursionCommandMap = linkRecursionCommandMap;
        this.applicationFormState = applicationFormState;
        this.urlServiceFactory = urlServiceFactory;
        this.outputManager = outputManager;
        this.progressService = progressService;
        this.alertService = alertService;
        this.messageSource = messageSource;
    }

    @Override
    public void execute() {
        //no-op
    }

    @Override
    public void execute(final List<URL> urls) {
    }

    @Override
    public void execute(final List<URL> urls, final ExecutorService executorService,
                        final int currentDepth,
                        final int maxDepth,
                        final int threadPoolSize) {
        progressService.updateProgress(ProgressType.LINKS);
        this.executorService = executorService;
        final List<URL> filtered = filterByRegex(urls);
        final ConcurrentUrlService concurrentUrlService = urlServiceFactory.getConcurrentServiceForUrls(filtered, 1,
                maxDepth, threadPoolSize);
        concurrentUrlService.setOnSucceeded(this::onDocumentsRetrieved);
        executorService.submit(concurrentUrlService);
    }

    @SuppressWarnings("unchecked")
    public void onDocumentsRetrieved(final WorkerStateEvent workerStateEvent) {
        final DocumentListWithMemo memo = (DocumentListWithMemo) workerStateEvent.getSource().getValue();
        final List<Document> documents = memo.getDocuments().stream()
                .filter(Objects::nonNull).collect(Collectors.toList());
        final ObservableList<DomQuery> domQueryObservableList = applicationFormState.getDomQueryObservableList();
        warnCommandMap.getCommand(documents.size() == 0)
                .execute(messageSource.getMessage("alert.no.documents.header", null, Locale.getDefault()),
                        messageSource.getMessage("alert.no.documents.content", null, Locale.getDefault()));
        final List<List<URL>> allLinks = documents.stream().map(doc -> {
            final Document document = doc;
            domQueryObservableList.forEach(domQuery -> {
                final String domQueryString = domQuery.getQuery();
                parserCommandMap.getCommand(applicationFormState.getOutputFileType()).execute(document, domQueryString);
            });
            final Elements linksElementsList = document.select("a");
            return outputManager.getUrlListFromElements(linksElementsList);
        }).collect(Collectors.toList());
        final List<URL> flattened = allLinks.stream().flatMap(List::stream).collect(Collectors.toList());
        linkRecursionCommandMap.getCommand(memo.getCurrentDepth() < memo.getMaxDepth()).execute(flattened, executorService,
                memo, progressService, this);
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
