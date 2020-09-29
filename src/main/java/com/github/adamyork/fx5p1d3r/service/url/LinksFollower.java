package com.github.adamyork.fx5p1d3r.service.url;

import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * Created by Adam York on 8/28/2020.
 * Copyright 2020
 */
public interface LinksFollower {

    void traverse(final List<URL> urls, final ExecutorService executorService,
                  final int currentDepth,
                  final int maxDepth,
                  final int threadPoolSize);

}
