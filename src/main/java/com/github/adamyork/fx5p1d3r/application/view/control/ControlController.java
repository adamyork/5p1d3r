package com.github.adamyork.fx5p1d3r.application.view.control;

import com.github.adamyork.fx5p1d3r.GlobalStage;
import com.github.adamyork.fx5p1d3r.application.view.control.command.ControlCommand;
import com.github.adamyork.fx5p1d3r.application.view.control.command.ControlStartCommand;
import com.github.adamyork.fx5p1d3r.common.command.CommandMap;
import com.github.adamyork.fx5p1d3r.common.service.AbortService;
import com.github.adamyork.fx5p1d3r.common.service.progress.ProgressService;
import com.github.adamyork.fx5p1d3r.common.service.progress.ProgressState;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.FileChooser;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.File;
import java.net.URL;
import java.util.*;

/**
 * Created by Adam York on 3/24/2017.
 * Copyright 2017
 */
@Component
public class ControlController implements Initializable, Observer {

    private final CommandMap<Boolean, ControlCommand> controlCommandMap;
    private final CommandMap<Boolean, ControlStartCommand> controlStartCommandMap;
    private final MessageSource messageSource;
    private final GlobalStage globalStage;
    private final ProgressService progressService;
    private final AbortService abortService;

    @FXML
    private Button startButton;
    @FXML
    private Button stopButton;
    @FXML
    private Label staticStatusLabel;
    @FXML
    private Label statusLabel;
    @FXML
    private ProgressBar progressBar;

    @Inject
    public ControlController(@Qualifier("ControlCommandMap") final CommandMap<Boolean, ControlCommand> controlCommandMap,
                             @Qualifier("ControlStartCommandMap") final CommandMap<Boolean, ControlStartCommand> controlStartCommandMap,
                             final GlobalStage globalStage,
                             final ProgressService progressService,
                             final AbortService abortService,
                             final MessageSource messageSource) {
        this.controlCommandMap = controlCommandMap;
        this.controlStartCommandMap = controlStartCommandMap;
        this.globalStage = globalStage;
        this.progressService = progressService;
        this.abortService = abortService;
        this.messageSource = messageSource;
    }

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        startButton.setText(messageSource.getMessage("start.label", null, Locale.getDefault()));
        startButton.setOnAction(this::handleStart);
        stopButton.setText(messageSource.getMessage("abort.label", null, Locale.getDefault()));
        stopButton.setOnAction(this::handleStop);
        staticStatusLabel.setText(messageSource.getMessage("status.label", null, Locale.getDefault()));
        progressService.addObserver(this);
    }

    @SuppressWarnings("unused")
    private void handleStart(final ActionEvent actionEvent) {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(messageSource.getMessage("save.output.as.label", null, Locale.getDefault()));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("json", "*.json"),
                new FileChooser.ExtensionFilter("csv", "*.csv")
        );
        final File file = fileChooser.showSaveDialog(globalStage.getStage());
        final File nullSafeFile = Optional.ofNullable(file).orElse(new File(""));
        final String nullSafeFileString = nullSafeFile.toString();
        final int extensionIndex = nullSafeFileString.indexOf(".");
        controlStartCommandMap.getCommand(extensionIndex != -1).execute(nullSafeFileString, extensionIndex, controlCommandMap);
    }

    @SuppressWarnings("unused")
    private void handleStop(final ActionEvent actionEvent) {
        abortService.stopAllCalls();
    }

    @Override
    public void update(final Observable observable, final Object changed) {
        final ProgressState progressState = progressService.getProgressState();
        statusLabel.setText(progressState.getMessage());
        progressBar.setProgress(progressState.getProgress());
    }
}
