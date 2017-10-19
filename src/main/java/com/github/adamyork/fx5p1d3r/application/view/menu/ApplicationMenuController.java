package com.github.adamyork.fx5p1d3r.application.view.menu;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.github.adamyork.fx5p1d3r.common.model.ApplicationFormState;
import javafx.event.ActionEvent;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.layout.FlowPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.jooq.lambda.Unchecked;

import java.io.File;

/**
 * Created by Adam York on 3/5/2017.
 * Copyright 2017
 */

public class ApplicationMenuController {

    private Stage stage;
    private ApplicationFormState applicationFormState;

    public ApplicationMenuController(final Stage stage, final FlowPane flowPane, final ApplicationFormState applicationFormState) {
        this.stage = stage;
        this.applicationFormState = applicationFormState;
        final MenuBar menuBar = new MenuBar();
        menuBar.useSystemMenuBarProperty().set(true);
        menuBar.setPrefWidth(400);
        flowPane.getChildren().add(0, menuBar);

        final Menu menu = new Menu("File");
        final MenuItem loadItem = new MenuItem("Load");
        final MenuItem saveItem = new MenuItem("Save");
        final SeparatorMenuItem separator = new SeparatorMenuItem();
        final MenuItem exitItem = new MenuItem("Exit");

        menu.getItems().add(loadItem);
        menu.getItems().add(saveItem);
        menu.getItems().add(separator);
        menu.getItems().add(exitItem);
        menuBar.getMenus().add(menu);

        exitItem.setOnAction(this::handleExitSelected);
        saveItem.setOnAction(this::handleSave);
        loadItem.setOnAction(this::handleLoad);
    }

    @SuppressWarnings("unused")
    private void handleExitSelected(final ActionEvent actionEvent) {
        stage.close();
    }

    @SuppressWarnings("unused")
    private void handleSave(final ActionEvent actionEvent) {
        final ObjectMapper mapper = new ObjectMapper();
        final String stateString = Unchecked.function(state -> mapper.writeValueAsString(applicationFormState)).apply(null);
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save configuration as...");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("json", "*.json")
        );
        final File file = fileChooser.showSaveDialog(stage);
        //TODO COMMAND
        if (file == null) {
            return;
        }
        Unchecked.consumer(consumer -> mapper.writerWithDefaultPrettyPrinter().writeValue(file, applicationFormState)).accept(null);
    }

    @SuppressWarnings("unused")
    private void handleLoad(final ActionEvent actionEvent) {
        final ObjectMapper mapper = new ObjectMapper();
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open configuration");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("json", "*.json")
        );
        final File file = fileChooser.showOpenDialog(stage);
        //TODO COMMAND
        if (file == null) {
            return;
        }
        mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
        final SimpleModule module = new SimpleModule();
        module.addDeserializer(ApplicationFormState.class, new FormStateDeserializer(applicationFormState));
        mapper.registerModule(module);
        final ApplicationFormState state = Unchecked.function(func -> mapper.readValue(file, ApplicationFormState.class)).apply(null);
        state.notifyChanged();
    }
}
