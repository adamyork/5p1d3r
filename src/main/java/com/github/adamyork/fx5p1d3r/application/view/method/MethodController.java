package com.github.adamyork.fx5p1d3r.application.view.method;

import com.github.adamyork.fx5p1d3r.GlobalStage;
import com.github.adamyork.fx5p1d3r.application.view.method.choice.FollowLinksChoice;
import com.github.adamyork.fx5p1d3r.application.view.method.choice.MultiThreadingChoice;
import com.github.adamyork.fx5p1d3r.application.view.method.choice.ThrottleChoice;
import com.github.adamyork.fx5p1d3r.application.view.method.choice.UrlMethodChoice;
import com.github.adamyork.fx5p1d3r.application.view.method.command.ManageMethodTogglesCommand;
import com.github.adamyork.fx5p1d3r.common.GlobalDefaults;
import com.github.adamyork.fx5p1d3r.common.command.CommandMap;
import com.github.adamyork.fx5p1d3r.common.command.ValidatorCommand;
import com.github.adamyork.fx5p1d3r.common.model.ApplicationFormState;
import com.github.adamyork.fx5p1d3r.common.model.GlobalDefault;
import com.github.adamyork.fx5p1d3r.common.model.OutputFileType;
import com.github.adamyork.fx5p1d3r.common.model.UrlMethod;
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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.awt.*;
import java.io.File;
import java.net.URL;
import java.util.Locale;
import java.util.Observer;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Created by Adam York on 3/24/2017.
 * Copyright 2017
 */
@Component
public class MethodController implements Initializable, Observer {

    private final CommandMap<Boolean, ValidatorCommand> loadUrlListCommandMap;
    private final CommandMap<Boolean, ManageMethodTogglesCommand> manageMethodTogglesCommandMap;
    private final CommandMap<Boolean, ManageMethodTogglesCommand> selectToggleCommandMap;
    private final ApplicationFormState applicationFormState;
    private final GlobalStage globalStage;
    private final GlobalDefaults globalDefaults;
    private final MessageSource messageSource;

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

    @Inject
    public MethodController(final @Qualifier("LoadUrlListCommandMap") CommandMap<Boolean, ValidatorCommand> loadUrlListCommandMap,
                            final @Qualifier("ManageMethodTogglesCommandMap") CommandMap<Boolean, ManageMethodTogglesCommand> manageMethodTogglesCommandMap,
                            final @Qualifier("SelectToggleCommandMap") CommandMap<Boolean, ManageMethodTogglesCommand> selectToggleCommandMap,
                            final ApplicationFormState applicationFormState,
                            final GlobalStage globalStage,
                            final GlobalDefaults globalDefaults,
                            final MessageSource messageSource) {
        this.loadUrlListCommandMap = loadUrlListCommandMap;
        this.manageMethodTogglesCommandMap = manageMethodTogglesCommandMap;
        this.selectToggleCommandMap = selectToggleCommandMap;
        this.applicationFormState = applicationFormState;
        this.globalStage = globalStage;
        this.globalDefaults = globalDefaults;
        this.messageSource = messageSource;
    }

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

        linkUrlPatternTextfield.setOnKeyTyped(this::handleLinkFollowPatternChanged);
        linkUrlPatternTextfield.setText(globalDefaults.getDefaultForKey(GlobalDefault.LINK_PATTERN));
        linkUrlPatternTextfield.setDisable(true);
        applicationFormState.setLinkFollowPattern(linkUrlPatternTextfield.getText());

        applicationFormState.setOutputFileType(OutputFileType.JSON);
        applicationFormState.addObserver(this);

        urlLabel.setText(messageSource.getMessage("url.label", null, Locale.getDefault()));
        urlMethodLabel.setText(messageSource.getMessage("url.method.label", null, Locale.getDefault()));
        linkUrlPatternLabel.setText(messageSource.getMessage("link.pattern.label", null, Locale.getDefault()));

