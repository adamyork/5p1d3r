package com.github.adamyork.fx5p1d3r.view.method;

import com.github.adamyork.fx5p1d3r.ApplicationFormState;
import com.github.adamyork.fx5p1d3r.GlobalDefault;
import com.github.adamyork.fx5p1d3r.GlobalDefaults;
import com.github.adamyork.fx5p1d3r.GlobalStage;
import com.github.adamyork.fx5p1d3r.service.output.data.OutputFileType;
import com.github.adamyork.fx5p1d3r.service.progress.ProgressService;
import com.github.adamyork.fx5p1d3r.service.progress.ProgressType;
import com.github.adamyork.fx5p1d3r.service.url.data.UrlMethod;
import com.github.adamyork.fx5p1d3r.view.method.choice.FollowLinksChoice;
import com.github.adamyork.fx5p1d3r.view.method.choice.MultiThreadingChoice;
import com.github.adamyork.fx5p1d3r.view.method.choice.ThrottleChoice;
import com.github.adamyork.fx5p1d3r.view.method.choice.UrlMethodChoice;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
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
 * Created by Adam York on 3/24/2017.
 * Copyright 2017
 */
@Component
public class MethodController implements Initializable, PropertyChangeListener {

    private final ApplicationFormState applicationFormState;
    private final GlobalStage globalStage;
    private final GlobalDefaults globalDefaults;
    private final MessageSource messageSource;
    private final ProgressService progressService;

    @FXML
    private ChoiceBox<UrlMethodChoice> urlMethodChoiceBox;
    @FXML
    private TextField startingUrlTextfield;
    @FXML
    private Button urlListSelectionButton;
    @FXML
    private ToggleSwitch requestThrottlingToggleSwitch;
    @FXML
    private ChoiceBox<ThrottleChoice> requestThrottlingChoiceBox;
    @FXML
    private ToggleSwitch multiThreadingToggleSwitch;
    @FXML
    private ChoiceBox<MultiThreadingChoice> multiThreadingChoiceBox;
    @FXML
    private ToggleSwitch followLinksToggleSwitch;
    @FXML
    private ChoiceBox<FollowLinksChoice> followLinksChoiceBox;
    @FXML
    private TextField linkUrlPatternTextfield;
    @FXML
    private Label urlLabel;
    @FXML
    private Label urlMethodLabel;
    @FXML
    private Label linkUrlPatternLabel;
    @FXML
    private Tooltip urlMethodChoiceBoxToolTip;
    @FXML
    private Tooltip startingUrlTextfieldToolTip;
    @FXML
    private Tooltip urlListSelectionButtonToolTip;
    @FXML
    private Tooltip requestThrottlingToggleSwitchToolTip;
    @FXML
    private Tooltip requestThrottlingChoiceBoxToolTip;
    @FXML
    private Tooltip multiThreadingToggleSwitchToolTip;
    @FXML
    private Tooltip multiThreadingChoiceBoxToolTip;
    @FXML
    private Tooltip followLinksToggleSwitchToolTip;
    @FXML
    private Tooltip followLinksChoiceBoxToolTip;
    @FXML
    private Tooltip linkUrlPatternTextfieldToolTip;

    @Inject
    public MethodController(final ApplicationFormState applicationFormState,
                            final GlobalStage globalStage,
                            final GlobalDefaults globalDefaults,
                            final MessageSource messageSource,
                            final ProgressService progressService) {
        this.applicationFormState = applicationFormState;
        this.globalStage = globalStage;
        this.globalDefaults = globalDefaults;
        this.messageSource = messageSource;
        this.progressService = progressService;
    }

