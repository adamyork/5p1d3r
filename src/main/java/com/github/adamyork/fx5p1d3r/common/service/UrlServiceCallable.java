package com.github.adamyork.fx5p1d3r.common.service;

import com.github.adamyork.fx5p1d3r.common.service.progress.ProgressService;
import com.github.adamyork.fx5p1d3r.common.service.progress.ProgressType;
import javafx.concurrent.Task;
import org.jooq.lambda.Unchecked;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.net.URL;

/**
 * Created by Adam York on 10/11/2017.
 * Copyright 2017
 */
class UrlServiceCallable extends Task<Document> {

    private final URL url;
    private final ProgressService progressService;

    UrlServiceCallable(final URL url,
                       final ProgressService progressService) {
        this.url = url;
        this.progressService = progressService;
    }

    @Override
    public Document call() throws Exception {
        progressService.updateProgress(ProgressType.FETCH);
        final Document document = Unchecked.function(urlToCall -> Jsoup.connect(urlToCall.toString())
                .userAgent(ThrottledUrlService.USER_AGENT)
                .timeout(30000)
                .get()).apply(url);
        progressService.updateProgress(ProgressType.RETRIEVED);
        return document;
    }
}
