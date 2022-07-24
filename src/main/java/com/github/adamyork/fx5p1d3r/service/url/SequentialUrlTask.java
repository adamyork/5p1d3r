package com.github.adamyork.fx5p1d3r.service.url;

import com.github.adamyork.fx5p1d3r.ApplicationFormState;
import com.github.adamyork.fx5p1d3r.LogDirectoryHelper;
import com.github.adamyork.fx5p1d3r.service.progress.ProgressService;
import com.github.adamyork.fx5p1d3r.service.progress.ProgressType;
import javafx.concurrent.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jooq.lambda.Unchecked;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Adam York on 4/4/2017.
 * Copyright 2017
 */
public class SequentialUrlTask extends Task<List<Document>> {

    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.2; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/32.0.1667.0 Safari/537.36";
    private static final Logger logger = LogManager.getLogger(SequentialUrlTask.class);

    private final List<URL> urls;
    private final ApplicationFormState applicationFormState;
    private final ProgressService progressService;

    public SequentialUrlTask(final List<URL> urls,
                             final ApplicationFormState applicationFormState,
                             final ProgressService progressService) {
        this.urls = urls;
        this.applicationFormState = applicationFormState;
        this.progressService = progressService;
    }

    @Override
    protected List<Document> call() {
        LogDirectoryHelper.manage();
        //final Whitelist wl = Whitelist.relaxed();
        //final Cleaner cleaner = new Cleaner(whitelist);
        //cleaned = cleaner.clean(dirty);
        //outputManager.outputToApplication("Document Cleaned...");
        final List<Document> documentList = new ArrayList<>();
        try {
            urls.forEach(url -> {
                logger.debug("Fetching url " + url);
                progressService.updateProgress(ProgressType.FETCH);
                try {
                    final Document document = Unchecked.function(urlToCall -> Jsoup.connect(urlToCall.toString())
                            .userAgent(USER_AGENT)
                            .timeout(30000)
                            .get()).apply(url);
                    documentList.add(document);
                } catch (final Exception exception) {
                    logger.debug("Error fetching url " + url);
                    logger.debug(exception);
                    logger.debug("Skipping url");
                }
                if (progressService.getCurrentProgressType().equals(ProgressType.ABORT)) {
                    throw new RuntimeException("Abort detected cancelling throttled request chain");
                } else {
                    if (applicationFormState.throttling()) {
                        final long requestDelay = applicationFormState.getThrottleMs().getValue();
                        logger.debug("Document fetched .. waiting " + requestDelay);
                        progressService.updateProgress(ProgressType.RETRIEVED);
                        Unchecked.consumer(o -> Thread.sleep(requestDelay)).accept(null);
                    } else {
                        logger.debug("Document fetched");
                        progressService.updateProgress(ProgressType.RETRIEVED);
                    }
                }
            });
        } catch (final Exception exception) {
            logger.debug("Abort detected cancelling, exception caught.");
        }
        return documentList;
    }
}