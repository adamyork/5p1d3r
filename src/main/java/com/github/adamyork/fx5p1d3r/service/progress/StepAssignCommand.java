package com.github.adamyork.fx5p1d3r.service.progress;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Adam York on 10/7/2017.
 * Copyright 2017
 */
public class StepAssignCommand implements StepCommand {

    private final Map<ProgressType, Double> stepMap;

    public StepAssignCommand() {
        stepMap = new HashMap<>();
        stepMap.put(ProgressType.START, 0.0);
        stepMap.put(ProgressType.ABORT, 1.0);
        stepMap.put(ProgressType.COMPLETE, 1.0);
    }

    @Override
    public ProgressState execute(final ProgressType progressType, final String message, final ProgressState previousState) {
        final ProgressState progressState = new ProgressState();
        progressState.setMessage(message);
        progressState.setProgress(stepMap.get(progressType));
        return progressState;
    }
}
