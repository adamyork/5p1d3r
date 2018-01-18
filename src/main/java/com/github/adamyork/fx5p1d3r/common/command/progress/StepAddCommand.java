package com.github.adamyork.fx5p1d3r.common.command.progress;

import com.github.adamyork.fx5p1d3r.common.service.progress.ProgressState;
import com.github.adamyork.fx5p1d3r.common.service.progress.ProgressType;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Adam York on 10/7/2017.
 * Copyright 2017
 */
public class StepAddCommand implements StepCommand {

    private final Map<ProgressType, Double> stepMap;

    public StepAddCommand(final double stepValue) {
        stepMap = new HashMap<>();
        stepMap.put(ProgressType.FETCH, stepValue);
        stepMap.put(ProgressType.VALIDATE, 0.0);
        stepMap.put(ProgressType.FETCH, 0.0);
        stepMap.put(ProgressType.RETRIEVED, 0.0);
        stepMap.put(ProgressType.SELECTOR, 0.0);
        stepMap.put(ProgressType.TRANSFORM, 0.0);
        stepMap.put(ProgressType.OUTPUT, 0.0);
        stepMap.put(ProgressType.LINKS, 0.0);
        stepMap.put(ProgressType.RETRIEVED, stepValue);
    }

    @Override
    public ProgressState execute(final ProgressType progressType, final String message, final ProgressState previousState) {
        final ProgressState progressState = new ProgressState();
        progressState.setMessage(message);
        progressState.setProgress(previousState.getProgress() + stepMap.get(progressType));
        return progressState;
    }
}
