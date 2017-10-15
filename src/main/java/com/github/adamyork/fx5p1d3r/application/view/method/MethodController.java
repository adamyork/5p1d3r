package com.github.adamyork.fx5p1d3r.application.view.method;

import com.github.adamyork.fx5p1d3r.GlobalStage;
import com.github.adamyork.fx5p1d3r.application.view.method.choice.FollowLinksChoice;
import com.github.adamyork.fx5p1d3r.application.view.method.choice.MultiThreadingChoice;
import com.github.adamyork.fx5p1d3r.application.view.method.choice.ThrottleChoice;
import com.github.adamyork.fx5p1d3r.application.view.method.choice.URLMethodChoice;
import com.github.adamyork.fx5p1d3r.common.GlobalDefaults;
import com.github.adamyork.fx5p1d3r.common.NullSafe;
import com.github.adamyork.fx5p1d3r.common.command.CommandMap;
import com.github.adamyork.fx5p1d3r.common.command.ValidatorCommand;
import com.github.adamyork.fx5p1d3r.common.model.*;
import javafx.application.Platform;
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
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import org.controlsfx.control.ToggleSwitch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.File;
import java.net.URL;
import java.util.Observer;
import java.util.ResourceBundle;

/**
 * Created by Adam York on 3/24/2017.
 * Copyright 2017
 */
@Component
public class MethodController implements Initializable, Observer {

    @FXML
    private ChoiceBox<URLMethodChoice> urlMethodChoiceBox;
    @FXML
    private TextField startingURLTextfield;
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
    private TextField linkURLPatternTextfield;
    @FXML
    private Label urlLabel;

    private ApplicationFormState applicationFormState;
    private GlobalStage globalStage;
    private GlobalDefaults globalDefaults;
    private CommandMap<Boolean, ValidatorCommand> loadUrlListCommandMap;

    @Autowired
    public MethodController(final ApplicationFormState applicationFormState,
                            final GlobalStage globalStage,
                            final GlobalDefaults globalDefaults,
                            final @Qualifier("LoadUrlListCommandMap") CommandMap<Boolean, ValidatorCommand> loadUrlListCommandMap) {
        this.applicationFormState = applicationFormState;
        this.globalStage = globalStage;
        this.globalDefaults = globalDefaults;
        this.loadUrlListCommandMap = loadUrlListCommandMap;
    }

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        urlMethodChoiceBox.setItems(URLMethodChoice.getURLMethodChoices());
        urlMethodChoiceBox.getSelectionModel().selectedItemProperty().addListener(this::handleURLMethodChange);
        urlMethodChoiceBox.setValue(urlMethodChoiceBox.getItems().get(0));
        applicationFormState.setUrlMethod(urlMethodChoiceBox.getSelectionModel().getSelectedItem().getUrlMethod());

        startingURLTextfield.textProperty().addListener(this::handleStartingURLChanged);
        startingURLTextfield.setText(globalDefaults.getDefaultForKey(GlobalDefault.STARTING_URL));
        applicationFormState.setStartingURL(startingURLTextfield.getText());

        urlListSelectionButton.setOnAction(this::handleAddUrlList);
        urlListSelectionButton.setDisable(true);

        requestThrottlingToggleSwitch.selectedProperty().addListener(this::handleThrottlingChanged);
        requestThrottlingToggleSwitch.setSelected(true);
        requestThrottlingChoiceBox.setDisable(false);
        requestThrottlingChoiceBox.setItems(ThrottleChoice.getThrottleChoices());
        requestThrottlingChoiceBox.getSelectionModel().selectedItemProperty().addListener(this::handleThrottlingOptionChanged);
        requestThrottlingChoiceBox.setValue(requestThrottlingChoiceBox.getItems().get(3));
        applicationFormState.setThrottleMs(requestThrottlingChoiceBox.getItems().get(3).getThrottleMs());

        multiThreadingToggleSwitch.selectedProperty().addListener(this::handleMultiThreadingChanged);
        multiThreadingChoiceBox.setDisable(true);
        multiThreadingChoiceBox.setItems(MultiThreadingChoice.getMultiThreadingChoices());
        multiThreadingChoiceBox.getSelectionModel().selectedItemProperty().addListener(this::handleMultiThreadingOptionChanged);
        multiThreadingChoiceBox.setValue(multiThreadingChoiceBox.getItems().get(0));
        applicationFormState.setMultiThreadMax(multiThreadingChoiceBox.getItems().get(0).getMultiThreadMax());

        followLinksToggleSwitch.selectedProperty().addListener(this::handleFollowLinksChanged);
        followLinksChoiceBox.setDisable(true);
        followLinksChoiceBox.setItems(FollowLinksChoice.getFollowLinksChoices());
        followLinksChoiceBox.getSelectionModel().selectedItemProperty().addListener(this::handleFollowLinksOptionChanged);
        followLinksChoiceBox.setValue(followLinksChoiceBox.getItems().get(0));
        applicationFormState.setFollowLinks(followLinksToggleSwitch.isSelected());

        linkURLPatternTextfield.setOnKeyTyped(this::handleLinkFollowPatternChanged);
        linkURLPatternTextfield.setText(globalDefaults.getDefaultForKey(GlobalDefault.LINK_PATTERN));
        linkURLPatternTextfield.setDisable(true);
        applicationFormState.setLinkFollowPattern(linkURLPatternTextfield.getText());

