package com.github.adamyork.fx5p1d3r.service.progress;

/**
 * Created by Adam York on 10/7/2017.
 * Copyright 2017
 */
public interface StepCommand {

    ProgressState execute(final ProgressType progressType, final String message, final ProgressState previousState);

}
