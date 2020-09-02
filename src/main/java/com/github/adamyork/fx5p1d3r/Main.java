package com.github.adamyork.fx5p1d3r;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.jooq.lambda.Unchecked;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.net.URL;
import java.util.Objects;
import java.util.Optional;

/**
 * Created by Adam York on 1/2/2018.
 * Copyright 2018
 */
@SpringBootApplication
public class Main extends Application {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    private ConfigurableApplicationContext applicationContext;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void init() {
        System.setProperty("java.awt.headless", "false");
        applicationContext = SpringApplication.run(Main.class);
    }

    @Override
    public void start(final Stage stage) {
        LogDirectoryHelper.manage();
        stage.setTitle("5p1d3r");
        stage.getIcons().addAll(
                new Image(Objects.requireNonNull(getClass().getClassLoader()
                        .getResourceAsStream("image/icon16.png"))),
                new Image(Objects.requireNonNull(getClass()
                        .getClassLoader().getResourceAsStream("image/icon32.png"))),
                new Image(Objects.requireNonNull(getClass()
                        .getClassLoader().getResourceAsStream("image/icon64.png"))));

        final FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/landing.fxml"));
        fxmlLoader.setControllerFactory(applicationContext::getBean);
        final GlobalStage globalStage = applicationContext.getBean(GlobalStage.class);
        globalStage.setStage(stage);

        final Parent landingRoot = (Parent) Unchecked.function(o -> fxmlLoader.load()).apply(null);
        final Scene landingScene = new Scene(landingRoot, 400, 832);
        final URL url = getClass().getClassLoader().getResource("css/landing.css");
        final URL nullSafeUrl = Optional.ofNullable(url)
                .orElse(Unchecked.function(o -> new URL("http://www.123456789010101.com"))
                        .apply(null));

        landingScene.getStylesheets().addAll(nullSafeUrl.toExternalForm());
        stage.setScene(landingScene);
        stage.setResizable(false);
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        applicationContext.close();
    }
}

//TODO finish native packaging
//TODO replace observer pattern
//TODO REWORK groovy transform dir
//TODO fix ui locking bug