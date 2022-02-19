package com.github.adamyork.fx5p1d3r.service.url;

import com.github.adamyork.fx5p1d3r.ApplicationFormState;
import com.github.adamyork.fx5p1d3r.service.progress.AlertService;
import com.github.adamyork.fx5p1d3r.service.progress.ProgressService;
import com.github.adamyork.fx5p1d3r.service.progress.ProgressType;
import com.github.adamyork.fx5p1d3r.service.url.data.UrlValidationResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.MessageSource;

import java.util.List;
import java.util.Locale;

/**
 * Created by Adam York on 10/7/2020.
 * Copyright 2020
 */
public class BaseSpiderService {

    private static final Logger logger = LogManager.getLogger(BaseSpiderService.class);

    protected final ApplicationFormState applicationFormState;
    protected final UrlService urlUrlService;
    protected final ProgressService progressService;
    protected final MessageSource messageSource;
    protected final AlertService alertService;
    protected final CrawlerService singleThreadedCrawler;
    protected final CrawlerService multiThreadedCrawler;

    public BaseSpiderService(final ApplicationFormState applicationFormState,
                             final UrlService urlUrlService,
                             final ProgressService progressService,
                             final MessageSource messageSource,
                             final AlertService alertService,
                             final CrawlerService singleThreadedCrawler,
                             final CrawlerService multiThreadedCrawler) {
        this.applicationFormState = applicationFormState;
        this.urlUrlService = urlUrlService;
        this.progressService = progressService;
        this.messageSource = messageSource;
        this.alertService = alertService;
        this.singleThreadedCrawler = singleThreadedCrawler;
        this.multiThreadedCrawler = multiThreadedCrawler;
    }

    protected void validateUrlsAndExecute(final List<String> urlStrings) {
        final UrlValidationResult validationResult = urlUrlService.validateUrls(urlStrings);
        if (validationResult.isValid()) {
            if (applicationFormState.multithreading()) {
                multiThreadedCrawler.execute(validationResult.getUrls());
            } else {
                singleThreadedCrawler.execute(validationResult.getUrls());
            }
        } else {
            logger.error("Crawling Aborted. No valid urls to crawl.");
            final String header = messageSource.getMessage("error.url.invalid.header", null, Locale.getDefault());
            final String content = messageSource.getMessage("error.url.invalid.content", null, Locale.getDefault());
            alertService.error(header, content);
            progressService.updateProgress(ProgressType.ABORT);
        }
    }
}
