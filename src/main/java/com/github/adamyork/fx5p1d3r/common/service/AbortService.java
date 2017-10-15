package com.github.adamyork.fx5p1d3r.common.service;

import javafx.application.Platform;
import org.springframework.stereotype.Component;

import java.util.Observable;

/**
 * Created by Adam York on 10/9/2017.
 * Copyright 2017
 */
@Component
public class AbortService extends Observable {

    public void stopAllCalls() {
        setChanged();
        Platform.runLater(this::notifyObservers);
    }

    public void clear() {
        clearChanged();
    }
}
