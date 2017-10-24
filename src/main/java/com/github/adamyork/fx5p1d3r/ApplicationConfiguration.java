package com.github.adamyork.fx5p1d3r;

import com.github.adamyork.fx5p1d3r.application.command.*;
import com.github.adamyork.fx5p1d3r.common.command.*;
import com.github.adamyork.fx5p1d3r.common.model.OutputFileType;
import com.github.adamyork.fx5p1d3r.common.model.UrlMethod;
import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;

import java.io.File;

/**
 * Created by Adam York on 2/26/2017.
 * Copyright 2017
 */
@Configuration
public class ApplicationConfiguration {

    @Bean
    public CommandMap<UrlMethod, ApplicationCommand> urlMethodCommandMap(final SingleUrlCommand singleUrlCommand,
                                                                         @Qualifier("UrlListCommand") final UrlListCommand urlListCommand) {
        final CommandMap<UrlMethod, ApplicationCommand> map = new CommandMap<>();
        map.add(UrlMethod.URL, singleUrlCommand);
        map.add(UrlMethod.URL_LIST, urlListCommand);
        return map;
    }

    @Bean
    @Qualifier("ThreadRequestsCommandMap")
    public CommandMap<Boolean, ApplicationCommand> threadRequestsCommandMap(final MultiThreadCommand threadRequestCommand,
                                                                            final SingleThreadCommand singleThreadCommand) {
        final CommandMap<Boolean, ApplicationCommand> map = new CommandMap<>();
        map.add(true, threadRequestCommand);
        map.add(false, singleThreadCommand);
        return map;
    }

    @Bean
    @Qualifier("FollowLinksCommandMap")
    public CommandMap<Boolean, ApplicationCommand> followLinksCommandMap(final LinksFollowCommand followLinksCommand,
                                                                         final LinksNoFollowCommand ignoreLinksCommand) {
        final CommandMap<Boolean, ApplicationCommand> map = new CommandMap<>();
        map.add(true, followLinksCommand);
        map.add(false, ignoreLinksCommand);
        return map;
    }

    @Bean
    @Qualifier("ParserCommandMap")
    public CommandMap<OutputFileType, ParserCommand> parserCommandMap(final DocumentParserJsonCommand documentParserJsonCommand,
                                                                      final DocumentParserCsvCommand documentParserCsvCommand) {
        final CommandMap<OutputFileType, ParserCommand> map = new CommandMap<>();
        map.add(OutputFileType.JSON, documentParserJsonCommand);
        map.add(OutputFileType.CSV, documentParserCsvCommand);
        return map;
    }

    @Bean
    @Qualifier("OutputCommandMap")
    public CommandMap<OutputFileType, OutputCommand> outputCommandMap(final OutputFileTypeJsonCommand outputFileTypeJsonCommand,
                                                                      final OutputFileTypeCsvCommand outputFileTypeCsvCommand) {
        final CommandMap<OutputFileType, OutputCommand> map = new CommandMap<>();
        map.add(OutputFileType.JSON, outputFileTypeJsonCommand);
        map.add(OutputFileType.CSV, outputFileTypeCsvCommand);
        return map;
    }

    @Bean
    @Qualifier("DomQueryListViewCommandMap")
    public CommandMap<Boolean, NullSafeCommand> domQueryListViewCommandMap(final DomQueryListViewCommand domQueryListViewCommand,
                                                                           final NoopCommand noopCommand) {
        final CommandMap<Boolean, NullSafeCommand> map = new CommandMap<>();
        map.add(true, domQueryListViewCommand);
        map.add(false, noopCommand);
        return map;
    }

    @Bean
    @Qualifier("ResultTransformListViewCommandMap")
    public CommandMap<Boolean, NullSafeCommand> resultTransformListViewCommandMap(final ResultTransformListViewCommand resultTransformListViewCommand,
                                                                                  final NoopCommand noopCommand) {
        final CommandMap<Boolean, NullSafeCommand> map = new CommandMap<>();
        map.add(true, resultTransformListViewCommand);
        map.add(false, noopCommand);
        return map;
    }

    @Bean
    @Qualifier("LoadUrlListCommandMap")
    public CommandMap<Boolean, ValidatorCommand> loadUrlListCommandMap(final LoadUrlListCommand loadUrlListCommand,
                                                                       final NoopValidatorCommand noopValidatorCommand) {
        final CommandMap<Boolean, ValidatorCommand> map = new CommandMap<>();
        map.add(true, loadUrlListCommand);
        map.add(false, noopValidatorCommand);
        return map;
    }

    @Bean
    @Qualifier("WarnCommandMap")
    public CommandMap<Boolean, AlertCommand> warnCommandMap(final WarnCommand warnCommand,
                                                            final NoopCommand noopCommand) {
        final CommandMap<Boolean, AlertCommand> map = new CommandMap<>();
        map.add(true, warnCommand);
        map.add(false, noopCommand);
        return map;
    }

    @Bean
    @Qualifier("UrlListSelectedCommandMap")
    public CommandMap<Boolean, AlertCommand> urlListSelectedCommandMap(final ErrorCommand errorCommand,
                                                                       final UrlListSelectedCommand urlListSelectedCommand) {
        final CommandMap<Boolean, AlertCommand> map = new CommandMap<>();
        map.add(true, errorCommand);
        map.add(false, urlListSelectedCommand);
        return map;
    }

    @Bean
    @Qualifier("UrlsValidCommandMap")
    public CommandMap<Boolean, AlertCommand> urlsValidCommandMap(final AllUrlsValidCommand allUrlsValidCommand,
                                                                 final UrlsInvalidCommand urlsInvalidCommand) {
        final CommandMap<Boolean, AlertCommand> map = new CommandMap<>();
        map.add(true, allUrlsValidCommand);
        map.add(false, urlsInvalidCommand);
        return map;
    }

    @Bean
    @Qualifier("DefaultJsonTransformCommandMap")
    public CommandMap<Boolean, NullSafeCommand<File>> defaultJsonTransformCommandMap(final DefaultJsonTransformCommand jsonTransformCommand,
                                                                                     final NoopCommand<File> noopCommand) {
        final CommandMap<Boolean, NullSafeCommand<File>> map = new CommandMap<>();
        map.add(true, jsonTransformCommand);
        map.add(false, noopCommand);
        return map;
    }

    @Bean
    @Qualifier("DefaultCsvTransformCommandMap")
    public CommandMap<Boolean, NullSafeCommand<File>> defaultCsvTransformCommandMap(final DefaultCsvTransformCommand csvTransformCommand,
                                                                                    final NoopCommand<File> noopCommand) {
        final CommandMap<Boolean, NullSafeCommand<File>> map = new CommandMap<>();
        map.add(true, csvTransformCommand);
        map.add(false, noopCommand);
        return map;
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

}