    @SuppressWarnings("DuplicatedCode")
    @Override
    public void initialize(final URL location, final ResourceBundle resources) {

        urlMethodChoiceBox.setItems(UrlMethodChoice.getUrlMethodChoices());
        urlMethodChoiceBox.getSelectionModel().selectedItemProperty().addListener(this::handleUrlMethodChange);
        urlMethodChoiceBox.setValue(urlMethodChoiceBox.getItems().get(0));
        applicationFormState.setUrlMethod(urlMethodChoiceBox.getSelectionModel().getSelectedItem().getUrlMethod());

        startingUrlTextfield.textProperty().addListener(this::handleStartingUrlChanged);
        startingUrlTextfield.setText(globalDefaults.getDefaultForKey(GlobalDefault.STARTING_URL));
        applicationFormState.setStartingUrl(startingUrlTextfield.getText());

        urlListSelectionButton.setOnAction(this::handleAddUrlList);
        urlListSelectionButton.setDisable(true);

        requestThrottlingToggleSwitch.setText(messageSource.getMessage("throttling.label", null, Locale.getDefault()));
        requestThrottlingToggleSwitch.selectedProperty().addListener(this::handleThrottlingChanged);
        requestThrottlingToggleSwitch.setDisable(true);
        requestThrottlingChoiceBox.setDisable(true);
        requestThrottlingChoiceBox.setItems(ThrottleChoice.getThrottleChoices());
        requestThrottlingChoiceBox.getSelectionModel().selectedItemProperty().addListener(this::handleThrottlingOptionChanged);
        requestThrottlingChoiceBox.setValue(requestThrottlingChoiceBox.getItems().get(3));
        applicationFormState.setThrottleMs(requestThrottlingChoiceBox.getItems().get(3).getThrottleMs());

        multiThreadingToggleSwitch.setText(messageSource.getMessage("threading.label", null, Locale.getDefault()));
        multiThreadingToggleSwitch.selectedProperty().addListener(this::handleMultiThreadingChanged);
        multiThreadingToggleSwitch.setDisable(true);
        multiThreadingChoiceBox.setDisable(true);
        multiThreadingChoiceBox.setItems(MultiThreadingChoice.getMultiThreadingChoices());
        multiThreadingChoiceBox.getSelectionModel().selectedItemProperty().addListener(this::handleMultiThreadingOptionChanged);
        multiThreadingChoiceBox.setValue(multiThreadingChoiceBox.getItems().get(0));
        applicationFormState.setMultiThreadMax(multiThreadingChoiceBox.getItems().get(0).getMultiThreadMax());

        followLinksToggleSwitch.setText(messageSource.getMessage("follow.links.label", null, Locale.getDefault()));
        followLinksToggleSwitch.selectedProperty().addListener(this::handleFollowLinksChanged);
        followLinksChoiceBox.setDisable(true);
        followLinksChoiceBox.setItems(FollowLinksChoice.getFollowLinksChoices());
        followLinksChoiceBox.getSelectionModel().selectedItemProperty().addListener(this::handleFollowLinksOptionChanged);
        followLinksChoiceBox.setValue(followLinksChoiceBox.getItems().get(0));
        applicationFormState.setFollowLinks(followLinksToggleSwitch.isSelected());

        linkUrlPatternTextfield.setText(globalDefaults.getDefaultForKey(GlobalDefault.LINK_PATTERN));
        linkUrlPatternTextfield.setDisable(true);
        applicationFormState.setLinkFollowPattern(linkUrlPatternTextfield.getText());

        applicationFormState.setOutputFileType(OutputFileType.JSON);
        applicationFormState.addListener(this);

        progressService.addListener(this);

        urlLabel.setText(messageSource.getMessage("url.label", null, Locale.getDefault()));
        urlMethodLabel.setText(messageSource.getMessage("url.method.label", null, Locale.getDefault()));
        linkUrlPatternLabel.setText(messageSource.getMessage("link.pattern.label", null, Locale.getDefault()));

        urlMethodChoiceBoxToolTip.setText(messageSource.getMessage("tooltip.method.url.choice", null, Locale.getDefault()));
        startingUrlTextfieldToolTip.setText(messageSource.getMessage("tooltip.method.url.starting", null, Locale.getDefault()));
        urlListSelectionButtonToolTip.setText(messageSource.getMessage("tooltip.method.url.list", null, Locale.getDefault()));
        requestThrottlingToggleSwitchToolTip.setText(messageSource.getMessage("tooltip.method.throttle", null, Locale.getDefault()));
        requestThrottlingChoiceBoxToolTip.setText(messageSource.getMessage("tooltip.method.throttle.choice", null, Locale.getDefault()));
        multiThreadingToggleSwitchToolTip.setText(messageSource.getMessage("tooltip.method.threading", null, Locale.getDefault()));
        multiThreadingChoiceBoxToolTip.setText(messageSource.getMessage("tooltip.method.threading.choice", null, Locale.getDefault()));
        followLinksToggleSwitchToolTip.setText(messageSource.getMessage("tooltip.method.link", null, Locale.getDefault()));
        followLinksChoiceBoxToolTip.setText(messageSource.getMessage("tooltip.method.link.choice", null, Locale.getDefault()));
        linkUrlPatternTextfieldToolTip.setText(messageSource.getMessage("tooltip.method.pattern.value", null, Locale.getDefault()));

        Platform.runLater(() -> startingUrlTextfield.requestFocus());
    }

