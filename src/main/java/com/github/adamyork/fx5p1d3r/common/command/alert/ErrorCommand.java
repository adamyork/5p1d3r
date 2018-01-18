package com.github.adamyork.fx5p1d3r.common.command.alert;

import com.github.adamyork.fx5p1d3r.common.service.AlertService;
import javafx.scene.control.ButtonType;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.File;
import java.net.URL;
import java.util.List;

/**
 * Created by Adam York on 10/26/2017.
 * Copyright 2017
 */
@Component
public class ErrorCommand implements AlertCommand<ButtonType> {

    private final AlertService alertService;

    @Inject
    public ErrorCommand(final AlertService alertService) {
        this.alertService = alertService;
    }

    @Override
    public ButtonType execute(final String header, final String content) {
        return alertService.error(header, content).orElse(new ButtonType(""));
    }

    @Override
    public ButtonType execute(final String header, final String content, final List<URL> urls) {
        return null;
    }

    @Override
    public ButtonType execute(final String header, final String content, final File file) {
        return null;
    }
}

