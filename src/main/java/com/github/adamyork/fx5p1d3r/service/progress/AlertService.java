package com.github.adamyork.fx5p1d3r.service.progress;

import javafx.scene.control.Alert;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Locale;

/**
 * Created by Adam York on 10/18/2017.
 * Copyright 2017
 */
@Component
public class AlertService {

    private final MessageSource messageSource;
    private boolean warningShown = false;

    @Inject
    public AlertService(final MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public void error(final String header, final String content) {
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
            alert.setOnHidden(event -> warningShown = false);
            warningShown = true;
        }
    }

}
