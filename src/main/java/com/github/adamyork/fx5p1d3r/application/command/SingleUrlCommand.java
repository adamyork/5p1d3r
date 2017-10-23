package com.github.adamyork.fx5p1d3r.application.command;

import com.github.adamyork.fx5p1d3r.common.command.ApplicationCommand;
import com.github.adamyork.fx5p1d3r.common.model.ApplicationFormState;
import com.github.adamyork.fx5p1d3r.common.service.progress.ProgressService;
import com.github.adamyork.fx5p1d3r.common.service.progress.ProgressType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * Created by Adam York on 2/23/2017.
 * Copyright 2017
 */
@Component
public class SingleUrlCommand implements ApplicationCommand {

    private final ApplicationFormState applicationFormState;
    private final UrlValidatorCommand urlValidatorCommand;
    private final ProgressService progressService;

    @Autowired
    public SingleUrlCommand(final ApplicationFormState applicationFormState,
                            final UrlValidatorCommand urlValidatorCommand,
                            final ProgressService progressService) {
        this.applicationFormState = applicationFormState;
        this.urlValidatorCommand = urlValidatorCommand;
        this.progressService = progressService;
    }

    @Override
    public void execute() {
        final List<String> urlStrings = new ArrayList<>();
        urlStrings.add(applicationFormState.getStartingUrl());
        progressService.updateSteps(urlStrings.size());
        progressService.updateProgress(ProgressType.START);
        urlValidatorCommand.execute(urlStrings);
    }

    @Override
    public void execute(final List<URL> urls) {
        //no-op
    }

    @Override
    public void execute(final List<URL> urls, final ExecutorService executorService) {
        //no-op
    }

}
