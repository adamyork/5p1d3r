package com.github.adamyork.fx5p1d3r.application;

import com.github.adamyork.fx5p1d3r.GlobalStage;
import com.github.adamyork.fx5p1d3r.application.view.menu.ApplicationMenuController;
import com.github.adamyork.fx5p1d3r.common.model.ApplicationFormState;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.FlowPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Component
public class ApplicationController implements Initializable {

    @FXML
    private FlowPane applicationFlowPane;

    private GlobalStage globalStage;
    private ApplicationFormState applicationFormState;

    @Autowired
    public ApplicationController(final GlobalStage globalStage,
                                 final ApplicationFormState applicationFormState) {
        this.globalStage = globalStage;
        this.applicationFormState = applicationFormState;
    }

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        new ApplicationMenuController(globalStage.getStage(), applicationFlowPane, applicationFormState);
    }

}
