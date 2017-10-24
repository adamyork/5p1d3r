package com.github.adamyork.fx5p1d3r.application.command;

import com.github.adamyork.fx5p1d3r.common.command.AlertCommand;
import com.github.adamyork.fx5p1d3r.common.service.progress.ProgressService;
import com.github.adamyork.fx5p1d3r.common.service.progress.ProgressType;
import org.apache.commons.io.FileUtils;
import org.jooq.lambda.Unchecked;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.URL;
import java.util.List;

/**
 * Created by Adam York on 10/26/2017.
 * Copyright 2017
 */
@Component
public class UrlListSelectedCommand implements AlertCommand<Void> {

    protected final UrlValidatorCommand urlValidatorCommand;
    protected final ProgressService progressService;

    @Autowired
    public UrlListSelectedCommand(final UrlValidatorCommand urlValidatorCommand,
                                  final ProgressService progressService) {
        this.urlValidatorCommand = urlValidatorCommand;
        this.progressService = progressService;
    }

    @Override
    public Void execute(final String header, final String content) {
        return null;
    }

    @Override
    public Void execute(final String header, final String content, final List<URL> urls) {
        return null;
    }

    @Override
    public Void execute(final String header, final String content, final File file) {
        final List<String> urlStrings = Unchecked.function(o -> FileUtils.readLines(file)).apply(null);
        progressService.updateSteps(urlStrings.size());
        progressService.updateProgress(ProgressType.START);
        urlValidatorCommand.execute(urlStrings);
        return null;
    }
}
