package com.github.adamyork.fx5p1d3r.service.url;

import com.github.adamyork.fx5p1d3r.ApplicationFormState;
import com.github.adamyork.fx5p1d3r.service.progress.AlertService;
import com.github.adamyork.fx5p1d3r.service.progress.ApplicationProgressService;
import com.github.adamyork.fx5p1d3r.service.progress.ProgressType;
import com.github.adamyork.fx5p1d3r.service.url.data.UrlValidationResult;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jooq.lambda.Unchecked;
import org.springframework.context.MessageSource;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Adam York on 2/23/2017.
 * Copyright 2017
 */
public class HybridDataService implements DataService {

    private static final Logger logger = LogManager.getLogger(HybridDataService.class);

    private final ApplicationFormState applicationFormState;
    private final UrlService urlUrlService;
    private final ApplicationProgressService progressService;
    private final MessageSource messageSource;
    private final AlertService alertService;
    private final ThreadService singleThreadService;
    private final ThreadService multiThreadService;

    public HybridDataService(final ApplicationFormState applicationFormState,
                             final UrlService urlUrlService,
                             final ApplicationProgressService progressService,
                             final MessageSource messageSource,
                             final AlertService alertService,
                             final ThreadService singleThreadService,
                             final ThreadService multiThreadService) {
        this.applicationFormState = applicationFormState;
        this.urlUrlService = urlUrlService;
        this.progressService = progressService;
        this.messageSource = messageSource;
        this.alertService = alertService;
        this.singleThreadService = singleThreadService;
        this.multiThreadService = multiThreadService;
    }

    @Override
    public void executeSingle() {
        final List<String> urlStrings = new ArrayList<>();
        urlStrings.add(applicationFormState.getStartingUrl());
        logger.debug("Crawling single URL " + applicationFormState.getStartingUrl());
        progressService.updateSteps(urlStrings.size());
        progressService.updateProgress(ProgressType.START);
        validateUrlsAndExecute(urlStrings);
    }

    @Override
    public void executeList() {
        logger.debug("Crawling URL list");
        final File urlListFile = applicationFormState.getUrlListFile();
        final String header = messageSource.getMessage("error.no.url.list.header", null, Locale.getDefault());
        final String content = messageSource.getMessage("error.no.url.list.content", null, Locale.getDefault());
        if (urlListFile == null) {
            alertService.error(header, content);
            //TODO this probably needs to reset the UI state
        } else {
            final List<String> urlStrings = Unchecked.function(o -> FileUtils.readLines(urlListFile, StandardCharsets.UTF_8))
                    .apply(null);
            progressService.updateSteps(urlStrings.size());
            progressService.updateProgress(ProgressType.START);
            validateUrlsAndExecute(urlStrings);
        }
    }

    private void validateUrlsAndExecute(final List<String> urlStrings) {
        final UrlValidationResult validationResult = urlUrlService.validateUrls(urlStrings);
        if (validationResult.isValid()) {
            if (applicationFormState.multithreading()) {
                multiThreadService.execute(validationResult.getUrls());
            } else {
                singleThreadService.execute(validationResult.getUrls());
            }
        } else {
            final String header = messageSource.getMessage("error.url.invalid.header", null, Locale.getDefault());
            final String content = messageSource.getMessage("error.url.invalid.content", null, Locale.getDefault());
            alertService.error(header, content);
            //TODO this probably needs to reset the UI state
        }
    }

}
