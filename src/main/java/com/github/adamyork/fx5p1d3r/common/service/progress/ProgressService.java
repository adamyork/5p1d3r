package com.github.adamyork.fx5p1d3r.common.service.progress;

import com.github.adamyork.fx5p1d3r.common.command.StepAddCommand;
import com.github.adamyork.fx5p1d3r.common.command.StepAssignCommand;
import com.github.adamyork.fx5p1d3r.common.command.StepCommand;
import javafx.application.Platform;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;

/**
 * Created by Adam York on 10/5/2017.
 * Copyright 2017
 */
@Component
public class ProgressService extends Observable {

    private static final String APPLICATION_START = "starting";
    private static final String VALIDATING_URL = "validating urls";
    private static final String FETCHING_RESOURCE = "fetching resource";
    private static final String RESOURCE_RETRIEVED = "resource retrieved";
    private static final String RUNNING_SELECTORS = "running selectors";
    private static final String RUNNING_TRANSFORMS = "running transforms";
    private static final String WRITING_OUTPUT = "writing output";
    private static final String FOLLOW_LINKS = "following links";
    private static final String APPLICATION_COMPLETE = "complete";
    private static final String APPLICATION_ABORTED = "aborted process due to error";

    private Map<ProgressType, String> messageMap;
    private ProgressState progressState;
    private ProgressState previousState;
    private Map<ProgressType, StepCommand> stepCommandMap;

    @PostConstruct
    public void initialize() {
        messageMap = new HashMap<>();
        messageMap.put(ProgressType.START, APPLICATION_START);
        messageMap.put(ProgressType.VALIDATE, VALIDATING_URL);
        messageMap.put(ProgressType.FETCH, FETCHING_RESOURCE);
        messageMap.put(ProgressType.RETRIEVED, RESOURCE_RETRIEVED);
        messageMap.put(ProgressType.SELECTOR, RUNNING_SELECTORS);
        messageMap.put(ProgressType.TRANSFORM, RUNNING_TRANSFORMS);
        messageMap.put(ProgressType.OUTPUT, WRITING_OUTPUT);
        messageMap.put(ProgressType.LINKS, FOLLOW_LINKS);
        messageMap.put(ProgressType.COMPLETE, APPLICATION_COMPLETE);
        messageMap.put(ProgressType.ABORT, APPLICATION_ABORTED);
    }

    public void updateSteps(final int size) {
        final double stepValue = 1.0 / (size * 2);
        stepCommandMap = new HashMap<>();
        stepCommandMap.put(ProgressType.START, new StepAssignCommand());
        stepCommandMap.put(ProgressType.VALIDATE, new StepAddCommand(stepValue));
        stepCommandMap.put(ProgressType.FETCH, new StepAddCommand(stepValue));
        stepCommandMap.put(ProgressType.RETRIEVED, new StepAddCommand(stepValue));
        stepCommandMap.put(ProgressType.SELECTOR, new StepAddCommand(stepValue));
        stepCommandMap.put(ProgressType.TRANSFORM, new StepAddCommand(stepValue));
        stepCommandMap.put(ProgressType.OUTPUT, new StepAddCommand(stepValue));
        stepCommandMap.put(ProgressType.LINKS, new StepAddCommand(stepValue));
        stepCommandMap.put(ProgressType.COMPLETE, new StepAssignCommand());
        stepCommandMap.put(ProgressType.ABORT, new StepAssignCommand());
    }

    public void updateProgress(final ProgressType progressType) {
        previousState = progressState;
        progressState = stepCommandMap.get(progressType).execute(progressType, messageMap.get(progressType), previousState);
        setChanged();
        Platform.runLater(this::notifyObservers);
    }

    public ProgressState getProgressState() {
        clearChanged();
        return progressState;
    }

}
