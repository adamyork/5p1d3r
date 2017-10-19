package com.github.adamyork.fx5p1d3r.application.command;

import com.github.adamyork.fx5p1d3r.common.command.ApplicationCommand;
import com.github.adamyork.fx5p1d3r.common.model.ApplicationFormState;
import com.github.adamyork.fx5p1d3r.common.service.AlertService;
import com.github.adamyork.fx5p1d3r.common.service.progress.ProgressService;
import com.github.adamyork.fx5p1d3r.common.service.progress.ProgressType;
import org.apache.commons.io.FileUtils;
import org.jooq.lambda.Unchecked;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * Created by Adam York on 2/23/2017.
 * Copyright 2017
 */
@Component
@Qualifier("UrlListCommand")
public class UrlListCommand implements ApplicationCommand {

    protected final ApplicationFormState applicationFormState;
    protected final UrlValidatorCommand urlValidatorCommand;
    protected final ProgressService progressService;
    protected final AlertService alertService;

    @Autowired
    public UrlListCommand(final ApplicationFormState applicationFormState,
                          final UrlValidatorCommand urlValidatorCommand,
                          final ProgressService progressService,
                          final AlertService alertService) {
        this.applicationFormState = applicationFormState;
        this.urlValidatorCommand = urlValidatorCommand;
        this.progressService = progressService;
        this.alertService = alertService;
    }

    @Override
    public void execute() {
        final File urlListFile = applicationFormState.getUrlListFile();
        if (urlListFile == null) {
            alertService.error("No URL list selected.", "No URL list selected. Please select a URL list.");
            return;
        }
        final List<String> urlStrings = Unchecked.function(o -> FileUtils.readLines(urlListFile)).apply(null);
        progressService.updateSteps(urlStrings.size());
        progressService.updateProgress(ProgressType.START);
        urlValidatorCommand.execute(urlStrings);
    }

    @Override
    public void execute(final List<URL> urls) {

    }

    @Override
    public void execute(final List<URL> urls, final ExecutorService executorService) {

    }

}
