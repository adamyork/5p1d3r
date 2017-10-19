package com.github.adamyork.fx5p1d3r;

import com.github.adamyork.fx5p1d3r.common.NullSafe;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.jooq.lambda.Unchecked;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.net.URL;

@SpringBootApplication
public class Main extends Application {

    private ConfigurableApplicationContext applicationContext;

    @Override
    public void init() throws Exception {
        System.setProperty("java.awt.headless", "false");
        applicationContext = SpringApplication.run(Main.class);
    }

    @Override
    public void start(final Stage stage) {
        stage.setTitle("5p1d3r");
        stage.getIcons().addAll(
                new Image(getClass().getClassLoader().getResourceAsStream("image/icon16.png")),
                new Image(getClass().getClassLoader().getResourceAsStream("image/icon32.png")),
                new Image(getClass().getClassLoader().getResourceAsStream("image/icon64.png")));

        final FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("landing.fxml"));
        fxmlLoader.setControllerFactory(applicationContext::getBean);
        final GlobalStage globalStage = applicationContext.getBean(GlobalStage.class);
        globalStage.setStage(stage);

        final Parent landingRoot = (Parent) Unchecked.function(o -> fxmlLoader.load()).apply(null);
        final Scene landingScene = new Scene(landingRoot, 400, 832);
        final URL url = getClass().getClassLoader().getResource("landing.css");
        final URL nullSafeURL = new NullSafe<>(URL.class).getNullSafe(url);

        landingScene.getStylesheets().addAll(nullSafeURL.toExternalForm());
        stage.setScene(landingScene);
        stage.setResizable(false);
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        applicationContext.close();
    }

    public static void main(String[] args) {
        launch(args);
    }
}