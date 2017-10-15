package com.github.adamyork.fx5p1d3r.common.service;

import com.github.adamyork.fx5p1d3r.common.model.ApplicationFormState;
import com.github.adamyork.fx5p1d3r.common.service.progress.ProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.List;

/**
 * Created by Adam York on 4/3/2017.
 * Copyright 2017
 */
@Component
public class URLServiceFactory {

    private ApplicationFormState applicationFormState;
    private ProgressService progressService;

    @Autowired
    public URLServiceFactory(final ApplicationFormState applicationFormState, final ProgressService progressService) {
        this.applicationFormState = applicationFormState;
        this.progressService = progressService;
    }

    public ConcurrentURLService getConcurrentServiceForURLs(final List<URL> urls, final int threadPoolSize) {
        return new ConcurrentURLService(urls, threadPoolSize, progressService);
    }

    public ThrottledURLService getThrottledServiceForURLs(final List<URL> urls) {
        return new ThrottledURLService(urls, applicationFormState, progressService);
    }
}
