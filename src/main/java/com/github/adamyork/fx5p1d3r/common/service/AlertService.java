package com.github.adamyork.fx5p1d3r.common.service;

import com.github.adamyork.fx5p1d3r.common.service.progress.ProgressService;
import com.github.adamyork.fx5p1d3r.common.service.progress.ProgressType;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Optional;

/**
 * Created by Adam York on 10/18/2017.
 * Copyright 2017
 */
@Component
public class AlertService {

    private final ProgressService progressService;
    private final MessageSource messageSource;
    private boolean warningShown = false;

    @Autowired
    public AlertService(final ProgressService progressService,
                        final MessageSource messageSource) {
        this.progressService = progressService;
        this.messageSource = messageSource;
    }

    public Optional<ButtonType> error(final String header, final String content) {
        progressService.updateSteps(0);
        progressService.updateProgress(ProgressType.ABORT);
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(messageSource.getMessage("alert.service.error.label", null, Locale.getDefault()));
        alert.setHeaderText(header);
        alert.setContentText(content);
        return alert.showAndWait();
    }

    public void warn(final String header, final String content) {
        //TODO command
        if (!warningShown) {
            warningShown = true;
            final Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle(messageSource.getMessage("alert.service.warning.label", null, Locale.getDefault()));
            alert.setHeaderText(header);
            alert.setContentText(content);
            alert.show();
            alert.setOnHidden(this::handleOnHidden);
        }
    }

    private void handleOnHidden(final DialogEvent dialogEvent) {
        warningShown = false;
    }
}