    private void handleUrlMethodChange(final ObservableValue<? extends UrlMethodChoice> observableValue,
                                       final Choice oldChoice, final Choice newChoice) {
        final UrlMethodChoice methodChoice = (UrlMethodChoice) newChoice;
        final UrlMethod urlMethod = methodChoice.getUrlMethod();
        applicationFormState.setUrlMethod(urlMethod);
        final String urlString = messageSource.getMessage("url.label", null, Locale.getDefault());
        final String urlListString = messageSource.getMessage("url.list.label", null, Locale.getDefault());
        urlLabel.setText(urlMethod.equals(UrlMethod.URL) ? urlString : urlListString);
        startingUrlTextfield.setDisable(urlMethod.equals(UrlMethod.URL_LIST));
        urlListSelectionButton.setDisable(urlMethod.equals(UrlMethod.URL));
        final boolean isSingleUrl = urlMethodChoiceBox.getSelectionModel().getSelectedItem().getUrlMethod().equals(UrlMethod.URL);
        if (isSingleUrl) {
            multiThreadingToggleSwitch.setDisable(!followLinksToggleSwitch.isSelected());
            requestThrottlingToggleSwitch.setDisable(!followLinksToggleSwitch.isSelected());
        } else {
            multiThreadingToggleSwitch.setDisable(false);
            requestThrottlingToggleSwitch.setDisable(false);
        }
    }

    private void handleStartingUrlChanged(final Observable observable,
                                          final String oldValue,
                                          final String newValue) {
        applicationFormState.setStartingUrl(Optional.ofNullable(newValue).orElse(""));
    }

    private void handleThrottlingChanged(final Observable observable) {
        final boolean selected = ((BooleanProperty) observable).get();
        requestThrottlingChoiceBox.setDisable(!selected);
        applicationFormState.setThrottling(selected);
    }

    private void handleThrottlingOptionChanged(final ObservableValue<? extends ThrottleChoice> observableValue,
                                               final Choice oldChoice,
                                               final Choice newChoice) {
        final ThrottleChoice throttleChoice = (ThrottleChoice) newChoice;
        applicationFormState.setThrottleMs(throttleChoice.getThrottleMs());
    }

    private void handleMultiThreadingChanged(final Observable observable) {
        final boolean selected = ((BooleanProperty) observable).get();
        multiThreadingChoiceBox.setDisable(!selected);
        applicationFormState.setMultithreading(selected);
    }

    private void handleMultiThreadingOptionChanged(final ObservableValue<? extends MultiThreadingChoice> observableValue,
                                                   final Choice oldChoice,
                                                   final Choice newChoice) {
        final MultiThreadingChoice multiThreadingChoice = (MultiThreadingChoice) newChoice;
        applicationFormState.setMultiThreadMax(multiThreadingChoice.getMultiThreadMax());
    }

    private void handleFollowLinksChanged(final Observable observable) {
        final boolean selected = ((BooleanProperty) observable).get();
        followLinksChoiceBox.setDisable(!selected);
        linkUrlPatternTextfield.setDisable(!selected);
        applicationFormState.setFollowLinks(selected);
        final boolean isSingleUrl = urlMethodChoiceBox.getSelectionModel().getSelectedItem().getUrlMethod().equals(UrlMethod.URL);
        multiThreadingToggleSwitch.setDisable(!followLinksToggleSwitch.isSelected() && isSingleUrl);
        requestThrottlingToggleSwitch.setDisable(!followLinksToggleSwitch.isSelected() && isSingleUrl);
        if (!selected && multiThreadingToggleSwitch.isSelected() && isSingleUrl) {
            multiThreadingToggleSwitch.setSelected(false);
            requestThrottlingToggleSwitch.setSelected(false);
        }
    }

    private void handleFollowLinksOptionChanged(final ObservableValue<? extends FollowLinksChoice> observableValue,
                                                final Choice oldChoice, final Choice newChoice) {
        final FollowLinksChoice followLinksChoice = (FollowLinksChoice) newChoice;
        applicationFormState.setFollowLinksDepth(followLinksChoice.getFollowLinksDepth());
    }

    @SuppressWarnings("DuplicatedCode")
    private void handleAddUrlList(final ActionEvent actionEvent) {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(messageSource.getMessage("select.url.list.label", null, Locale.getDefault()));
        final String[] validExtensions = {"*.txt"};
        final FileChooser.ExtensionFilter txtFileFilter = new FileChooser.ExtensionFilter("txt files", validExtensions);
        fileChooser.getExtensionFilters().add(txtFileFilter);
        final File file = fileChooser.showOpenDialog(globalStage.getStage());
        final boolean fileSelected = !(file == null && applicationFormState.getUrlListFile() != null);
        if (fileSelected) {
            final File nullSafeFile = Optional.ofNullable(file).orElse(new File(""));
            final String nullSafeFilePath = nullSafeFile.toString();
            startingUrlTextfield.setText(nullSafeFilePath);
            applicationFormState.setUrlListFile(nullSafeFile);
        }
    }

