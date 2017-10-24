package com.github.adamyork.fx5p1d3r.common.command;

import com.github.adamyork.fx5p1d3r.common.service.AlertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.URL;
import java.util.List;

/**
 * Created by Adam York on 10/24/2017.
 * Copyright 2017
 */
@Component
public class WarnCommand implements AlertCommand<Void> {

    private final AlertService alertService;

    @Autowired
    public WarnCommand(final AlertService alertService) {
        this.alertService = alertService;
    }

    @Override
    public Void execute(final String header, final String content) {
        alertService.warn(header, content);
        return null;
    }

    @Override
    public Void execute(final String header, final String content, final List<URL> urls) {
        return null;
    }

    @Override
    public Void execute(final String header, final String content, final File file) {
        return null;
    }
}
