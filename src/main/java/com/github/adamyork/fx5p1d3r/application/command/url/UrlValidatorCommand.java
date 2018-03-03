package com.github.adamyork.fx5p1d3r.application.command.url;

import com.github.adamyork.fx5p1d3r.common.Validator;
import com.github.adamyork.fx5p1d3r.common.command.ApplicationCommand;
import com.github.adamyork.fx5p1d3r.common.command.CommandMap;
import com.github.adamyork.fx5p1d3r.common.command.NoopCommand;
import com.github.adamyork.fx5p1d3r.common.command.ValidatorCommand;
import com.github.adamyork.fx5p1d3r.common.command.alert.AlertCommand;
import com.github.adamyork.fx5p1d3r.common.model.AllValidUrls;
import com.github.adamyork.fx5p1d3r.common.model.ApplicationFormState;
import com.github.adamyork.fx5p1d3r.common.service.progress.ProgressService;
import com.github.adamyork.fx5p1d3r.common.service.progress.ProgressType;
import javafx.scene.control.TextField;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Adam York on 3/23/2017.
 * Copyright 2017
 */
@Component
public class UrlValidatorCommand implements ValidatorCommand {

    private final Map<Boolean, CommandMap<Boolean, ApplicationCommand>> isValidMap;
    private final CommandMap<Boolean, AlertCommand> urlsValidCommandMap;
    private final Validator validator;
    private final ApplicationFormState applicationFormState;
    private final ProgressService progressService;
    private final MessageSource messageSource;

    @Inject
    public UrlValidatorCommand(@Qualifier("ThreadRequestsCommandMap") final CommandMap<Boolean, ApplicationCommand> threadRequestsCommandMap,
                               @Qualifier("UrlsValidCommandMap") final CommandMap<Boolean, AlertCommand> urlsValidCommandMap,
                               final Validator validator,
                               final ApplicationFormState applicationFormState,
                               final ProgressService progressService,
                               final MessageSource messageSource) {
        this.validator = validator;
        this.applicationFormState = applicationFormState;
        this.progressService = progressService;
        this.messageSource = messageSource;
        this.urlsValidCommandMap = urlsValidCommandMap;

        final CommandMap<Boolean, ApplicationCommand> noopCommandMap = new CommandMap<>();
        noopCommandMap.add(true, new NoopCommand());
        noopCommandMap.add(false, new NoopCommand());

        isValidMap = new HashMap<>();
        isValidMap.put(true, threadRequestsCommandMap);
        isValidMap.put(false, noopCommandMap);
    }

    @Override
    public void execute(final List<String> urlStrings) {
        final AllValidUrls allUrlsValid = validateUrls(urlStrings);
        isValidMap.get(allUrlsValid.isValidity()).getCommand(applicationFormState.multithreading()).execute(allUrlsValid.getUrls());
    }

    @Override
    public void execute(final File file, final TextField textField) {
        //no-op
    }

    @Override
    public String execute(final Element element, final String href) {
        return null;
    }

    private AllValidUrls validateUrls(final List<String> urlStrings) {
        progressService.updateProgress(ProgressType.VALIDATE);
        final List<Map<String, Boolean>> validityMap = urlStrings.stream().map(urlString -> {
            final Map<String, Boolean> map = new HashMap<>();
            final Boolean valid = validator.validateUrlString(urlString);
            map.put(urlString, valid);
            return map;
        }).collect(Collectors.toList());
        final boolean isValid = validityMap.stream()
                .allMatch(stringBooleanMap -> stringBooleanMap.values()
                        .stream()
                        .allMatch(Boolean::booleanValue));
        return (AllValidUrls) urlsValidCommandMap.getCommand(isValid)
                .execute(messageSource.getMessage("error.url.invalid.header", null, Locale.getDefault()),
                        messageSource.getMessage("error.url.invalid.content", null, Locale.getDefault()), urlStrings);
    }

}
