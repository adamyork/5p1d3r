package com.github.adamyork.fx5p1d3r.application.command;

import com.github.adamyork.fx5p1d3r.common.command.AlertCommand;
import com.github.adamyork.fx5p1d3r.common.command.ApplicationCommand;
import com.github.adamyork.fx5p1d3r.common.command.CommandMap;
import com.github.adamyork.fx5p1d3r.common.model.ApplicationFormState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;

/**
 * Created by Adam York on 2/23/2017.
 * Copyright 2017
 */
@Component
@Qualifier("UrlListCommand")
public class UrlListCommand implements ApplicationCommand {

    protected final ApplicationFormState applicationFormState;
    protected final CommandMap<Boolean, AlertCommand> urlListSelectedCommandMap;
    private final MessageSource messageSource;

    @Inject
    public UrlListCommand(final ApplicationFormState applicationFormState,
                          final MessageSource messageSource,
                          @Qualifier("UrlListSelectedCommandMap") final CommandMap<Boolean, AlertCommand> urlListSelectedCommandMap) {
        this.applicationFormState = applicationFormState;
        this.urlListSelectedCommandMap = urlListSelectedCommandMap;
        this.messageSource = messageSource;
    }

    @Override
    public void execute() {
        final File urlListFile = applicationFormState.getUrlListFile();
        urlListSelectedCommandMap.getCommand(urlListFile == null)
                .execute(messageSource.getMessage("error.no.url.list.header", null, Locale.getDefault()),
                        messageSource.getMessage("error.no.url.list.content", null, Locale.getDefault()));
    }

    @Override
    public void execute(final List<URL> urls) {

    }

    @Override
    public void execute(final List<URL> urls, final ExecutorService executorService) {

    }

}
