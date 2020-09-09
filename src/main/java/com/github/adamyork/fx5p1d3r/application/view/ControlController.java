package com.github.adamyork.fx5p1d3r.application.view;

import com.github.adamyork.fx5p1d3r.GlobalStage;
import com.github.adamyork.fx5p1d3r.application.service.url.UrlService;
import com.github.adamyork.fx5p1d3r.application.view.method.MethodController;
import com.github.adamyork.fx5p1d3r.application.view.query.QueryController;
import com.github.adamyork.fx5p1d3r.common.model.ApplicationFormState;
import com.github.adamyork.fx5p1d3r.common.model.OutputFileType;
import com.github.adamyork.fx5p1d3r.common.model.UrlMethod;
import com.github.adamyork.fx5p1d3r.common.service.AbortService;
import com.github.adamyork.fx5p1d3r.common.service.progress.ProgressService;
import com.github.adamyork.fx5p1d3r.common.service.progress.ProgressState;
import com.github.adamyork.fx5p1d3r.common.service.progress.ProgressType;
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
import java.io.File;
import java.net.URL;
import java.util.*;

/**
 * Created by Adam York on 3/24/2017.
 * Copyright 2017
 */
@Component
public class ControlController implements Initializable, Observer, ModalHandler {

    private static final Logger logger = LogManager.getLogger(ControlController.class);

    private final MessageSource messageSource;
    private final GlobalStage globalStage;
    private final ProgressService progressService;
    private final AbortService abortService;
    private final ApplicationFormState applicationFormState;
    private final UrlService urlService;
    private final MethodController methodController;
    private final TransformController transformController;
    private final QueryController queryController;

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
                             final ProgressService progressService,
                             final AbortService abortService,
                             final MessageSource messageSource,
                             final ApplicationFormState applicationFormState,
                             final UrlService urlService,
                             final MethodController methodController,
                             final TransformController transformController,
                             final QueryController queryController) {
        this.globalStage = globalStage;
        this.progressService = progressService;
        this.abortService = abortService;
        this.messageSource = messageSource;
        this.applicationFormState = applicationFormState;
        this.urlService = urlService;
        this.methodController = methodController;
        this.transformController = transformController;
        this.queryController = queryController;
    }

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        startButton.setText(messageSource.getMessage("start.label", null, Locale.getDefault()));
        startButton.setOnAction(this::handleStart);
        stopButton.setText(messageSource.getMessage("abort.label", null, Locale.getDefault()));
        stopButton.setOnAction(this::handleStop);
        staticStatusLabel.setText(messageSource.getMessage("status.label", null, Locale.getDefault()));
        progressService.addObserver(this);
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
                urlService.executeSingle();
            } else {
                urlService.executeList();
            }
            methodController.modal(true);
            queryController.modal(true);
            transformController.modal(true);
            modal(true);
        }
    }

    private void handleStop(final ActionEvent actionEvent) {
        logger.debug("Cancelling Crawl");
        abortService.stopAllCalls();
        methodController.modal(false);
        queryController.modal(false);
        transformController.modal(false);
        modal(false);
    }

    @Override
    public void update(final Observable observable, final Object changed) {
        final ProgressState progressState = progressService.getProgressState();
        statusLabel.setText(progressState.getMessage());
        progressBar.setProgress(progressState.getProgress());
        logger.debug("Progress updated " + progressService.getCurrentProgressType());
        if (progressService.getCurrentProgressType().equals(ProgressType.COMPLETE)) {
            statusLabel.setText("Idle");
            progressBar.setProgress(0.0);
            methodController.modal(false);
            queryController.modal(false);
            transformController.modal(false);
            modal(false);
        }
    }

    @Override
    public void modal(final boolean enable) {
        startButton.setDisable(enable);
        stopButton.setDisable(!enable);
    }
}
