package com.github.adamyork.fx5p1d3r.service.progress;

import javafx.application.Platform;
import org.springframework.context.MessageSource;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.*;

/**
 * Created by Adam York on 10/5/2017.
 * Copyright 2017
 */
public class ApplicationProgressService implements ProgressService {

    protected final PropertyChangeSupport support;
    protected final MessageSource messageSource;

    protected ProgressState progressState;
    protected ProgressState previousState;
    protected Map<ProgressType, String> messageMap;
    protected Map<ProgressType, StepCommand> stepCommandMap;
    protected ProgressType currentProgressType;

    public ApplicationProgressService(final MessageSource messageSource) {
        this.messageSource = messageSource;
        support = new PropertyChangeSupport(this);
    }

    @SuppressWarnings("DuplicatedCode")
    @Override
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

    @SuppressWarnings("DuplicatedCode")
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
        if (progressState == null) {
            progressState = stepCommandMap.get(ProgressType.START)
                    .execute(progressType, messageMap.get(progressType), previousState);
        }
        previousState = progressState;
        currentProgressType = progressType;
        progressState = stepCommandMap.get(progressType).execute(progressType, messageMap.get(progressType), previousState);
        if (previousState != null && !previousState.equals(progressState)) {
            Platform.runLater(() -> support.firePropertyChange("state", this.previousState, this.progressState));
        }
    }

    @Override
    public void forceComplete() {
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                updateProgress(ProgressType.COMPLETE);
            }
        }, 1000);
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
