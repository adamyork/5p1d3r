package com.github.adamyork.fx5p1d3r.service.url;


import com.github.adamyork.fx5p1d3r.ApplicationFormState;
import com.github.adamyork.fx5p1d3r.service.progress.AlertService;
import com.github.adamyork.fx5p1d3r.service.progress.ProgressService;
import com.github.adamyork.fx5p1d3r.service.progress.ProgressType;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.MessageSource;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;

/**
 * Created by Adam York on 2/23/2017.
 * Copyright 2017
 */
public class UrlListSpiderService extends BaseSpiderService implements SpiderService {

    private static final Logger logger = LogManager.getLogger(SingleUrlSpiderService.class);


    public UrlListSpiderService(final ApplicationFormState applicationFormState,
                                final UrlService urlUrlService,
                                final ProgressService progressService,
                                final MessageSource messageSource,
                                final AlertService alertService,
                                final CrawlerService singleThreadedCrawler,
                                final CrawlerService multiThreadedCrawler) {
        super(applicationFormState, urlUrlService, progressService, messageSource, alertService,
                singleThreadedCrawler, multiThreadedCrawler);
    }

    @Override
    public void execute(final File urlListFile) {
        logger.debug("Crawling URL list");
        final String header = messageSource.getMessage("error.no.url.list.header", null, Locale.getDefault());
        final String content = messageSource.getMessage("error.no.url.list.content", null, Locale.getDefault());
        if (urlListFile == null) {
            logger.error("Crawling Aborted. Url file list is null.");
            alertService.error(header, content);
            progressService.updateSteps(0);
            progressService.updateProgress(ProgressType.ABORT);
        } else {
            try {
                final List<String> urlStrings = FileUtils.readLines(urlListFile, StandardCharsets.UTF_8);
                progressService.updateSteps(urlStrings.size());
                progressService.updateProgress(ProgressType.START);
                validateUrlsAndExecute(urlStrings);
            } catch (final IOException exception) {
                final String header2 = messageSource.getMessage("error.bad.url.list.header", null, Locale.getDefault());
                final String content2 = messageSource.getMessage("error.bad.url.list.content", null, Locale.getDefault());
                logger.error("Crawling Aborted. Url file list not found.", exception);
                alertService.error(header2, content2);
                progressService.updateSteps(0);
                progressService.updateProgress(ProgressType.ABORT);
            }
        }
    }


}

