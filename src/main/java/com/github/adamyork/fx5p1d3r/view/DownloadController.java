package com.github.adamyork.fx5p1d3r.view;

import com.github.adamyork.fx5p1d3r.ApplicationFormState;
import com.github.adamyork.fx5p1d3r.GlobalStage;
import com.github.adamyork.fx5p1d3r.service.progress.ProgressService;
import com.github.adamyork.fx5p1d3r.service.progress.ProgressType;
import com.github.adamyork.fx5p1d3r.service.url.SpiderService;
import com.github.adamyork.fx5p1d3r.view.method.choice.MultiThreadingChoice;
import com.github.adamyork.fx5p1d3r.view.method.choice.ThrottleChoice;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.ToggleSwitch;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.net.URL;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Created by Adam York on 2/19/2022.
 * Copyright 2022
 */
@Component
public class DownloadController implements Initializable, PropertyChangeListener {

    private static final Logger logger = LogManager.getLogger(DownloadController.class);

    private final ApplicationFormState applicationFormState;
    private final GlobalStage globalStage;
    private final MessageSource messageSource;
    private final ProgressService progressService;
    private final SpiderService urlListDownloadSpiderService;

    @FXML
    public Label urlLabel;
    @FXML
    public Label urlListLabel;
    @FXML
    public Button urlListSelectionButton;
    @FXML
    public ToggleSwitch requestThrottlingToggleSwitch;
    @FXML
    public ChoiceBox<ThrottleChoice> requestThrottlingChoiceBox;
    @FXML
    public ToggleSwitch multiThreadingToggleSwitch;
    @FXML
    public ChoiceBox<MultiThreadingChoice> multiThreadingChoiceBox;
    @FXML
    public Button outputDirSelectionButton;
    @FXML
    public ProgressBar downloadProgressBar;
    @FXML
    public Button startButton;
    @FXML
    public Button stopButton;
    @FXML
    public Label outputDirLabel;
    @FXML
    public Label outputSelectedDirLabel;
    @FXML
    public Label progressLabel;

    @Inject
    public DownloadController(final ApplicationFormState applicationFormState,
                              final GlobalStage globalStage,
                              final MessageSource messageSource,
                              final ProgressService downloadProgressService,
                              final SpiderService urlListDownloadSpiderService) {
        this.applicationFormState = applicationFormState;
        this.globalStage = globalStage;
        this.messageSource = messageSource;
        this.progressService = downloadProgressService;
        this.urlListDownloadSpiderService = urlListDownloadSpiderService;
    }

    @SuppressWarnings("DuplicatedCode")
    @Override
    public void initialize(final URL location, final ResourceBundle resources) {

        urlListSelectionButton.setOnAction(this::handleSelectUrlList);

        requestThrottlingToggleSwitch.setText(messageSource.getMessage("throttling.label", null, Locale.getDefault()));
        requestThrottlingToggleSwitch.selectedProperty().addListener(this::handleThrottlingChanged);
        requestThrottlingToggleSwitch.setDisable(true);
        requestThrottlingChoiceBox.setDisable(true);
        requestThrottlingChoiceBox.setItems(ThrottleChoice.getThrottleChoices());
        requestThrottlingChoiceBox.getSelectionModel().selectedItemProperty().addListener(this::handleThrottlingOptionChanged);
        requestThrottlingChoiceBox.setValue(requestThrottlingChoiceBox.getItems().get(3));
        applicationFormState.setDownloadThrottleMs(requestThrottlingChoiceBox.getItems().get(3).getThrottleMs());

        multiThreadingToggleSwitch.setText(messageSource.getMessage("threading.label", null, Locale.getDefault()));
        multiThreadingToggleSwitch.selectedProperty().addListener(this::handleMultiThreadingChanged);
        multiThreadingToggleSwitch.setDisable(true);
        multiThreadingChoiceBox.setDisable(true);
        multiThreadingChoiceBox.setItems(MultiThreadingChoice.getMultiThreadingChoices());
        multiThreadingChoiceBox.getSelectionModel().selectedItemProperty().addListener(this::handleMultiThreadingOptionChanged);
        multiThreadingChoiceBox.setValue(multiThreadingChoiceBox.getItems().get(0));
        applicationFormState.setDownloadMultiThreadMax(multiThreadingChoiceBox.getItems().get(0).getMultiThreadMax());

        outputDirSelectionButton.setDisable(true);
        outputDirSelectionButton.setOnAction(this::handleSelectOutputDir);

        startButton.setDisable(true);
        startButton.setOnAction(this::handleStart);
        stopButton.setDisable(true);
        stopButton.setOnAction(this::handleStop);

        applicationFormState.addListener(this);
        progressService.addListener(this);
    }

    @SuppressWarnings("DuplicatedCode")
    private void handleSelectUrlList(final ActionEvent actionEvent) {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(messageSource.getMessage("select.url.list.label", null, Locale.getDefault()));
        final String[] validExtensions = {"*.txt"};
        final FileChooser.ExtensionFilter txtFileFilter = new FileChooser.ExtensionFilter("txt files", validExtensions);
        fileChooser.getExtensionFilters().add(txtFileFilter);
        final File file = fileChooser.showOpenDialog(globalStage.getStage());
        final boolean fileSelected = !(file == null && applicationFormState.getDownloadUrlListFile() != null);
        if (fileSelected) {
            final File nullSafeFile = Optional.ofNullable(file).orElse(new File(""));
            final String nullSafeFilePath = nullSafeFile.toString();
            urlListLabel.setText(nullSafeFilePath);
            applicationFormState.setDownloadUrlListFile(nullSafeFile);
            requestThrottlingToggleSwitch.setDisable(false);
            multiThreadingToggleSwitch.setDisable(false);
            outputDirSelectionButton.setDisable(false);
        }
    }