        Platform.runLater(() -> startingUrlTextfield.requestFocus());
    }

    @SuppressWarnings("unused")
    private void handleUrlMethodChange(final ObservableValue<? extends UrlMethodChoice> observableValue,
                                       final Choice oldChoice, final Choice newChoice) {
        final UrlMethodChoice methodChoice = (UrlMethodChoice) newChoice;
        final UrlMethod urlMethod = methodChoice.getUrlMethod();
        startingUrlTextfield.setDisable(urlMethod.equals(UrlMethod.URL_LIST));
        applicationFormState.setUrlMethod(urlMethod);
        final String urlString = messageSource.getMessage("url.label", null, Locale.getDefault());
        final String urlListString = messageSource.getMessage("url.list.label", null, Locale.getDefault());
        urlLabel.setText(urlMethod.equals(UrlMethod.URL) ? urlString : urlListString);
        urlListSelectionButton.setDisable(urlMethod.equals(UrlMethod.URL));
        final boolean isSingleUrl = urlMethodChoiceBox.getSelectionModel().getSelectedItem().getUrlMethod().equals(UrlMethod.URL);
        manageMethodTogglesCommandMap.getCommand(isSingleUrl).execute(requestThrottlingToggleSwitch, multiThreadingToggleSwitch,
                followLinksToggleSwitch);
    }

    private void handleStartingUrlChanged(@SuppressWarnings("unused") final Observable observable,
                                          @SuppressWarnings("unused") final String oldValue,
                                          final String newValue) {
        applicationFormState.setStartingUrl(Optional.ofNullable(newValue).orElse(""));
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
        linkUrlPatternTextfield.setDisable(!selected);
        applicationFormState.setFollowLinks(selected);
        final boolean isSingleUrl = urlMethodChoiceBox.getSelectionModel().getSelectedItem().getUrlMethod().equals(UrlMethod.URL);
        multiThreadingToggleSwitch.setDisable(!followLinksToggleSwitch.isSelected() && isSingleUrl);
        requestThrottlingToggleSwitch.setDisable(!followLinksToggleSwitch.isSelected() && isSingleUrl);
        selectToggleCommandMap.getCommand(!selected && multiThreadingToggleSwitch.isSelected() && isSingleUrl).execute(requestThrottlingToggleSwitch,
                multiThreadingToggleSwitch, followLinksToggleSwitch);
    }

    @SuppressWarnings("unused")
    private void handleFollowLinksOptionChanged(final ObservableValue<? extends FollowLinksChoice> observableValue,
                                                final Choice oldChoice, final Choice newChoice) {
        final FollowLinksChoice followLinksChoice = (FollowLinksChoice) newChoice;
        applicationFormState.setFollowLinksDepth(followLinksChoice.getFollowLinksDepth());
    }

    private void handleLinkFollowPatternChanged(@SuppressWarnings("unused") final KeyEvent keyEvent) {
        applicationFormState.setLinkFollowPattern(Optional.of(linkUrlPatternTextfield.getText() + keyEvent.getCharacter()).orElse(""));
    }

    private void handleAddUrlList(@SuppressWarnings("unused") final ActionEvent actionEvent) {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(messageSource.getMessage("select.url.list.label", null, Locale.getDefault()));
        final String[] validExtensions = {"*.txt"};
        final FileChooser.ExtensionFilter txtFileFilter = new FileChooser.ExtensionFilter("txt files", validExtensions);
        fileChooser.getExtensionFilters().add(txtFileFilter);
        final File file = fileChooser.showOpenDialog(globalStage.getStage());
        final boolean fileSelected = !(file == null && applicationFormState.getUrlListFile() != null);
        loadUrlListCommandMap.getCommand(fileSelected).execute(file, startingUrlTextfield);
    }

    @Override
    public void update(final java.util.Observable observable, final Object observed) {
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
        applicationFormState.clearNotify();
    }
}
