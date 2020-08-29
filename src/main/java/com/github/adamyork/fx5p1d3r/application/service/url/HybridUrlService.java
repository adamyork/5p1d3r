package com.github.adamyork.fx5p1d3r.application.service.url;

import com.github.adamyork.fx5p1d3r.application.service.thread.ThreadService;
import com.github.adamyork.fx5p1d3r.common.model.ApplicationFormState;
import com.github.adamyork.fx5p1d3r.common.model.UrlValidationResult;
import com.github.adamyork.fx5p1d3r.common.service.AlertService;
import com.github.adamyork.fx5p1d3r.common.service.progress.ProgressService;
import com.github.adamyork.fx5p1d3r.common.service.progress.ProgressType;
import org.apache.commons.io.FileUtils;
import org.jooq.lambda.Unchecked;
import org.springframework.context.MessageSource;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Adam York on 2/23/2017.
 * Copyright 2017
 */
public class HybridUrlService implements UrlService {

    private final ApplicationFormState applicationFormState;
    private final ValidatorService urlValidatorService;
    private final ProgressService progressService;
    private final MessageSource messageSource;
    private final AlertService alertService;
    private final ThreadService singleThreadService;
    private final ThreadService multiThreadService;

    public HybridUrlService(final ApplicationFormState applicationFormState,
                            final ValidatorService urlValidatorService,
                            final ProgressService progressService,
                            final MessageSource messageSource,
                            final AlertService alertService,
                            final ThreadService singleThreadService,
                            final ThreadService multiThreadService) {
        this.applicationFormState = applicationFormState;
        this.urlValidatorService = urlValidatorService;
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
        progressService.updateSteps(urlStrings.size());
        progressService.updateProgress(ProgressType.START);
        validateUrlsAndExecute(urlStrings);
    }

    @Override
    public void executeList() {
        final File urlListFile = applicationFormState.getUrlListFile();
        final String header = messageSource.getMessage("error.no.url.list.header", null, Locale.getDefault());
        final String content = messageSource.getMessage("error.no.url.list.content", null, Locale.getDefault());
        if (urlListFile == null) {
            alertService.error(header, content);
        } else {
            final List<String> urlStrings = Unchecked.function(o -> FileUtils.readLines(urlListFile)).apply(null);
            progressService.updateSteps(urlStrings.size());
            progressService.updateProgress(ProgressType.START);
            validateUrlsAndExecute(urlStrings);
        }
    }

    private void validateUrlsAndExecute(final List<String> urlStrings) {
        final UrlValidationResult validationResult = urlValidatorService.validateUrls(urlStrings);
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
        }
    }

}
