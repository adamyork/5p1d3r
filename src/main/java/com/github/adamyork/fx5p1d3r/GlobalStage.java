package com.github.adamyork.fx5p1d3r;

import javafx.stage.Stage;
import org.springframework.stereotype.Component;

/**
 * Created by Adam York on 2/26/2017.
 * Copyright 2017
 */
@Component
public class GlobalStage {

    private Stage stage;

    public Stage getStage() {
        return stage;
    }

    void setStage(final Stage stage) {
        this.stage = (this.stage == null) ? this.stage = stage : this.stage;
    }
}
