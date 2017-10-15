package com.github.adamyork.fx5p1d3r.landing;

import com.github.adamyork.fx5p1d3r.GlobalStage;
import com.github.adamyork.fx5p1d3r.Main;
import com.github.adamyork.fx5p1d3r.common.NullSafe;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import org.jooq.lambda.Unchecked;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Adam York on 2/20/2017.
 * Copyright 2017
 */
@Component
public class LandingController implements Initializable, ApplicationContextAware {

    private final GlobalStage globalStage;
    @FXML
    private Button basicButton;
    private ApplicationContext applicationContext;

    @Autowired
    public LandingController(final GlobalStage globalStage) {
        this.globalStage = globalStage;
    }

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        basicButton.setOnAction(this::handleBasicSelection);
    }

    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    private void handleBasicSelection(@SuppressWarnings("unused") final ActionEvent actionEvent) {
        final Parent basicRoot;
        final FXMLLoader fxmlLoader = Unchecked.function(a -> new FXMLLoader(Main.class.getClassLoader().getResource("application.fxml"))).apply(null);
        fxmlLoader.setControllerFactory(applicationContext::getBean);
        basicRoot = (Parent) Unchecked.function(o -> fxmlLoader.load()).apply(null);
        final Scene basicScene = new Scene(basicRoot, 400, 832);
        final URL url = getClass().getClassLoader().getResource("application.css");
        final NullSafe<URL> nullSafe = new NullSafe<>(URL.class);
        final URL nullSafeURL = nullSafe.getNullSafe(url);
        basicScene.getStylesheets().addAll(nullSafeURL.toExternalForm());
        globalStage.getStage().setScene(basicScene);

    }

}
