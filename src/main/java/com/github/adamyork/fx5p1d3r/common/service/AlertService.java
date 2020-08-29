package com.github.adamyork.fx5p1d3r.common.service;

import com.github.adamyork.fx5p1d3r.common.service.progress.ProgressService;
import com.github.adamyork.fx5p1d3r.common.service.progress.ProgressType;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogEvent;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Locale;
import java.util.function.Function;

/**
 * Created by Adam York on 10/18/2017.
 * Copyright 2017
 */
@Component
public class AlertService {

    private final ProgressService progressService;
    private final MessageSource messageSource;
    private boolean warningShown = false;

    @Inject
    public AlertService(final ProgressService progressService,
                        final MessageSource messageSource) {
        this.progressService = progressService;
        this.messageSource = messageSource;
    }

    public void error(final String header, final String content) {
        progressService.updateSteps(0);
        progressService.updateProgress(ProgressType.ABORT);
        final Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(messageSource.getMessage("alert.service.error.label", null, Locale.getDefault()));
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void warn(final String header, final String content) {
        if (!warningShown) {
            final Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle(messageSource.getMessage("alert.service.warning.label", null, Locale.getDefault()));
            alert.setHeaderText(header);
            alert.setContentText(content);
            alert.show();
            warningShown = true;
        }
    }

    private Function<Alert, Alert> getHandlerFunction() {
        return alert -> {
            alert.setOnHidden(this::handleOnHidden);
            return alert;
        };
    }

    private void handleOnHidden(final DialogEvent dialogEvent) {
        warningShown = false;
    }
}
