package com.github.adamyork.fx5p1d3r.application.command.io;

import com.github.adamyork.fx5p1d3r.common.command.io.ExecutorCommand;
import com.github.adamyork.fx5p1d3r.common.service.AbortService;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.concurrent.ExecutorService;

/**
 * Created by Adam York on 1/2/2018.
 * Copyright 2018
 */
@Component
public class CleanUpExecutorCommand implements ExecutorCommand {

    private final AbortService abortService;

    @Inject
    public CleanUpExecutorCommand(final AbortService abortService) {
        this.abortService = abortService;
    }

    @Override
    public void execute(final ExecutorService executorService) {
        executorService.shutdownNow();
        abortService.clear();
    }
}
