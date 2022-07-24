package com.github.adamyork.fx5p1d3r.view;

import com.github.adamyork.fx5p1d3r.ApplicationFormState;
import com.github.adamyork.fx5p1d3r.GlobalStage;
import com.github.adamyork.fx5p1d3r.service.progress.ProgressService;
import com.github.adamyork.fx5p1d3r.service.progress.ProgressType;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
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
import java.util.ResourceBundle;

/**
 * Created by Adam York on 3/24/2017.
 * Copyright 2017
 */
@Component
public class TransformController implements Initializable, PropertyChangeListener, Closeable {

    private static final Logger logger = LogManager.getLogger(TransformController.class);

    private final ApplicationFormState applicationFormState;
    private final GlobalStage globalStage;
    private final MessageSource messageSource;
    private final ProgressService progressService;

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
    @FXML
    private Tooltip addResultTransformToolTip;
    @FXML
    private Tooltip removeResultTransformToolTip;
    @FXML
    private Tooltip addDefaultJsonTransformerToolTip;
    @FXML
    private Tooltip addDefaultCsvTransformerToolTip;

    @Inject
    public TransformController(final ApplicationFormState applicationFormState,
                               final GlobalStage globalStage,
                               final MessageSource messageSource,
                               final ProgressService progressService) {
        this.applicationFormState = applicationFormState;
        this.globalStage = globalStage;
        this.messageSource = messageSource;
        this.progressService = progressService;
    }

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        addResultTransform.setOnAction(this::handleAddResultTransform);
        removeResultTransform.setOnAction(this::handleRemoveResultTransform);
        addDefaultJsonTransformer.setText(messageSource.getMessage("basic.json.transform.label", null, Locale.getDefault()));
        addDefaultJsonTransformer.setOnAction(this::addJsonTransformer);
        addDefaultCsvTransformer.setText(messageSource.getMessage("basic.csv.transform.label", null, Locale.getDefault()));
        addDefaultCsvTransformer.setOnAction(this::addCsvTransformer);
        resultTransformListView.getItems().add(applicationFormState.getDefaultJsonTransform());
        applicationFormState.setResultTransformObservableList(resultTransformListView.getItems());
        transformsLabel.setText(messageSource.getMessage("transforms.label", null, Locale.getDefault()));
        addResultTransformToolTip.setText(messageSource.getMessage("tooltip.transform.result.add", null, Locale.getDefault()));
        removeResultTransformToolTip.setText(messageSource.getMessage("tooltip.transform.result.remove", null, Locale.getDefault()));
        addDefaultJsonTransformerToolTip.setText(messageSource.getMessage("tooltip.transform.result.json", null, Locale.getDefault()));
        addDefaultCsvTransformerToolTip.setText(messageSource.getMessage("tooltip.transform.result.csv", null, Locale.getDefault()));
        progressService.addListener(this);
    }

    private void handleAddResultTransform(final ActionEvent actionEvent) {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(messageSource.getMessage("select.groovy.result.transform.label", null, Locale.getDefault()));
        final String[] validExtensions = {"*.groovy", "*.gvy", "*.gy", "*.gsh"};
        final FileChooser.ExtensionFilter groovyFileFilter = new FileChooser.ExtensionFilter("groovy files", validExtensions);
        fileChooser.getExtensionFilters().add(groovyFileFilter);
        final File file = fileChooser.showOpenDialog(globalStage.getStage());
        if (file != null) {
            resultTransformListView.getItems().add(file);
        }
        applicationFormState.setResultTransformObservableList(resultTransformListView.getItems());
        transformCount.setText(Integer.toString(resultTransformListView.getItems().size()));
    }

    private void handleRemoveResultTransform(final ActionEvent actionEvent) {
        final int selectedIndex = resultTransformListView.getSelectionModel().getSelectedIndex();
        if (selectedIndex != -1) {
            resultTransformListView.getItems().remove(selectedIndex);
        }
        applicationFormState.setResultTransformObservableList(resultTransformListView.getItems());
        transformCount.setText(Integer.toString(resultTransformListView.getItems().size()));
    }

    private void addJsonTransformer(final ActionEvent actionEvent) {
        resultTransformListView.getItems().add(applicationFormState.getDefaultJsonTransform());
        applicationFormState.setResultTransformObservableList(resultTransformListView.getItems());
        transformCount.setText(Integer.toString(resultTransformListView.getItems().size()));
    }

    private void addCsvTransformer(final ActionEvent actionEvent) {
        resultTransformListView.getItems().add(applicationFormState.getDefaultCsvTransform());
        applicationFormState.setResultTransformObservableList(resultTransformListView.getItems());
        transformCount.setText(Integer.toString(resultTransformListView.getItems().size()));
    }

    @Override
    public void propertyChange(final PropertyChangeEvent evt) {
        if (progressService.getCurrentProgressType().equals(ProgressType.COMPLETE) ||
                progressService.getCurrentProgressType().equals(ProgressType.ABORT)) {
            addResultTransform.setDisable(false);
            removeResultTransform.setDisable(false);
            addDefaultJsonTransformer.setDisable(false);
            addDefaultCsvTransformer.setDisable(false);
        } else {
            addResultTransform.setDisable(true);
            removeResultTransform.setDisable(true);
            addDefaultJsonTransformer.setDisable(true);
            addDefaultCsvTransformer.setDisable(true);
        }
    }

    @Override
    public void close() {
        logger.info("closing transform controller file lists");
        resultTransformListView.getItems().clear();
        logger.info("resultTransformListView items cleared");

    }
}
