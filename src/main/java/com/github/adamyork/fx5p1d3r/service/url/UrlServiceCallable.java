package com.github.adamyork.fx5p1d3r.service.url;

import com.github.adamyork.fx5p1d3r.LogDirectoryHelper;
import com.github.adamyork.fx5p1d3r.service.progress.ApplicationProgressService;
import com.github.adamyork.fx5p1d3r.service.progress.ProgressService;
import com.github.adamyork.fx5p1d3r.service.progress.ProgressType;
import javafx.concurrent.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jooq.lambda.Unchecked;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.net.URL;

/**
 * Created by Adam York on 10/11/2017.
 * Copyright 2017
 */
class UrlServiceCallable extends Task<Document> {

    private static final Logger logger = LogManager.getLogger(UrlServiceCallable.class);

    private final URL url;
    private final ProgressService progressService;

    UrlServiceCallable(final URL url,
                       final ProgressService progressService) {
        this.url = url;
        this.progressService = progressService;
    }

    @Override
    public Document call() {
        LogDirectoryHelper.manage();
        progressService.updateProgress(ProgressType.FETCH);
        logger.debug("Fetching " + url);
        final Document document = Unchecked.function(urlToCall -> Jsoup.connect(urlToCall.toString())
                .userAgent(SequentialUrlTask.USER_AGENT)
                .timeout(30000)
                .get()).apply(url);
        progressService.updateProgress(ProgressType.RETRIEVED);
        logger.debug("Document fetched");
        return document;
    }

    public URL getUrl() {
        return url;
    }
}
