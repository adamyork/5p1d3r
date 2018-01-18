package com.github.adamyork.fx5p1d3r.application.view.landing;

import com.github.adamyork.fx5p1d3r.GlobalStage;
import com.github.adamyork.fx5p1d3r.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import org.jooq.lambda.Unchecked;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.net.URL;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Created by Adam York on 2/20/2017.
 * Copyright 2017
 */
@Component
public class LandingController implements Initializable, ApplicationContextAware {

    private final GlobalStage globalStage;
    private final MessageSource messageSource;
    @FXML
    private Button startButton;
    @FXML
    private Button aboutButton;
    @FXML
    private Button helpButton;
    private ApplicationContext applicationContext;

    @Inject
    public LandingController(final GlobalStage globalStage,
                             final MessageSource messageSource) {
        this.globalStage = globalStage;
        this.messageSource = messageSource;
    }

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        startButton.setOnAction(this::handleBasicSelection);
        startButton.setText(messageSource.getMessage("start.label", null, Locale.getDefault()));
        aboutButton.setText(messageSource.getMessage("about.label", null, Locale.getDefault()));
        helpButton.setText(messageSource.getMessage("help.label", null, Locale.getDefault()));
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
        final URL nullSafeURL = Optional.ofNullable(url)
                .orElse(Unchecked.function(o -> new URL("http://www.123456789010101.com"))
                        .apply(null));
        basicScene.getStylesheets().addAll(nullSafeURL.toExternalForm());
        globalStage.getStage().setScene(basicScene);
    }

}