    private void handleThrottlingChanged(final Observable observable) {
        final boolean selected = ((BooleanProperty) observable).get();
        requestThrottlingChoiceBox.setDisable(!selected);
        applicationFormState.setDownloadThrottling(selected);
    }

    private void handleThrottlingOptionChanged(final ObservableValue<? extends ThrottleChoice> observableValue,
                                               final Choice oldChoice,
                                               final Choice newChoice) {
        final ThrottleChoice throttleChoice = (ThrottleChoice) newChoice;
        applicationFormState.setDownloadThrottleMs(throttleChoice.getThrottleMs());
    }

    private void handleMultiThreadingChanged(final Observable observable) {
        final boolean selected = ((BooleanProperty) observable).get();
        multiThreadingChoiceBox.setDisable(!selected);
        applicationFormState.setDownloadMultithreading(selected);
    }

    private void handleMultiThreadingOptionChanged(final ObservableValue<? extends MultiThreadingChoice> observableValue,
                                                   final Choice oldChoice,
                                                   final Choice newChoice) {
        final MultiThreadingChoice multiThreadingChoice = (MultiThreadingChoice) newChoice;
        applicationFormState.setMultiThreadMax(multiThreadingChoice.getMultiThreadMax());
    }

    @SuppressWarnings({"DuplicatedCode", "RedundantSuppression"})
    @Override
    public void propertyChange(final PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("form")) {
            final FilteredList<ThrottleChoice> filteredThrottlingChoices = requestThrottlingChoiceBox.getItems()
                    .filtered(throttleChoice -> throttleChoice.getThrottleMs().equals(applicationFormState.getDownloadThrottleMs()));
            if (!filteredThrottlingChoices.isEmpty()) {
                final ThrottleChoice throttleChoice = filteredThrottlingChoices.get(0);
                requestThrottlingToggleSwitch.setSelected(applicationFormState.downloadThrottling());
                requestThrottlingChoiceBox.setDisable(!applicationFormState.downloadThrottling());
                requestThrottlingChoiceBox.setValue(throttleChoice);
            }
            final FilteredList<MultiThreadingChoice> filteredMultiThreadingChoices = multiThreadingChoiceBox.getItems()
                    .filtered(multiThreadingChoice -> multiThreadingChoice.getMultiThreadMax().equals(applicationFormState.getDownloadMultiThreadMax()));
            if (!filteredMultiThreadingChoices.isEmpty()) {
                final MultiThreadingChoice multiThreadingChoice = filteredMultiThreadingChoices.get(0);
                multiThreadingToggleSwitch.setSelected(applicationFormState.downloadMultithreading());
                multiThreadingChoiceBox.setDisable(!applicationFormState.downloadMultithreading());
                multiThreadingChoiceBox.setValue(multiThreadingChoice);
            }
        } else {
            progressLabel.setText(progressService.getProgressState().getMessage());
            downloadProgressBar.setProgress(progressService.getProgressState().getProgress());
            logger.debug("Progress updated " + progressService.getCurrentProgressType());
            if (progressService.getCurrentProgressType().equals(ProgressType.ABORT)) {
                logger.debug("Forcing Progress Completion ");
                progressService.forceComplete();
            }
            if (progressService.getCurrentProgressType().equals(ProgressType.COMPLETE)) {
                progressLabel.setText("Idle");
                downloadProgressBar.setProgress(0.0);
                startButton.setDisable(false);
                stopButton.setDisable(true);
                urlListSelectionButton.setDisable(false);
                if(applicationFormState.downloadThrottling()){
                    requestThrottlingToggleSwitch.setDisable(false);
                    requestThrottlingChoiceBox.setDisable(false);
                }
                if(applicationFormState.downloadMultithreading()){
                    multiThreadingToggleSwitch.setDisable(false);
                    multiThreadingChoiceBox.setDisable(false);
                }
                outputDirSelectionButton.setDisable(false);
            }
        }
    }

    private void handleSelectOutputDir(final ActionEvent actionEvent) {
        final DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle(messageSource.getMessage("save.output.as.label", null, Locale.getDefault()));
        final File file = directoryChooser.showDialog(globalStage.getStage());
        if (file != null) {
            applicationFormState.setDownloadOutputFile(file);
            startButton.setDisable(false);
            outputSelectedDirLabel.setText(file.getAbsolutePath());
        }
    }

    private void handleStart(final ActionEvent actionEvent) {
        startButton.setDisable(true);
        stopButton.setDisable(false);

        urlListSelectionButton.setDisable(true);
        requestThrottlingToggleSwitch.setDisable(true);
        requestThrottlingChoiceBox.setDisable(true);
        multiThreadingToggleSwitch.setDisable(true);
        multiThreadingChoiceBox.setDisable(true);
        outputDirSelectionButton.setDisable(true);

        urlListDownloadSpiderService.execute(applicationFormState.getDownloadUrlListFile());
    }

    private void handleStop(final ActionEvent actionEvent) {
        logger.debug("handling stop aborting");
        progressService.updateProgress(ProgressType.ABORT);
    }

}
