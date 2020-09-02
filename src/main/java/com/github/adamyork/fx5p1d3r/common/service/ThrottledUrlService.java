package com.github.adamyork.fx5p1d3r.common.service;

import com.github.adamyork.fx5p1d3r.LogDirectoryHelper;
import com.github.adamyork.fx5p1d3r.common.model.ApplicationFormState;
import com.github.adamyork.fx5p1d3r.common.service.progress.ProgressService;
import com.github.adamyork.fx5p1d3r.common.service.progress.ProgressType;
import javafx.concurrent.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jooq.lambda.Unchecked;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Whitelist;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Adam York on 4/4/2017.
 * Copyright 2017
 */
public class ThrottledUrlService extends Task<List<Document>> {

    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.2; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/32.0.1667.0 Safari/537.36";
    private static final Logger logger = LogManager.getLogger(ThrottledUrlService.class);

    private final List<URL> urls;
    private final ApplicationFormState applicationFormState;
    private final ProgressService progressService;

    public ThrottledUrlService(final List<URL> urls,
                               final ApplicationFormState applicationFormState,
                               final ProgressService progressService) {
        this.urls = urls;
        this.applicationFormState = applicationFormState;
        this.progressService = progressService;
    }

    @Override
    protected List<Document> call() {
        LogDirectoryHelper.manage();
        final Whitelist whitelist = Whitelist.relaxed();
        //final Cleaner cleaner = new Cleaner(whitelist);
        //cleaned = cleaner.clean(dirty);
        //outputManager.outputToApplication("Document Cleaned...");
        final List<Document> documentList = new ArrayList<>();
        urls.forEach(url -> {
            logger.debug("Fetching url " + url);
            progressService.updateProgress(ProgressType.FETCH);
            final Document document = Unchecked.function(urlToCall -> Jsoup.connect(urlToCall.toString())
                    .userAgent(USER_AGENT)
                    .timeout(30000)
                    .get()).apply(url);
            documentList.add(document);
            final long requestDelay = applicationFormState.getThrottleMs().getValue();
            logger.debug("Document Fetched .. waiting " + requestDelay);
            progressService.updateProgress(ProgressType.RETRIEVED);
            Unchecked.consumer(o -> Thread.sleep(requestDelay)).accept(null);
        });
        return documentList;
    }
}