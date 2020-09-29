package com.github.adamyork.fx5p1d3r.view;

import com.github.adamyork.fx5p1d3r.GlobalStage;
import com.github.adamyork.fx5p1d3r.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import org.jooq.lambda.Unchecked;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.awt.*;
import java.net.URI;
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
        aboutButton.setOnAction(this::handleAbout);
        helpButton.setOnAction(this::handleHelp);
        startButton.setText(messageSource.getMessage("start.label", null, Locale.getDefault()));
        aboutButton.setText(messageSource.getMessage("about.label", null, Locale.getDefault()));
        helpButton.setText(messageSource.getMessage("help.label", null, Locale.getDefault()));
    }

    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    private void handleBasicSelection(final ActionEvent actionEvent) {
        final Parent basicRoot;
        final FXMLLoader fxmlLoader = Unchecked.function(a -> new FXMLLoader(Main.class.getClassLoader().getResource("fxml/application.fxml"))).apply(null);
        fxmlLoader.setControllerFactory(applicationContext::getBean);
        basicRoot = (Parent) Unchecked.function(o -> fxmlLoader.load()).apply(null);
        final Scene basicScene = new Scene(basicRoot, 400, 832);
        final URL url = getClass().getClassLoader().getResource("css/application.css");
        final URL nullSafeURL = Optional.ofNullable(url)
                .orElse(Unchecked.function(o -> new URL("http://www.123456789010101.com"))
                        .apply(null));
        basicScene.getStylesheets().addAll(nullSafeURL.toExternalForm());
        globalStage.getStage().setScene(basicScene);
    }

    private void handleAbout(final ActionEvent actionEvent) {
        final Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(messageSource.getMessage("alert.service.about.label", null, Locale.getDefault()));
        alert.setHeaderText("5p1d3r");
        alert.setContentText(buildAboutContent());
        alert.show();
    }

    private String buildAboutContent() {
        final AnnotationConfigApplicationContext configContext = (AnnotationConfigApplicationContext) applicationContext;
        String versionString;
        String dateString;
        try {
            versionString = configContext.getBeanFactory().resolveEmbeddedValue("${info.build.version}");
            dateString = configContext.getBeanFactory().resolveEmbeddedValue("${info.build.date}");
        } catch (final Exception exception) {
            versionString = "unknown";
            dateString = "unknown";
        }
        return messageSource.getMessage("about.build.version.label", null, Locale.getDefault()) +
                ": " +
                versionString +
                "\n" +
                messageSource.getMessage("about.build.date.label", null, Locale.getDefault()) +
                ": " +
                dateString +
                "\n" +
                messageSource.getMessage("about.repository.label", null, Locale.getDefault()) +
                ": " +
                "http://www.github.com/adamyork/5p1d3r" +
                "\n" +
                "\n" +
                messageSource.getMessage("about.disclaimer", null, Locale.getDefault());
    }

    private void handleHelp(final ActionEvent actionEvent) {
        Unchecked.consumer(o -> Desktop.getDesktop().browse(new URI("https://github.com/adamyork/5p1d3r#how-to"))).accept(null);
    }

}
