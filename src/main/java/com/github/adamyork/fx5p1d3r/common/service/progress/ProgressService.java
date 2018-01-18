package com.github.adamyork.fx5p1d3r.common.service.progress;

import com.github.adamyork.fx5p1d3r.common.command.progress.StepAddCommand;
import com.github.adamyork.fx5p1d3r.common.command.progress.StepAssignCommand;
import com.github.adamyork.fx5p1d3r.common.command.progress.StepCommand;
import javafx.application.Platform;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Observable;

/**
 * Created by Adam York on 10/5/2017.
 * Copyright 2017
 */
@Component
public class ProgressService extends Observable {

    private final MessageSource messageSource;
    private ProgressState progressState;
    @SuppressWarnings("FieldCanBeLocal")
    private ProgressState previousState;
    private Map<ProgressType, String> messageMap;
    private Map<ProgressType, StepCommand> stepCommandMap;

    @Inject
    public ProgressService(final MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @PostConstruct
    public void initialize() {
        messageMap = new HashMap<>();
        messageMap.put(ProgressType.START, messageSource.getMessage("starting.label", null, Locale.getDefault()));
        messageMap.put(ProgressType.VALIDATE, messageSource.getMessage("validating.urls.label", null, Locale.getDefault()));
        messageMap.put(ProgressType.FETCH, messageSource.getMessage("fetching.resource", null, Locale.getDefault()));
        messageMap.put(ProgressType.RETRIEVED, messageSource.getMessage("resource.retrieved", null, Locale.getDefault()));
        messageMap.put(ProgressType.SELECTOR, messageSource.getMessage("running.selectors", null, Locale.getDefault()));
        messageMap.put(ProgressType.TRANSFORM, messageSource.getMessage("running.transforms", null, Locale.getDefault()));
        messageMap.put(ProgressType.OUTPUT, messageSource.getMessage("writing.output.label", null, Locale.getDefault()));
        messageMap.put(ProgressType.LINKS, messageSource.getMessage("following.links", null, Locale.getDefault()));
        messageMap.put(ProgressType.COMPLETE, messageSource.getMessage("complete.label", null, Locale.getDefault()));
        messageMap.put(ProgressType.ABORT, messageSource.getMessage("aborted.process.due.to.error", null, Locale.getDefault()));
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
