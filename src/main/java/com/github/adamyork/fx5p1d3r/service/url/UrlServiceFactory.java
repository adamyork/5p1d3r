package com.github.adamyork.fx5p1d3r.service.url;

import com.github.adamyork.fx5p1d3r.ApplicationFormState;
import com.github.adamyork.fx5p1d3r.service.progress.ProgressService;

import java.net.URL;
import java.util.List;

/**
 * Created by Adam York on 4/3/2017.
 * Copyright 2017
 */
public class UrlServiceFactory {

    private final ApplicationFormState applicationFormState;
    private final ProgressService progressService;

    public UrlServiceFactory(final ApplicationFormState applicationFormState, final ProgressService progressService) {
        this.applicationFormState = applicationFormState;
        this.progressService = progressService;
    }

    public ConcurrentUrlTask getConcurrentServiceForUrls(final List<URL> urls,
                                                         final int currentDepth,
                                                         final int maxDepth,
                                                         final int threadPoolSize) {
        return new ConcurrentUrlTask(urls, threadPoolSize, currentDepth, maxDepth, progressService, applicationFormState);
    }

    public SequentialUrlTask getSequentialServiceForUrls(final List<URL> urls) {
        return new SequentialUrlTask(urls, applicationFormState, progressService);
    }

    public SequentialDownloadTask getSequentialDownloadServiceForUrls(final List<URL> urls) {
        return new SequentialDownloadTask(urls, applicationFormState, progressService);
    }
}
