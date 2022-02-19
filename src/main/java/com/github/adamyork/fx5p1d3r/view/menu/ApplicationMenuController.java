package com.github.adamyork.fx5p1d3r.view.menu;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.github.adamyork.fx5p1d3r.ApplicationFormState;
import com.github.adamyork.fx5p1d3r.FormState;
import com.github.adamyork.fx5p1d3r.LogDirectoryHelper;
import com.github.adamyork.fx5p1d3r.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.image.Image;
import javafx.scene.layout.FlowPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jooq.lambda.Unchecked;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

/**
 * Created by Adam York on 3/5/2017.
 * Copyright 2017
 */
public class ApplicationMenuController {

    private static final Logger logger = LogManager.getLogger(ApplicationMenuController.class);

    private final Stage stage;
    private final ApplicationFormState applicationFormState;
    private final ApplicationContext applicationContext;

    public ApplicationMenuController(final Stage stage,
                                     final FlowPane flowPane,
                                     final ApplicationFormState applicationFormState,
                                     final MessageSource messageSource,
                                     final ApplicationContext applicationContext) {
        this.stage = stage;
        this.applicationFormState = applicationFormState;
        this.applicationContext = applicationContext;

        final MenuBar menuBar = new MenuBar();
        menuBar.useSystemMenuBarProperty().set(true);
        menuBar.setPrefWidth(400);
        flowPane.getChildren().add(0, menuBar);

        final Menu menu = new Menu(messageSource.getMessage("menu.file.label",
                null, Locale.getDefault()));
        final MenuItem loadItem = new MenuItem(messageSource.getMessage("menu.load.label",
                null, Locale.getDefault()));
        final MenuItem saveItem = new MenuItem(messageSource.getMessage("menu.save.label",
                null, Locale.getDefault()));
        final SeparatorMenuItem separator = new SeparatorMenuItem();
        final MenuItem exitItem = new MenuItem(messageSource.getMessage("menu.exit.label",
                null, Locale.getDefault()));

        final Menu toolsMenu = new Menu(messageSource.getMessage("menu.tools.label",
                null, Locale.getDefault()));
        final MenuItem downloaderItem = new MenuItem(messageSource.getMessage("menu.downloader.label",
                null, Locale.getDefault()));

        final Menu helpMenu = new Menu(messageSource.getMessage("menu.help.label",
                null, Locale.getDefault()));
        final MenuItem logsItem = new MenuItem(messageSource.getMessage("menu.logs.label",
                null, Locale.getDefault()));
        final MenuItem openLogsItem = new MenuItem(messageSource.getMessage("menu.logs.open.label",
                null, Locale.getDefault()));
        final MenuItem purgeLogsItems = new MenuItem(messageSource.getMessage("menu.logs.purge.label",
                null, Locale.getDefault()));


        menu.getItems().add(loadItem);
        menu.getItems().add(saveItem);
        menu.getItems().add(separator);
        menu.getItems().add(exitItem);

        toolsMenu.getItems().add(downloaderItem);

        helpMenu.getItems().add(logsItem);
        helpMenu.getItems().add(openLogsItem);
        helpMenu.getItems().add(purgeLogsItems);

        menuBar.getMenus().add(menu);
        menuBar.getMenus().add(toolsMenu);
        menuBar.getMenus().add(helpMenu);

        exitItem.setOnAction(this::handleExitSelected);
        saveItem.setOnAction(this::handleSave);
        loadItem.setOnAction(this::handleLoad);
        downloaderItem.setOnAction(this::handleLaunchDownloader);
        logsItem.setOnAction(this::handleLogs);
        openLogsItem.setOnAction(this::handleOpenLogsDir);
        purgeLogsItems.setOnAction(this::purgeLogs);
    }

    private void handleExitSelected(final ActionEvent actionEvent) {
        stage.close();
    }

    private void handleSave(final ActionEvent actionEvent) {
        final ObjectMapper mapper = new ObjectMapper();
        Unchecked.function(state -> mapper.writeValueAsString(applicationFormState)).apply(null);
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save configuration as...");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("json", "*.json")
        );
        final File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            Unchecked.consumer(consumer -> mapper.writerWithDefaultPrettyPrinter()
                            .writeValue(file, applicationFormState))
                    .accept(null);
        }
    }

    private void handleLoad(final ActionEvent actionEvent) {
        final ObjectMapper mapper = new ObjectMapper();
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open configuration");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("json", "*.json")
        );
        final File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
            final SimpleModule module = new SimpleModule();
            module.addDeserializer(ApplicationFormState.class, new FormStateDeserializer(applicationFormState));
            mapper.registerModule(module);
            final FormState state = Unchecked.function(func -> mapper.readValue(file, ApplicationFormState.class)).apply(null);
            state.notifyChanged();
        }
    }

    private void handleLogs(final ActionEvent actionEvent) {
        try {
            final String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                final String file = LogDirectoryHelper.getTempDirectoryPath() + "\\5p1d3r.log";
                Runtime.getRuntime().exec(new String[]{"cmd", "/c", "start", "cmd", "/k", "MORE " + file});
            }
        } catch (final IOException exception) {
            logger.error("Cant open log file", exception);
        }
    }

    private void handleOpenLogsDir(final ActionEvent actionEvent) {
        try {
            Desktop.getDesktop().open(new File(LogDirectoryHelper.getTempDirectoryPath().toString()));
        } catch (final IOException exception) {
            logger.error("Cant open log directory", exception);
        }
    }

    private void purgeLogs(final ActionEvent actionEvent) {
        final String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            final String file = LogDirectoryHelper.getTempDirectoryPath() + "\\5p1d3r.log";
            final File logs = new File(file);
            try {
                FileUtils.write(logs, "", Charset.defaultCharset());
                logger.info("Logs purged");
            } catch (final Exception exception) {
                logger.error("Logs cannot be purged", exception);
            }
        }
    }

    private void handleLaunchDownloader(final ActionEvent actionEvent){
        final FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getClassLoader().getResource("fxml/download.fxml"));
        fxmlLoader.setControllerFactory(applicationContext::getBean);
        final Parent parent = (Parent) Unchecked.function(o -> fxmlLoader.load()).apply(null);
        final Scene scene = new Scene(parent, 300, 400);
        final Stage stage = new Stage();
        final URL url = Main.class.getClassLoader().getResource("css/application.css");
        final URL nullSafeUrl = Optional.ofNullable(url)
                .orElse(Unchecked.function(o -> new URL("https://www.123456789010101.com"))
                        .apply(null));
        scene.getStylesheets().addAll(nullSafeUrl.toExternalForm());
        stage.setResizable(false);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setTitle("Bulk Downloader");
        stage.getIcons().addAll(
                new Image(Objects.requireNonNull(getClass().getClassLoader()
                        .getResourceAsStream("image/icon16.png"))),
                new Image(Objects.requireNonNull(getClass()
                        .getClassLoader().getResourceAsStream("image/icon32.png"))),
                new Image(Objects.requireNonNull(getClass()
                        .getClassLoader().getResourceAsStream("image/icon64.png"))));
        stage.setScene(scene);
        stage.show();
    }
}
