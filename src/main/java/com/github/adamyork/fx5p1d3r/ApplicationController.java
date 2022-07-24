package com.github.adamyork.fx5p1d3r;

import com.github.adamyork.fx5p1d3r.view.Closeable;
import com.github.adamyork.fx5p1d3r.view.menu.ApplicationMenuController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.FlowPane;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by Adam York on 1/2/2018.
 * Copyright 2018
 */
@Component
public class ApplicationController implements Initializable, ApplicationContextAware {

    private final GlobalStage globalStage;
    private final ApplicationFormState applicationFormState;
    private final MessageSource messageSource;
    private final List<Closeable> closeableList;

    private ApplicationContext applicationContext;

    @FXML
    private FlowPane applicationFlowPane;

    @Inject
    public ApplicationController(final GlobalStage globalStage,
                                 final ApplicationFormState applicationFormState,
                                 final MessageSource messageSource,
                                 final List<Closeable> closeableList) {
        this.globalStage = globalStage;
        this.applicationFormState = applicationFormState;
        this.messageSource = messageSource;
        this.closeableList = closeableList;
    }

    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        new ApplicationMenuController(globalStage.getStage(), applicationFlowPane,
                applicationFormState, messageSource, applicationContext, closeableList);
        applicationFlowPane.setOnMouseClicked(event -> applicationFlowPane.requestFocus());
    }

}
