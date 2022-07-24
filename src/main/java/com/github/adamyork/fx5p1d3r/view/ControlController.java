package com.github.adamyork.fx5p1d3r.view;

import com.github.adamyork.fx5p1d3r.ApplicationFormState;
import com.github.adamyork.fx5p1d3r.GlobalStage;
import com.github.adamyork.fx5p1d3r.service.output.data.OutputFileType;
import com.github.adamyork.fx5p1d3r.service.progress.AbortService;
import com.github.adamyork.fx5p1d3r.service.progress.ProgressService;
import com.github.adamyork.fx5p1d3r.service.progress.ProgressState;
import com.github.adamyork.fx5p1d3r.service.progress.ProgressType;
import com.github.adamyork.fx5p1d3r.service.url.SpiderService;
import com.github.adamyork.fx5p1d3r.service.url.data.UrlMethod;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tooltip;
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
public class ControlController implements Initializable, PropertyChangeListener, Closeable {

    private static final Logger logger = LogManager.getLogger(ControlController.class);

    private final MessageSource messageSource;
    private final GlobalStage globalStage;
    private final ProgressService progressService;
    private final AbortService abortService;
    private final ApplicationFormState applicationFormState;
    private final SpiderService singleUrlSpiderService;
    private final SpiderService urlListSpiderService;
    @FXML
    public Tooltip startButtonToolTip;
    @FXML
    public Tooltip stopButtonToolTip;
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
                             final SpiderService singleUrlSpiderService,
                             final SpiderService urlListSpiderService) {
        this.globalStage = globalStage;
        this.progressService = progressService;
        this.abortService = abortService;
        this.messageSource = messageSource;
        this.applicationFormState = applicationFormState;
        this.singleUrlSpiderService = singleUrlSpiderService;
        this.urlListSpiderService = urlListSpiderService;
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
        startButtonToolTip.setText(messageSource.getMessage("tooltip.crawl.start", null, Locale.getDefault()));
        stopButtonToolTip.setText(messageSource.getMessage("tooltip.crawl.stop", null, Locale.getDefault()));
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
                singleUrlSpiderService.execute();
            } else {
                urlListSpiderService.execute(applicationFormState.getUrlListFile());
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
        if (progressService.getCurrentProgressType().equals(ProgressType.ABORT)) {
            logger.debug("Forcing Progress Completion ");
            progressService.forceComplete();
        }
        if (progressService.getCurrentProgressType().equals(ProgressType.COMPLETE)) {
            statusLabel.setText("Idle");
            progressBar.setProgress(0.0);
            modal(false);
        }
    }

    @Override
    public void close() {
        logger.info("closing control controller service references");
        singleUrlSpiderService.close();
        logger.info("singleUrlSpiderService closed");
        urlListSpiderService.close();
        logger.info("urlListSpiderService closed");
    }
}
