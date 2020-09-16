package com.github.adamyork.fx5p1d3r.common.service.progress;

import com.github.adamyork.fx5p1d3r.common.command.StepAddCommand;
import com.github.adamyork.fx5p1d3r.common.command.StepAssignCommand;
import com.github.adamyork.fx5p1d3r.common.command.StepCommand;
import javafx.application.Platform;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Adam York on 10/5/2017.
 * Copyright 2017
 */
@Component
public class ApplicationProgressService implements ProgressService {

    private final PropertyChangeSupport support;
    private final MessageSource messageSource;

    private ProgressState progressState;
    @SuppressWarnings("FieldCanBeLocal")
    private ProgressState previousState;
    private Map<ProgressType, String> messageMap;
    private Map<ProgressType, StepCommand> stepCommandMap;
    private ProgressType currentProgressType;

    @Inject
    public ApplicationProgressService(final MessageSource messageSource) {
        this.messageSource = messageSource;
        support = new PropertyChangeSupport(this);
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

    @Override
    public void addListener(final PropertyChangeListener pcl) {
        support.addPropertyChangeListener(pcl);
    }

    @Override
    public void removeListener(final PropertyChangeListener pcl) {
        support.removePropertyChangeListener(pcl);
    }

    @Override
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

    @Override
    public void updateProgress(final ProgressType progressType) {
        previousState = progressState;
        currentProgressType = progressType;
        progressState = stepCommandMap.get(progressType).execute(progressType, messageMap.get(progressType), previousState);
        Platform.runLater(() -> support.firePropertyChange("state", this.previousState, this.progressState));
    }

    @Override
    public ProgressType getCurrentProgressType() {
        return currentProgressType;
    }

    @Override
    public ProgressState getProgressState() {
        return progressState;
    }

}
