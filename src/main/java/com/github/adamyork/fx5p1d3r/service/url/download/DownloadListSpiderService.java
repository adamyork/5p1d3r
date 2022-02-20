package com.github.adamyork.fx5p1d3r.service.url.download;

import com.github.adamyork.fx5p1d3r.ApplicationFormState;
import com.github.adamyork.fx5p1d3r.service.progress.AlertService;
import com.github.adamyork.fx5p1d3r.service.progress.ProgressService;
import com.github.adamyork.fx5p1d3r.service.progress.ProgressType;
import com.github.adamyork.fx5p1d3r.service.url.CrawlerService;
import com.github.adamyork.fx5p1d3r.service.url.UrlListSpiderService;
import com.github.adamyork.fx5p1d3r.service.url.UrlService;
import com.github.adamyork.fx5p1d3r.service.url.data.UrlValidationResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.MessageSource;

import java.util.List;
import java.util.Locale;

/**
 * Created by Adam York on 2/19/2022.
 * Copyright 2022
 */
public class DownloadListSpiderService extends UrlListSpiderService {

    private static final Logger logger = LogManager.getLogger(DownloadListSpiderService.class);

    public DownloadListSpiderService(final ApplicationFormState applicationFormState,
                                     final UrlService urlUrlService,
                                     final ProgressService progressService,
                                     final MessageSource messageSource,
                                     final AlertService alertService,
                                     final CrawlerService singleThreadedCrawler,
                                     final CrawlerService multiThreadedCrawler) {
        super(applicationFormState, urlUrlService, progressService, messageSource, alertService, singleThreadedCrawler, multiThreadedCrawler);
    }

    @Override
    protected void validateUrlsAndExecute(List<String> urlStrings) {
        final UrlValidationResult validationResult = urlUrlService.validateUrls(urlStrings);
        if (validationResult.isValid()) {
            if (applicationFormState.downloadMultithreading()) {
                multiThreadedCrawler.execute(validationResult.getUrls());
            } else {
                singleThreadedCrawler.execute(validationResult.getUrls());
            }
        } else {
            logger.error("Crawling Aborted. No valid urls to download.");
            final String header = messageSource.getMessage("error.url.invalid.header", null, Locale.getDefault());
            final String content = messageSource.getMessage("error.url.invalid.content", null, Locale.getDefault());
            alertService.error(header, content);
            progressService.updateProgress(ProgressType.ABORT);
        }
    }
}