        applicationFormState.setOutputFileType(OutputFileType.JSON);
        applicationFormState.addObserver(this);
        Platform.runLater(() -> startingURLTextfield.requestFocus());
    }

    @SuppressWarnings("unused")
    private void handleURLMethodChange(final ObservableValue<? extends URLMethodChoice> observableValue,
                                       final Choice oldChoice, final Choice newChoice) {
        final URLMethodChoice methodChoice = (URLMethodChoice) newChoice;
        final URLMethod urlMethod = methodChoice.getUrlMethod();
        startingURLTextfield.setDisable(urlMethod.equals(URLMethod.URL_LIST));
        applicationFormState.setUrlMethod(urlMethod);
        urlLabel.setText(urlMethod.equals(URLMethod.URL) ? "URL" : "URL LIST");
        urlListSelectionButton.setDisable(urlMethod.equals(URLMethod.URL));
    }

    private void handleStartingURLChanged(@SuppressWarnings("unused") final Observable observable,
                                          @SuppressWarnings("unused") final String oldValue,
                                          final String newValue) {
        applicationFormState.setStartingURL(NullSafe.getSafeString(newValue));
    }

    private void handleThrottlingChanged(@SuppressWarnings("unused") final Observable observable) {
        final boolean selected = ((BooleanProperty) observable).get();
        requestThrottlingChoiceBox.setDisable(!selected);
        applicationFormState.setThrottling(selected);
    }

    @SuppressWarnings("unused")
    private void handleThrottlingOptionChanged(final ObservableValue<? extends ThrottleChoice> observableValue,
                                               final Choice oldChoice,
                                               final Choice newChoice) {
        final ThrottleChoice throttleChoice = (ThrottleChoice) newChoice;
        applicationFormState.setThrottleMs(throttleChoice.getThrottleMs());
    }

    private void handleMultiThreadingChanged(@SuppressWarnings("unused") final Observable observable) {
        final boolean selected = ((BooleanProperty) observable).get();
        multiThreadingChoiceBox.setDisable(!selected);
        applicationFormState.setMultithreading(selected);
        requestThrottlingToggleSwitch.setSelected(!selected);
        requestThrottlingChoiceBox.setValue(requestThrottlingChoiceBox.getItems().get(0));
        requestThrottlingChoiceBox.setDisable(selected);
        final ThrottleMs throttleMs = (selected) ? ThrottleMs.ZERO : requestThrottlingChoiceBox.getSelectionModel().getSelectedItem().getThrottleMs();
        applicationFormState.setThrottleMs(throttleMs);
    }

    @SuppressWarnings("unused")
    private void handleMultiThreadingOptionChanged(final ObservableValue<? extends MultiThreadingChoice> observableValue,
                                                   final Choice oldChoice,
                                                   final Choice newChoice) {
        final MultiThreadingChoice multiThreadingChoice = (MultiThreadingChoice) newChoice;
        applicationFormState.setMultiThreadMax(multiThreadingChoice.getMultiThreadMax());
    }

    private void handleFollowLinksChanged(@SuppressWarnings("unused") final Observable observable) {
        final boolean selected = ((BooleanProperty) observable).get();
        followLinksChoiceBox.setDisable(!selected);
        linkURLPatternTextfield.setDisable(!selected);
        applicationFormState.setFollowLinks(selected);
    }

    @SuppressWarnings("unused")
    private void handleFollowLinksOptionChanged(final ObservableValue<? extends FollowLinksChoice> observableValue,
                                                final Choice oldChoice, final Choice newChoice) {
        final FollowLinksChoice followLinksChoice = (FollowLinksChoice) newChoice;
        applicationFormState.setFollowLinksDepth(followLinksChoice.getFollowLinksDepth());
    }

    private void handleLinkFollowPatternChanged(@SuppressWarnings("unused") final KeyEvent keyEvent) {
        applicationFormState.setLinkFollowPattern(NullSafe.getSafeString(linkURLPatternTextfield.getText()));
    }

    private void handleAddUrlList(@SuppressWarnings("unused") final ActionEvent actionEvent) {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select URL List");
        final String[] validExtensions = {"*.txt"};
        final FileChooser.ExtensionFilter txtFileFilter = new FileChooser.ExtensionFilter("txt files", validExtensions);
        fileChooser.getExtensionFilters().add(txtFileFilter);
        final File file = fileChooser.showOpenDialog(globalStage.getStage());
        final boolean fileSelected = !(file == null && applicationFormState.getUrlListFile() != null);
        loadUrlListCommandMap.getCommand(fileSelected).execute(file, startingURLTextfield);
    }

    @Override
    public void update(final java.util.Observable observable, final Object arg) {
        final FilteredList<URLMethodChoice> filteredMethodChoices = urlMethodChoiceBox.getItems()
                .filtered(urlMethodChoice -> urlMethodChoice.getUrlMethod().equals(applicationFormState.getUrlMethod()));
        urlMethodChoiceBox.setValue(filteredMethodChoices.get(0));
        startingURLTextfield.setText(globalDefaults.getDefaultForKey(GlobalDefault.STARTING_URL));
        urlListSelectionButton.setDisable(true);
        requestThrottlingToggleSwitch.setSelected(true);
        requestThrottlingChoiceBox.setDisable(false);
        final FilteredList<ThrottleChoice> filteredThrottlingChoices = requestThrottlingChoiceBox.getItems()
                .filtered(throttleChoice -> throttleChoice.getThrottleMs().equals(applicationFormState.getThrottleMs()));
        requestThrottlingChoiceBox.setValue(filteredThrottlingChoices.get(0));
        multiThreadingChoiceBox.setDisable(true);
        multiThreadingChoiceBox.setValue(multiThreadingChoiceBox.getItems().get(0));
        followLinksChoiceBox.setDisable(true);
        followLinksChoiceBox.setValue(followLinksChoiceBox.getItems().get(0));
        linkURLPatternTextfield.setText(globalDefaults.getDefaultForKey(GlobalDefault.LINK_PATTERN));
        linkURLPatternTextfield.setDisable(true);

    }
}
