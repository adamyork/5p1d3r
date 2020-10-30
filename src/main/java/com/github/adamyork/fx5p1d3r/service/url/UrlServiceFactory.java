package com.github.adamyork.fx5p1d3r.service.url;

import com.github.adamyork.fx5p1d3r.ApplicationFormState;
import com.github.adamyork.fx5p1d3r.service.progress.ApplicationProgressService;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.net.URL;
import java.util.List;

/**
 * Created by Adam York on 4/3/2017.
 * Copyright 2017
 */
@Component
public class UrlServiceFactory {

    private final ApplicationFormState applicationFormState;
    private final ApplicationProgressService progressService;

    @Inject
    public UrlServiceFactory(final ApplicationFormState applicationFormState, final ApplicationProgressService progressService) {
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
}
