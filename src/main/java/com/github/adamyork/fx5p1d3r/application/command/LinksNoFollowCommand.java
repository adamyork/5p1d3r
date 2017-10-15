package com.github.adamyork.fx5p1d3r.application.command;

import com.github.adamyork.fx5p1d3r.common.command.ApplicationCommand;
import com.github.adamyork.fx5p1d3r.common.service.progress.ProgressService;
import com.github.adamyork.fx5p1d3r.common.service.progress.ProgressType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * Created by Adam York on 2/28/2017.
 * Copyright 2017
 */
@Component
public class LinksNoFollowCommand implements ApplicationCommand {

    private final ProgressService progressService;

    @Autowired
    public LinksNoFollowCommand(final ProgressService progressService) {
        this.progressService = progressService;
    }

    @Override
    public void execute() {
        progressService.updateProgress(ProgressType.COMPLETE);
    }

    @Override
    public void execute(final List<URL> urls) {
        progressService.updateProgress(ProgressType.COMPLETE);
    }

    @Override
    public void execute(final List<URL> urls, final ExecutorService executorService) {
        progressService.updateProgress(ProgressType.COMPLETE);
    }

}
