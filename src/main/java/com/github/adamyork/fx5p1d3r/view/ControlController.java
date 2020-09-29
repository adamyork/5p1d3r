package com.github.adamyork.fx5p1d3r.view;

import com.github.adamyork.fx5p1d3r.GlobalStage;
import com.github.adamyork.fx5p1d3r.service.url.DataService;
import com.github.adamyork.fx5p1d3r.ApplicationFormState;
import com.github.adamyork.fx5p1d3r.service.output.data.OutputFileType;
import com.github.adamyork.fx5p1d3r.service.url.data.UrlMethod;
import com.github.adamyork.fx5p1d3r.service.progress.AbortService;
import com.github.adamyork.fx5p1d3r.service.progress.ApplicationProgressService;
import com.github.adamyork.fx5p1d3r.service.progress.ProgressState;
import com.github.adamyork.fx5p1d3r.service.progress.ProgressType;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.FileChooser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.net.URL;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Created by Adam York on 3/24/2017.
 * Copyright 2017
 */
@Component
public class ControlController implements Initializable, PropertyChangeListener {

    private static final Logger logger = LogManager.getLogger(ControlController.class);

    private final MessageSource messageSource;
    private final GlobalStage globalStage;
    private final ApplicationProgressService progressService;
    private final AbortService abortService;
    private final ApplicationFormState applicationFormState;
    private final DataService dataService;

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
    public ControlController(final GlobalStage globalStage,
                             final ApplicationProgressService progressService,
                             final AbortService abortService,
                             final MessageSource messageSource,
                             final ApplicationFormState applicationFormState,
                             final DataService dataService) {
        this.globalStage = globalStage;
        this.progressService = progressService;
        this.abortService = abortService;
        this.messageSource = messageSource;
        this.applicationFormState = applicationFormState;
        this.dataService = dataService;
    }

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        startButton.setText(messageSource.getMessage("start.label", null, Locale.getDefault()));
        startButton.setOnAction(this::handleStart);
        stopButton.setText(messageSource.getMessage("abort.label", null, Locale.getDefault()));
        stopButton.setOnAction(this::handleStop);
        staticStatusLabel.setText(messageSource.getMessage("status.label", null, Locale.getDefault()));
        progressService.addListener(this);
        modal(false);
    }

    private void handleStart(final ActionEvent actionEvent) {
        logger.debug("Starting crawl");
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
        if (extensionIndex != -1) {
            final String fileTypeString = nullSafeFileString.substring(extensionIndex);
            if (fileTypeString.equals(OutputFileType.JSON.toString())) {
                applicationFormState.setOutputFileType(OutputFileType.JSON);
                logger.debug("JSON output file type selected");
            } else {
                applicationFormState.setOutputFileType(OutputFileType.CSV);
                logger.debug("CSV output file type selected");
            }
            applicationFormState.setOutputFile(nullSafeFileString);
            if (applicationFormState.getUrlMethod().equals(UrlMethod.URL)) {
                dataService.executeSingle();
            } else {
                dataService.executeList();
            }
            modal(true);
        }
    }

    private void handleStop(final ActionEvent actionEvent) {
        logger.debug("Cancelling Crawl");
        abortService.abort();
        modal(false);
    }

    private void modal(final boolean enable) {
        startButton.setDisable(enable);
        stopButton.setDisable(!enable);
    }

    @Override
    public void propertyChange(final PropertyChangeEvent evt) {
        final ProgressState progressState = progressService.getProgressState();
        statusLabel.setText(progressState.getMessage());
        progressBar.setProgress(progressState.getProgress());
        logger.debug("Progress updated " + progressService.getCurrentProgressType());
        if (progressService.getCurrentProgressType().equals(ProgressType.COMPLETE)) {
            statusLabel.setText("Idle");
            progressBar.setProgress(0.0);
            modal(false);
        }
    }
}
