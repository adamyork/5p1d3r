package com.github.adamyork.fx5p1d3r.common.command.io;

import java.util.concurrent.ExecutorService;

/**
 * Created by Adam York on 1/2/2018.
 * Copyright 2018
 */
public interface ExecutorCommand {

    void execute(final ExecutorService executorService);

}
