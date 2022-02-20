package com.github.adamyork.fx5p1d3r;

import com.github.adamyork.fx5p1d3r.service.progress.AlertService;
import com.github.adamyork.fx5p1d3r.service.progress.BatchDownloadProgressService;
import com.github.adamyork.fx5p1d3r.service.progress.ProgressService;
import com.github.adamyork.fx5p1d3r.service.url.*;
import com.github.adamyork.fx5p1d3r.service.url.download.DownloadListSpiderService;
import com.github.adamyork.fx5p1d3r.service.url.download.MultiThreadedDownloadCrawler;
import com.github.adamyork.fx5p1d3r.service.url.download.SingleThreadedDownloadCrawler;
import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by Adam York on 2/19/2022.
 * Copyright 2022
 */
@Configuration
public class DownloadConfiguration {

    @Bean("downloadProgressService")
    public ProgressService downloadProgressService(final MessageSource messageSource) {
        final BatchDownloadProgressService batchDownloadProgressService = new BatchDownloadProgressService(messageSource);
        batchDownloadProgressService.initialize();
        return batchDownloadProgressService;
    }

    @Bean("downloadUrlService")
    public UrlService downloadUrlValidatorService(final UrlValidator urlValidator,
                                                  final ProgressService downloadProgressService) {
        return new DefaultUrlService(urlValidator, downloadProgressService);
    }

    @Bean("downloadUrlServiceFactory")
    public UrlServiceFactory downloadUrlServiceFactory(final ApplicationFormState applicationFormState,
                                                       final ProgressService downloadProgressService) {
        return new UrlServiceFactory(applicationFormState, downloadProgressService);
    }

    @Bean("singleThreadedDownloader")
    public CrawlerService singleThreadedDownloader(final UrlServiceFactory downloadUrlServiceFactory,
                                                   final ProgressService downloadProgressService) {
        return new SingleThreadedDownloadCrawler(downloadUrlServiceFactory, downloadProgressService);
    }

    @Bean("multiThreadedDownloader")
    public CrawlerService multiThreadedDownloader(final UrlServiceFactory downloadUrlServiceFactory,
                                                  final ApplicationFormState applicationFormState,
                                                  final ProgressService downloadProgressService) {
        return new MultiThreadedDownloadCrawler(downloadUrlServiceFactory, downloadProgressService, applicationFormState);
    }

    @Bean("urlListDownloadSpiderService")
    public SpiderService urlListDownloadSpiderService(final ApplicationFormState applicationFormState,
                                                      final UrlService downloadUrlService,
                                                      final MessageSource messageSource,
                                                      final AlertService alertService,
                                                      final CrawlerService singleThreadedDownloader,
                                                      final CrawlerService multiThreadedDownloader,
                                                      final ProgressService downloadProgressService) {
        return new DownloadListSpiderService(applicationFormState, downloadUrlService,
                downloadProgressService, messageSource, alertService,
                singleThreadedDownloader, multiThreadedDownloader);
    }
}
