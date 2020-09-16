package com.github.adamyork.fx5p1d3r.common.service;

import com.github.adamyork.fx5p1d3r.common.service.progress.ApplicationProgressService;
import com.github.adamyork.fx5p1d3r.common.service.progress.ProgressType;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.beans.PropertyChangeSupport;

/**
 * Created by Adam York on 10/9/2017.
 * Copyright 2017
 */
@Component
public class AbortAllService implements AbortService {

    private final ApplicationProgressService progressService;

    private PropertyChangeSupport support;

    @Inject
    public AbortAllService(final ApplicationProgressService progressService) {
        this.progressService = progressService;
    }

    @Override
    public void abort() {
        progressService.updateProgress(ProgressType.ABORT);
    }

}
