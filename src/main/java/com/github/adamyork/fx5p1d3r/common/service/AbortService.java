package com.github.adamyork.fx5p1d3r.common.service;

import com.github.adamyork.fx5p1d3r.common.service.progress.ProgressService;
import com.github.adamyork.fx5p1d3r.common.service.progress.ProgressType;
import javafx.application.Platform;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Observable;

/**
 * Created by Adam York on 10/9/2017.
 * Copyright 2017
 */
@Component
public class AbortService extends Observable {

    private final ProgressService progressService;

    @Inject
    public AbortService(final ProgressService progressService) {
        this.progressService = progressService;
    }

    public void stopAllCalls() {
        setChanged();
        progressService.updateProgress(ProgressType.ABORT);
        Platform.runLater(this::notifyObservers);
    }

    public void clear() {
        clearChanged();
    }
}
