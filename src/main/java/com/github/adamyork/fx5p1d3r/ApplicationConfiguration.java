package com.github.adamyork.fx5p1d3r;

import com.github.adamyork.fx5p1d3r.service.parse.DocumentParserService;
import com.github.adamyork.fx5p1d3r.service.parse.JSoupDocumentParser;
import com.github.adamyork.fx5p1d3r.service.progress.AlertService;
import com.github.adamyork.fx5p1d3r.service.progress.ApplicationProgressService;
import com.github.adamyork.fx5p1d3r.service.progress.ProgressService;
import com.github.adamyork.fx5p1d3r.service.url.*;
import com.github.adamyork.fx5p1d3r.view.Closeable;
import com.github.adamyork.fx5p1d3r.view.ControlController;
import com.github.adamyork.fx5p1d3r.view.TransformController;
import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;

import java.util.ArrayList;
import java.util.List;

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
    public UrlService urlService(final UrlValidator urlValidator,
                                 final ProgressService progressService) {
        return new DefaultUrlService(urlValidator, progressService);
    }

    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasenames("messages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

    @Bean
    public ProgressService progressService(final MessageSource messageSource) {
        final ApplicationProgressService applicationProgressService = new ApplicationProgressService(messageSource);
        applicationProgressService.initialize();
        return applicationProgressService;
    }

    @Bean
    public UrlServiceFactory urlServiceFactory(final ApplicationFormState applicationFormState,
                                               final ProgressService progressService) {
        return new UrlServiceFactory(applicationFormState, progressService);
    }

    @Bean
    public LinksFollower recursiveLinksFollower(final ApplicationFormState applicationFormState,
                                                final UrlServiceFactory urlServiceFactory,
                                                final UrlService urlService,
                                                final ProgressService progressService,
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
                                                final ProgressService progressService,
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
                                               final ProgressService progressService,
                                               final LinksFollower recursiveLinksFollower,
                                               final DocumentParserService parserService) {
        return new MultiThreadedCrawler(urlServiceFactory,
                applicationFormState, urlService, messageSource, alertService, progressService,
                recursiveLinksFollower, parserService);
    }

    @Bean
    public SpiderService singleUrlSpiderService(final ApplicationFormState applicationFormState,
                                                final UrlService urlService,
                                                final ProgressService progressService,
                                                final MessageSource messageSource,
                                                final AlertService alertService,
                                                final CrawlerService singleThreadedCrawler,
                                                final CrawlerService multiThreadedCrawler) {
        return new SingleUrlSpiderService(applicationFormState, urlService,
                progressService, messageSource, alertService, singleThreadedCrawler, multiThreadedCrawler);
    }

    @Bean
    public SpiderService urlListSpiderService(final ApplicationFormState applicationFormState,
                                              final UrlService urlService,
                                              final ProgressService progressService,
                                              final MessageSource messageSource,
                                              final AlertService alertService,
                                              final CrawlerService singleThreadedCrawler,
                                              final CrawlerService multiThreadedCrawler) {
        return new UrlListSpiderService(applicationFormState, urlService,
                progressService, messageSource, alertService, singleThreadedCrawler, multiThreadedCrawler);
    }

    @Bean
    public DocumentParserService jsoupDocumentParser(final ProgressService progressService,
                                                     final MessageSource messageSource,
                                                     final AlertService alertService) {
        return new JSoupDocumentParser(progressService, messageSource, alertService);
    }

    @Bean
    public List<Closeable> closeableList(final ControlController controlController,
                                         final TransformController transformController){
        final List<Closeable> list = new ArrayList<>();
        list.add(controlController);
        list.add(transformController);
        return list;
    }

}
