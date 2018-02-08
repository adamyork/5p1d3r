package com.github.adamyork.fx5p1d3r.application;

import com.github.adamyork.fx5p1d3r.GlobalStage;
import com.github.adamyork.fx5p1d3r.application.view.menu.ApplicationMenuController;
import com.github.adamyork.fx5p1d3r.common.model.ApplicationFormState;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.FlowPane;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Adam York on 1/2/2018.
 * Copyright 2018
 */
@Component
public class ApplicationController implements Initializable {

    private final GlobalStage globalStage;
    private final ApplicationFormState applicationFormState;
    private final MessageSource messageSource;

    @FXML
    private FlowPane applicationFlowPane;

    @Inject
    public ApplicationController(final GlobalStage globalStage,
                                 final ApplicationFormState applicationFormState,
                                 final MessageSource messageSource) {
        this.globalStage = globalStage;
        this.applicationFormState = applicationFormState;
        this.messageSource = messageSource;
    }

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        new ApplicationMenuController(globalStage.getStage(), applicationFlowPane,
                applicationFormState, messageSource);
    }

}
