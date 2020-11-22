package com.github.adamyork.fx5p1d3r;

import com.github.adamyork.fx5p1d3r.service.parse.DocumentParserService;
import com.github.adamyork.fx5p1d3r.service.parse.JSoupDocumentParser;
import com.github.adamyork.fx5p1d3r.service.progress.AlertService;
import com.github.adamyork.fx5p1d3r.service.progress.ApplicationProgressService;
import com.github.adamyork.fx5p1d3r.service.url.*;
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
    public DefaultUrlService urlValidatorService(final UrlValidator urlValidator,
                                                 final ApplicationProgressService progressService) {
        return new DefaultUrlService(urlValidator, progressService);
    }


    @Bean
    public LinksFollower recursiveLinksFollower(final ApplicationFormState applicationFormState,
                                                final UrlServiceFactory urlServiceFactory,
                                                final UrlService urlService,
                                                final ApplicationProgressService progressService,
                                                final AlertService alertService,
                                                final MessageSource messageSource,
                                                final DocumentParserService parserService) {
        return new DefaultLinksFollower(urlServiceFactory,
                applicationFormState, urlService, messageSource, alertService, progressService,
                null, parserService);
    }

    @Bean
    public CrawlerService singleThreadedCrawler(final UrlServiceFactory urlServiceFactory,
                                                final ApplicationFormState applicationFormState,
                                                final UrlService urlService,
                                                final MessageSource messageSource,
                                                final AlertService alertService,
                                                final ApplicationProgressService progressService,
                                                final LinksFollower recursiveLinksFollower,
                                                final DocumentParserService parserService) {
        return new SingleThreadedCrawler(urlServiceFactory,
                applicationFormState, urlService, messageSource, alertService, progressService,
                recursiveLinksFollower, parserService);
    }

    @Bean
    public CrawlerService multiThreadedCrawler(final UrlServiceFactory urlServiceFactory,
                                               final ApplicationFormState applicationFormState,
                                               final UrlService urlService,
                                               final MessageSource messageSource,
                                               final AlertService alertService,
                                               final ApplicationProgressService progressService,
                                               final LinksFollower recursiveLinksFollower,
                                               final DocumentParserService parserService) {
        return new MultiThreadedCrawler(urlServiceFactory,
                applicationFormState, urlService, messageSource, alertService, progressService,
                recursiveLinksFollower, parserService);
    }

    @Bean
    public SpiderService singleUrlSpiderService(final ApplicationFormState applicationFormState,
                                                final DefaultUrlService urlValidatorService,
                                                final ApplicationProgressService progressService,
                                                final MessageSource messageSource,
                                                final AlertService alertService,
                                                final CrawlerService singleThreadedCrawler,
                                                final CrawlerService multiThreadedCrawler) {
        return new SingleUrlSpiderService(applicationFormState, urlValidatorService,
                progressService, messageSource, alertService, singleThreadedCrawler, multiThreadedCrawler);
    }

    @Bean
    public SpiderService urlListSpiderService(final ApplicationFormState applicationFormState,
                                              final DefaultUrlService urlValidatorService,
                                              final ApplicationProgressService progressService,
                                              final MessageSource messageSource,
                                              final AlertService alertService,
                                              final CrawlerService singleThreadedCrawler,
                                              final CrawlerService multiThreadedCrawler) {
        return new UrlListSpiderService(applicationFormState, urlValidatorService,
                progressService, messageSource, alertService, singleThreadedCrawler, multiThreadedCrawler);
    }

    @Bean
    public DocumentParserService jsoupDocumentParser(final ApplicationProgressService progressService,
                                                     final MessageSource messageSource,
                                                     final AlertService alertService) {
        return new JSoupDocumentParser(progressService, messageSource, alertService);
    }

}
