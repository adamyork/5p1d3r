package com.github.adamyork.fx5p1d3r.common.service;

import com.github.adamyork.fx5p1d3r.common.service.progress.ProgressService;
import com.github.adamyork.fx5p1d3r.common.service.progress.ProgressType;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Created by Adam York on 10/18/2017.
 * Copyright 2017
 */
@Component
public class AlertService {

    private final ProgressService progressService;

    @Autowired
    public AlertService(final ProgressService progressService) {
        this.progressService = progressService;
    }

    public Optional<ButtonType> error(final String header, final String content) {
        progressService.updateSteps(0);
        progressService.updateProgress(ProgressType.ABORT);
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(content);
        return alert.showAndWait();
    }

    public void warn(final String header, final String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.show();
    }
}
