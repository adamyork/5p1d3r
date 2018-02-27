package com.github.adamyork.fx5p1d3r.common.command;

import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * Created by Adam York on 2/23/2017.
 * Copyright 2017
 */
public interface ApplicationCommand {

    void execute();

    void execute(final List<URL> urls);

    void execute(final List<URL> urls,
                 final ExecutorService executorService,
                 final int currentDepth,
                 final int maxDepth,
                 final int threadPoolSize);

}
