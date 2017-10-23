package com.github.adamyork.fx5p1d3r.application.view.control;

import com.github.adamyork.fx5p1d3r.GlobalStage;
import com.github.adamyork.fx5p1d3r.common.NullSafe;
import com.github.adamyork.fx5p1d3r.common.command.ApplicationCommand;
import com.github.adamyork.fx5p1d3r.common.command.CommandMap;
import com.github.adamyork.fx5p1d3r.common.model.ApplicationFormState;
import com.github.adamyork.fx5p1d3r.common.model.OutputFileType;
import com.github.adamyork.fx5p1d3r.common.model.UrlMethod;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.URL;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;

/**
 * Created by Adam York on 3/24/2017.
 * Copyright 2017
 */
@Component
public class ControlController implements Initializable, Observer {

    private final MessageSource messageSource;
    private final ApplicationFormState applicationFormState;
    private final GlobalStage globalStage;
    private final CommandMap<UrlMethod, ApplicationCommand> urlMethodCommandMap;
    private final ProgressService progressService;
    private final AbortService abortService;

    @FXML
    private Button startButton;
    @FXML
    private Button stopButton;
    @FXML
    private Label statusLabel;
    @FXML
    private ProgressBar progressBar;

    @Autowired
    public ControlController(final ApplicationFormState applicationFormState,
                             final GlobalStage globalStage,
                             final CommandMap<UrlMethod, ApplicationCommand> urlMethodCommandMap,
                             final ProgressService progressService,
                             final AbortService abortService,
                             final MessageSource messageSource) {
        this.applicationFormState = applicationFormState;
        this.globalStage = globalStage;
        this.urlMethodCommandMap = urlMethodCommandMap;
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
        final NullSafe<File> nullSafe = new NullSafe<>(File.class);
        final File nullSafeFile = nullSafe.getNullSafe(file);
        final String nullSafeFileString = nullSafeFile.toString();
        final int extensionIndex = nullSafeFileString.indexOf(".");
        //TODO COMMAND
        if (extensionIndex != -1) {
            final String fileTypeString = nullSafeFileString.substring(extensionIndex);
            //TODO COMMAND
            if (fileTypeString.equals(OutputFileType.JSON.toString())) {
                applicationFormState.setOutputFileType(OutputFileType.JSON);
            } else {
                applicationFormState.setOutputFileType(OutputFileType.CSV);
            }
            applicationFormState.setOutputFile(nullSafeFileString);
            urlMethodCommandMap.getCommand(applicationFormState.getUrlMethod()).execute();
        }
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
