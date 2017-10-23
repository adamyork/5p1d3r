package com.github.adamyork.fx5p1d3r.application.view.transform;

import com.github.adamyork.fx5p1d3r.GlobalStage;
import com.github.adamyork.fx5p1d3r.common.command.CommandMap;
import com.github.adamyork.fx5p1d3r.common.command.NullSafeCommand;
import com.github.adamyork.fx5p1d3r.common.model.ApplicationFormState;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.FileChooser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Created by Adam York on 3/24/2017.
 * Copyright 2017
 */
@Component
public class TransformController implements Initializable {

    @FXML
    private ListView<File> resultTransformListView;
    @FXML
    private Button addResultTransform;
    @FXML
    private Button removeResultTransform;
    @FXML
    private Button addDefaultJsonTransformer;
    @FXML
    private Button addDefaultCsvTransformer;
    @FXML
    private Label transformCount;
    @FXML
    private Label transformsLabel;

    private ApplicationFormState applicationFormState;
    private CommandMap<Boolean, NullSafeCommand> resultTransformCommandMap;
    private GlobalStage globalStage;
    private MessageSource messageSource;

    @Autowired
    public TransformController(final ApplicationFormState applicationFormState,
                               final GlobalStage globalStage,
                               final MessageSource messageSource,
                               @Qualifier("ResultTransformListViewCommandMap") final CommandMap<Boolean, NullSafeCommand> resultTransformCommandMap) {
        this.applicationFormState = applicationFormState;
        this.resultTransformCommandMap = resultTransformCommandMap;
        this.globalStage = globalStage;
        this.messageSource = messageSource;
    }

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        addResultTransform.setOnAction(this::handleAddResultTransform);
        removeResultTransform.setOnAction(this::handleRemoveResultTransform);
        addDefaultJsonTransformer.setText(messageSource.getMessage("basic.json.transform.label", null, Locale.getDefault()));
        addDefaultJsonTransformer.setOnAction(this::addJsonTransformer);
        addDefaultCsvTransformer.setText(messageSource.getMessage("basic.json.transform.label", null, Locale.getDefault()));
        addDefaultCsvTransformer.setOnAction(this::addCsvTransformer);
        resultTransformListView.getItems().add(applicationFormState.getDefaultJSONTransform());
        applicationFormState.setResultTransformObservableList(resultTransformListView.getItems());
        transformsLabel.setText(messageSource.getMessage("transforms.label", null, Locale.getDefault()));
    }

    @SuppressWarnings({"unchecked", "unused"})
    private void handleAddResultTransform(final ActionEvent actionEvent) {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(messageSource.getMessage("select.groovy.result.transform.label", null, Locale.getDefault()));
        final String[] validExtensions = {"*.groovy", "*.gvy", "*.gy", "*.gsh"};
        final FileChooser.ExtensionFilter groovyFileFilter = new FileChooser.ExtensionFilter("groovy files", validExtensions);
        fileChooser.getExtensionFilters().add(groovyFileFilter);
        final File file = fileChooser.showOpenDialog(globalStage.getStage());
        resultTransformCommandMap.getCommand(file != null).execute(resultTransformListView, file);
        applicationFormState.setResultTransformObservableList(resultTransformListView.getItems());
        transformCount.setText(Integer.toString(resultTransformListView.getItems().size()));
    }

    @SuppressWarnings({"unchecked", "unused"})
    private void handleRemoveResultTransform(final ActionEvent actionEvent) {
        final int selectedIndex = resultTransformListView.getSelectionModel().getSelectedIndex();
        resultTransformCommandMap.getCommand(selectedIndex != -1).execute(resultTransformListView, selectedIndex);
        applicationFormState.setResultTransformObservableList(resultTransformListView.getItems());
        transformCount.setText(Integer.toString(resultTransformListView.getItems().size()));
    }

    @SuppressWarnings("unused")
    private void addJsonTransformer(final ActionEvent actionEvent) {
        resultTransformListView.getItems().add(applicationFormState.getDefaultJSONTransform());
        applicationFormState.setResultTransformObservableList(resultTransformListView.getItems());
        transformCount.setText(Integer.toString(resultTransformListView.getItems().size()));
    }

    @SuppressWarnings("unused")
    private void addCsvTransformer(final ActionEvent actionEvent) {
        resultTransformListView.getItems().add(applicationFormState.getDefaultCSVTransform());
        applicationFormState.setResultTransformObservableList(resultTransformListView.getItems());
        transformCount.setText(Integer.toString(resultTransformListView.getItems().size()));
    }

}
