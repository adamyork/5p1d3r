package com.github.adamyork.fx5p1d3r.service.url;


import com.github.adamyork.fx5p1d3r.ApplicationFormState;
import com.github.adamyork.fx5p1d3r.service.progress.AlertService;
import com.github.adamyork.fx5p1d3r.service.progress.ApplicationProgressService;
import com.github.adamyork.fx5p1d3r.service.progress.ProgressType;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jooq.lambda.Unchecked;
import org.springframework.context.MessageSource;

import java.io.File;
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
                                final ApplicationProgressService progressService,
                                final MessageSource messageSource,
                                final AlertService alertService,
                                final CrawlerService singleThreadedCrawler,
                                final CrawlerService multiThreadedCrawler) {
        super(applicationFormState, urlUrlService, progressService, messageSource, alertService,
                singleThreadedCrawler, multiThreadedCrawler);
    }

    @Override
    public void execute() {
        logger.debug("Crawling URL list");
        final File urlListFile = applicationFormState.getUrlListFile();
        final String header = messageSource.getMessage("error.no.url.list.header", null, Locale.getDefault());
        final String content = messageSource.getMessage("error.no.url.list.content", null, Locale.getDefault());
        if (urlListFile == null) {
            logger.error("Crawling Aborted. Url file list is null.");
            alertService.error(header, content);
            progressService.updateSteps(0);
            progressService.updateProgress(ProgressType.ABORT);
        } else {
            final List<String> urlStrings = Unchecked.function(o -> FileUtils.readLines(urlListFile, StandardCharsets.UTF_8))
                    .apply(null);
            progressService.updateSteps(urlStrings.size());
            progressService.updateProgress(ProgressType.START);
            validateUrlsAndExecute(urlStrings);
        }
    }


}

