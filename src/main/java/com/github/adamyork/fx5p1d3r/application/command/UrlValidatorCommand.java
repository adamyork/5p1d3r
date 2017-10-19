package com.github.adamyork.fx5p1d3r.application.command;

import com.github.adamyork.fx5p1d3r.common.Validator;
import com.github.adamyork.fx5p1d3r.common.command.ApplicationCommand;
import com.github.adamyork.fx5p1d3r.common.command.CommandMap;
import com.github.adamyork.fx5p1d3r.common.command.ValidatorCommand;
import com.github.adamyork.fx5p1d3r.common.model.ApplicationFormState;
import com.github.adamyork.fx5p1d3r.common.service.AlertService;
import com.github.adamyork.fx5p1d3r.common.service.progress.ProgressService;
import com.github.adamyork.fx5p1d3r.common.service.progress.ProgressType;
import javafx.scene.control.TextField;
import org.jooq.lambda.Unchecked;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Adam York on 3/23/2017.
 * Copyright 2017
 */
@Component
public class UrlValidatorCommand implements ValidatorCommand {

    private final Validator validator;
    private final ApplicationFormState applicationFormState;
    private final ProgressService progressService;
    private final AlertService alertService;
    private final Map<Boolean, CommandMap<Boolean, ApplicationCommand>> isValidMap = new HashMap<>();

    @Autowired
    public UrlValidatorCommand(@Qualifier("ThreadRequestsCommandMap") final CommandMap<Boolean, ApplicationCommand> threadRequestsCommandMap,
                               final Validator validator,
                               final ApplicationFormState applicationFormState,
                               final ProgressService progressService,
                               final AlertService alertService) {
        this.validator = validator;
        this.applicationFormState = applicationFormState;
        this.progressService = progressService;
        this.alertService = alertService;

        final CommandMap<Boolean, ApplicationCommand> noopCommandMap = new CommandMap<>();
        noopCommandMap.add(true, new NoopCommand());
        noopCommandMap.add(false, new NoopCommand());

        isValidMap.put(true, threadRequestsCommandMap);
        isValidMap.put(false, noopCommandMap);
    }

    @Override
    public void execute(final List<String> urlStrings) {
        final AllValidURLS allURLSValid = validateURLS(urlStrings);
        isValidMap.get(allURLSValid.isValidity()).getCommand(applicationFormState.multithreading()).execute(allURLSValid.getUrls());
    }

    @Override
    public void execute(final File file, final TextField textField) {
        //no-op
    }

    private AllValidURLS validateURLS(final List<String> urlStrings) {
        progressService.updateProgress(ProgressType.VALIDATE);
        final List<URL> urls = urlStrings.stream().map(Unchecked.function(URL::new)).collect(Collectors.toList());
        final List<Map<String, Boolean>> validityMap = urlStrings.stream().map(urlString -> {
            final Map<String, Boolean> map = new HashMap<>();
            final Boolean valid = validator.validateURLString(urlString);
            map.put(urlString, valid);
            return map;
        }).collect(Collectors.toList());
        final boolean isValid = validityMap.stream().allMatch(stringBooleanMap -> stringBooleanMap.values()
                .stream()
                .allMatch(Boolean::booleanValue));
        //TODO COMMAND
        if (!isValid) {
            alertService.error("Invalid URL", "One or more URL's is invalid.");
            return new AllValidURLS(false, urls);
        }
        return new AllValidURLS(true, urls);

    }

    private class AllValidURLS {

        private boolean validity;
        private List<URL> urls;

        private AllValidURLS(final boolean validity,
                             final List<URL> urls) {
            this.validity = validity;
            this.urls = urls;
        }

        boolean isValidity() {
            return validity;
        }

        public List<URL> getUrls() {
            return urls;
        }

    }

}
