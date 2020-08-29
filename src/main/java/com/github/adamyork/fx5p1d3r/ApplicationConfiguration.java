package com.github.adamyork.fx5p1d3r;

import com.github.adamyork.fx5p1d3r.application.service.io.CsvDocumentParser;
import com.github.adamyork.fx5p1d3r.application.service.io.DocumentParserService;
import com.github.adamyork.fx5p1d3r.application.service.io.JsonDocumentParser;
import com.github.adamyork.fx5p1d3r.application.service.thread.*;
import com.github.adamyork.fx5p1d3r.application.service.url.HybridUrlService;
import com.github.adamyork.fx5p1d3r.application.service.url.UrlService;
import com.github.adamyork.fx5p1d3r.application.service.url.UrlValidatorService;
import com.github.adamyork.fx5p1d3r.common.Validator;
import com.github.adamyork.fx5p1d3r.common.model.ApplicationFormState;
import com.github.adamyork.fx5p1d3r.common.service.*;
import com.github.adamyork.fx5p1d3r.common.service.progress.ProgressService;
import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;

/**
 * Created by Adam York on 2/26/2017.
 * Copyright 2017
 */
@SuppressWarnings("UnusedReturnValue")
@Configuration
public class ApplicationConfiguration {

    @Bean
    public ApplicationFormState applicationFormState() {
        return new ApplicationFormState();
    }

    @Bean
    public UrlValidator urlValidator() {
        final String[] supportedUrlSchemes = {"http", "https"};
        return new UrlValidator(supportedUrlSchemes, UrlValidator.ALLOW_LOCAL_URLS);
    }

    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasenames("messages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

    @Bean
    public UrlValidatorService urlValidatorService(final Validator validator,
                                                   final ProgressService progressService) {
        return new UrlValidatorService(validator, progressService);
    }

    @Bean
    public OutputService jsonOutputService(final ApplicationFormState applicationFormState,
                                           final Validator validator,
                                           final ProgressService progressService) {
        return new JsonOutputService(applicationFormState,
                validator, progressService);
    }

    @Bean
    public OutputService csvOutputService(final ApplicationFormState applicationFormState,
                                          final Validator validator,
                                          final ProgressService progressService) {
        return new CsvOutputService(applicationFormState,
                validator, progressService);
    }

    @Bean
    public DocumentParserService jsonDocumentParser(final ApplicationFormState applicationFormState,
                                                    final ProgressService progressService,
                                                    final MessageSource messageSource,
                                                    final AlertService alertService,
                                                    final OutputService jsonOutputService) {
        return new JsonDocumentParser(applicationFormState, progressService,
                messageSource, alertService, jsonOutputService);
    }

    @Bean
    public DocumentParserService csvDocumentParser(final ApplicationFormState applicationFormState,
                                                   final ProgressService progressService,
                                                   final MessageSource messageSource,
                                                   final AlertService alertService,
                                                   final OutputService csvOutputService) {
        return new CsvDocumentParser(applicationFormState, progressService,
                messageSource, alertService, csvOutputService);
    }

    @Bean
    public LinksFollower recursiveLinksFollower(final ApplicationFormState applicationFormState,
                                                final UrlServiceFactory urlServiceFactory,
                                                final OutputService jsonOutputService,
                                                final ProgressService progressService,
                                                final AlertService alertService,
                                                final MessageSource messageSource,
                                                final DocumentParserService jsonDocumentParser,
                                                final DocumentParserService csvDocumentParser) {
        return new RecursiveLinksFollower(applicationFormState, urlServiceFactory,
                jsonOutputService, progressService, alertService, messageSource, jsonDocumentParser, csvDocumentParser);
    }

    @Bean
    public ThreadService singleThreadService(final UrlServiceFactory urlServiceFactory,
                                             final ApplicationFormState applicationFormState,
                                             final OutputService jsonOutputService,
                                             final AbortService abortService,
                                             final MessageSource messageSource,
                                             final AlertService alertService,
                                             final DocumentParserService jsonDocumentParser,
                                             final DocumentParserService csvDocumentParser,
                                             final ProgressService progressService,
                                             final LinksFollower recursiveLinksFollower) {
        return new SingleThreadService(urlServiceFactory,
                applicationFormState, jsonOutputService, abortService, messageSource, alertService,
                jsonDocumentParser, csvDocumentParser, progressService, recursiveLinksFollower);
    }

    @Bean
    public ThreadService multiThreadService(final UrlServiceFactory urlServiceFactory,
                                            final ApplicationFormState applicationFormState,
                                            final OutputService jsonOutputService,
                                            final AbortService abortService,
                                            final MessageSource messageSource,
                                            final AlertService alertService,
                                            final DocumentParserService jsonDocumentParser,
                                            final DocumentParserService csvDocumentParser,
                                            final ProgressService progressService,
                                            final LinksFollower recursiveLinksFollower) {
        return new MultiThreadService(urlServiceFactory,
                applicationFormState, jsonOutputService, abortService, messageSource, alertService,
                jsonDocumentParser, csvDocumentParser, progressService, recursiveLinksFollower);
    }

    @Bean
    public UrlService urlService(final ApplicationFormState applicationFormState,
                                 final UrlValidatorService urlValidatorService,
                                 final ProgressService progressService,
                                 final MessageSource messageSource,
                                 final AlertService alertService,
                                 final ThreadService singleThreadService,
                                 final ThreadService multiThreadService) {
        return new HybridUrlService(applicationFormState, urlValidatorService,
                progressService, messageSource, alertService, singleThreadService, multiThreadService);
    }

}
