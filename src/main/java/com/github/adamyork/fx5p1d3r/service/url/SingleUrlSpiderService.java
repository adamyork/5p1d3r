package com.github.adamyork.fx5p1d3r.service.url;

import com.github.adamyork.fx5p1d3r.ApplicationFormState;
import com.github.adamyork.fx5p1d3r.service.progress.AlertService;
import com.github.adamyork.fx5p1d3r.service.progress.ProgressService;
import com.github.adamyork.fx5p1d3r.service.progress.ProgressType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.MessageSource;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Adam York on 2/23/2017.
 * Copyright 2017
 */
public class SingleUrlSpiderService extends BaseSpiderService implements SpiderService {

    private static final Logger logger = LogManager.getLogger(SingleUrlSpiderService.class);


    public SingleUrlSpiderService(final ApplicationFormState applicationFormState,
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
    public void execute() {
        logger.debug("Crawling single URL " + applicationFormState.getStartingUrl());
        final List<String> urlStrings = new ArrayList<>();
        urlStrings.add(applicationFormState.getStartingUrl());
        progressService.updateSteps(urlStrings.size());
        progressService.updateProgress(ProgressType.START);
        validateUrlsAndExecute(urlStrings);
    }

}
