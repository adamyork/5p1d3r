package com.github.adamyork.fx5p1d3r;

import com.github.adamyork.fx5p1d3r.application.command.*;
import com.github.adamyork.fx5p1d3r.common.command.*;
import com.github.adamyork.fx5p1d3r.common.model.OutputFileType;
import com.github.adamyork.fx5p1d3r.common.model.URLMethod;
import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;

/**
 * Created by Adam York on 2/26/2017.
 * Copyright 2017
 */
@Configuration
public class ApplicationConfiguration {

    @Bean
    public CommandMap<URLMethod, ApplicationCommand> providesURLMethodCommandMap(final SingleURLCommand singleUrlCommand,
                                                                                 @Qualifier("UrlListCommand") final UrlListCommand urlListCommand) {
        final CommandMap<URLMethod, ApplicationCommand> urlMethodCommandMap = new CommandMap<>();
        urlMethodCommandMap.add(URLMethod.URL, singleUrlCommand);
        urlMethodCommandMap.add(URLMethod.URL_LIST, urlListCommand);
        return urlMethodCommandMap;
    }

    @Bean
    @Qualifier("ThreadRequestsCommandMap")
    public CommandMap<Boolean, ApplicationCommand> providesThreadRequestsCommandMap(final MultiThreadCommand threadRequestCommand,
                                                                                    final SingleThreadCommand singleThreadCommand) {
        final CommandMap<Boolean, ApplicationCommand> threadRequestsCommandMap = new CommandMap<>();
        threadRequestsCommandMap.add(true, threadRequestCommand);
        threadRequestsCommandMap.add(false, singleThreadCommand);
        return threadRequestsCommandMap;
    }

    @Bean
    @Qualifier("FollowLinksCommandMap")
    public CommandMap<Boolean, ApplicationCommand> providesFollowLinksCommandMap(final LinksFollowCommand followLinksCommand,
                                                                                 final LinksNoFollowCommand ignoreLinksCommand) {
        final CommandMap<Boolean, ApplicationCommand> followLinksCommandMap = new CommandMap<>();
        followLinksCommandMap.add(true, followLinksCommand);
        followLinksCommandMap.add(false, ignoreLinksCommand);
        return followLinksCommandMap;
    }

    @Bean
    @Qualifier("ParserCommandMap")
    public CommandMap<OutputFileType, ParserCommand> providesParserCommandMap(final DocumentParserJsonCommand documentParserJsonCommand,
                                                                              final DocumentParserCsvCommand documentParserCsvCommand) {
        final CommandMap<OutputFileType, ParserCommand> parserCommandMap = new CommandMap<>();
        parserCommandMap.add(OutputFileType.JSON, documentParserJsonCommand);
        parserCommandMap.add(OutputFileType.CSV, documentParserCsvCommand);
        return parserCommandMap;
    }

    @Bean
    @Qualifier("OutputCommandMap")
    public CommandMap<OutputFileType, OutputCommand> providesOutputCommandMap(final OutputFileTypeJsonCommand outputFileTypeJsonCommand,
                                                                              final OutputFileTypeCsvCommand outputFileTypeCsvCommand) {
        final CommandMap<OutputFileType, OutputCommand> outputCommandMap = new CommandMap<>();
        outputCommandMap.add(OutputFileType.JSON, outputFileTypeJsonCommand);
        outputCommandMap.add(OutputFileType.CSV, outputFileTypeCsvCommand);
        return outputCommandMap;
    }

    @Bean
    @Qualifier("DomQueryListViewCommandMap")
    public CommandMap<Boolean, NullSafeCommand> providesDomQueryListViewCommandMap(final DomQueryListViewCommand domQueryListViewCommand,
                                                                                   final NoopCommand noopCommand) {
        final CommandMap<Boolean, NullSafeCommand> domQueryListViewCommandMap = new CommandMap<>();
        domQueryListViewCommandMap.add(true, domQueryListViewCommand);
        domQueryListViewCommandMap.add(false, noopCommand);
        return domQueryListViewCommandMap;
    }

    @Bean
    @Qualifier("ResultTransformListViewCommandMap")
    public CommandMap<Boolean, NullSafeCommand> providesResultTransformListViewCommandMap(final ResultTransformListViewCommand resultTransformListViewCommand,
                                                                                          final NoopCommand noopCommand) {
        final CommandMap<Boolean, NullSafeCommand> resultTransformListViewCommandMap = new CommandMap<>();
        resultTransformListViewCommandMap.add(true, resultTransformListViewCommand);
        resultTransformListViewCommandMap.add(false, noopCommand);
        return resultTransformListViewCommandMap;
    }

    @Bean
    @Qualifier("LoadUrlListCommandMap")
    public CommandMap<Boolean, ValidatorCommand> providesLoadUrlListCommand(final LoadUrlListCommand loadUrlListCommand,
                                                                            final NoopValidatorCommand noopValidatorCommand) {
        final CommandMap<Boolean, ValidatorCommand> loadUrlListCommandMap = new CommandMap<>();
        loadUrlListCommandMap.add(true, loadUrlListCommand);
        loadUrlListCommandMap.add(false, noopValidatorCommand);
        return loadUrlListCommandMap;
    }

    @Bean
    public UrlValidator providesURLValidator() {
        final String[] supportedURLSchemes = {"http", "https"};
        return new UrlValidator(supportedURLSchemes, UrlValidator.ALLOW_LOCAL_URLS);
    }

    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasenames("messages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

}
