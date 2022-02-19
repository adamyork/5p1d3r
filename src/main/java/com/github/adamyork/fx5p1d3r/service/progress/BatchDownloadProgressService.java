package com.github.adamyork.fx5p1d3r.service.progress;

import org.springframework.context.MessageSource;

import java.util.HashMap;
import java.util.Locale;

public class BatchDownloadProgressService extends ApplicationProgressService{

    public BatchDownloadProgressService(MessageSource messageSource) {
        super(messageSource);

    }

    @SuppressWarnings("DuplicatedCode")
    @Override
    public void initialize() {
        messageMap = new HashMap<>();
        messageMap.put(ProgressType.START, messageSource.getMessage("starting.label", null, Locale.getDefault()));
        messageMap.put(ProgressType.VALIDATE, messageSource.getMessage("validating.urls.label", null, Locale.getDefault()));
        messageMap.put(ProgressType.FETCH, messageSource.getMessage("fetching.resource", null, Locale.getDefault()));
        messageMap.put(ProgressType.RETRIEVED, messageSource.getMessage("resource.retrieved", null, Locale.getDefault()));
        messageMap.put(ProgressType.OUTPUT, messageSource.getMessage("writing.output.label", null, Locale.getDefault()));
        messageMap.put(ProgressType.COMPLETE, messageSource.getMessage("complete.label", null, Locale.getDefault()));
        messageMap.put(ProgressType.ABORT, messageSource.getMessage("aborted.process.due.to.error", null, Locale.getDefault()));
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
        stepCommandMap.put(ProgressType.OUTPUT, new StepAddCommand(stepValue));
        stepCommandMap.put(ProgressType.COMPLETE, new StepAssignCommand());
        stepCommandMap.put(ProgressType.ABORT, new StepAssignCommand());
    }
}
