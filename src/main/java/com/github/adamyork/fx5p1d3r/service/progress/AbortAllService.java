package com.github.adamyork.fx5p1d3r.service.progress;

import org.springframework.stereotype.Component;

import javax.inject.Inject;

/**
 * Created by Adam York on 10/9/2017.
 * Copyright 2017
 */
@Component
public class AbortAllService implements AbortService {

    private final ApplicationProgressService progressService;

    @Inject
    public AbortAllService(final ApplicationProgressService progressService) {
        this.progressService = progressService;
    }

    @Override
    public void abort() {
        progressService.updateProgress(ProgressType.ABORT);
    }

}