    @Override
    public void propertyChange(final PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("form")) {
            final FilteredList<UrlMethodChoice> filteredMethodChoices = urlMethodChoiceBox.getItems()
                    .filtered(urlMethodChoice -> urlMethodChoice.getUrlMethod().equals(applicationFormState.getUrlMethod()));
            final UrlMethodChoice methodChoice = filteredMethodChoices.get(0);
            urlMethodChoiceBox.setValue(methodChoice);
            urlListSelectionButton.setDisable(methodChoice.getUrlMethod().equals(UrlMethod.URL));
            startingUrlTextfield.setText(applicationFormState.getStartingUrl());

            final FilteredList<ThrottleChoice> filteredThrottlingChoices = requestThrottlingChoiceBox.getItems()
                    .filtered(throttleChoice -> throttleChoice.getThrottleMs().equals(applicationFormState.getThrottleMs()));
            final ThrottleChoice throttleChoice = filteredThrottlingChoices.get(0);
            requestThrottlingToggleSwitch.setSelected(applicationFormState.throttling());
            requestThrottlingChoiceBox.setDisable(!applicationFormState.throttling());
            requestThrottlingChoiceBox.setValue(throttleChoice);

            final FilteredList<MultiThreadingChoice> filteredMultiThreadingChoices = multiThreadingChoiceBox.getItems()
                    .filtered(multiThreadingChoice -> multiThreadingChoice.getMultiThreadMax().equals(applicationFormState.getMultiThreadMax()));
            final MultiThreadingChoice multiThreadingChoice = filteredMultiThreadingChoices.get(0);
            multiThreadingToggleSwitch.setSelected(applicationFormState.multithreading());
            multiThreadingChoiceBox.setDisable(!applicationFormState.multithreading());
            multiThreadingChoiceBox.setValue(multiThreadingChoice);

            final FilteredList<FollowLinksChoice> filteredFollowLinksChoices = followLinksChoiceBox.getItems()
                    .filtered(followLinksChoice -> followLinksChoice.getFollowLinksDepth().equals(applicationFormState.getFollowLinksDepth()));
            final FollowLinksChoice followLinksChoice = filteredFollowLinksChoices.get(0);
            followLinksToggleSwitch.setSelected(applicationFormState.followLinks());
            followLinksChoiceBox.setDisable(!applicationFormState.followLinks());
            followLinksChoiceBox.setValue(followLinksChoice);

            linkUrlPatternTextfield.setText(applicationFormState.getLinkFollowPattern());
            linkUrlPatternTextfield.setDisable(!applicationFormState.followLinks());
        } else {
            if (progressService.getCurrentProgressType().equals(ProgressType.COMPLETE) ||
                    progressService.getCurrentProgressType().equals(ProgressType.ABORT)) {
                final FilteredList<UrlMethodChoice> filteredMethodChoices = urlMethodChoiceBox.getItems()
                        .filtered(urlMethodChoice -> urlMethodChoice.getUrlMethod().equals(applicationFormState.getUrlMethod()));
                final UrlMethodChoice methodChoice = filteredMethodChoices.get(0);
                startingUrlTextfield.setDisable(methodChoice.getUrlMethod().equals(UrlMethod.URL_LIST));
                urlMethodChoiceBox.setDisable(false);
                requestThrottlingToggleSwitch.setDisable(false);
                multiThreadingToggleSwitch.setDisable(false);
                followLinksToggleSwitch.setDisable(false);
                urlListSelectionButton.setDisable(methodChoice.getUrlMethod().equals(UrlMethod.URL));
                requestThrottlingChoiceBox.setDisable(!applicationFormState.throttling());
                multiThreadingChoiceBox.setDisable(!applicationFormState.multithreading());
                followLinksChoiceBox.setDisable(!applicationFormState.followLinks());
                linkUrlPatternTextfield.setDisable(!applicationFormState.followLinks());
            } else {
                applicationFormState.setLinkFollowPattern(Optional.of(linkUrlPatternTextfield.getText()).orElse(""));
                urlMethodChoiceBox.setDisable(true);
                startingUrlTextfield.setDisable(true);
                urlListSelectionButton.setDisable(true);
                requestThrottlingToggleSwitch.setDisable(true);
                requestThrottlingChoiceBox.setDisable(true);
                multiThreadingToggleSwitch.setDisable(true);
                multiThreadingChoiceBox.setDisable(true);
                followLinksToggleSwitch.setDisable(true);
                followLinksChoiceBox.setDisable(true);
                linkUrlPatternTextfield.setDisable(true);
            }
        }
    }
}
