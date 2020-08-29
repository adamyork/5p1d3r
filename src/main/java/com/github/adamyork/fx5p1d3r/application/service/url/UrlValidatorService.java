package com.github.adamyork.fx5p1d3r.application.service.url;

import com.github.adamyork.fx5p1d3r.common.Validator;
import com.github.adamyork.fx5p1d3r.common.model.UrlValidationResult;
import com.github.adamyork.fx5p1d3r.common.service.progress.ProgressService;
import com.github.adamyork.fx5p1d3r.common.service.progress.ProgressType;
import org.jooq.lambda.Unchecked;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Adam York on 3/23/2017.
 * Copyright 2017
 */
public class UrlValidatorService implements ValidatorService {

    private final Validator validator;
    private final ProgressService progressService;

    public UrlValidatorService(final Validator validator,
                               final ProgressService progressService) {
        this.validator = validator;
        this.progressService = progressService;
    }

    @Override
    public UrlValidationResult validateUrls(final List<String> urlStrings) {
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
        if (isValid) {
            final List<URL> urls = urlStrings.stream().map(Unchecked.function(URL::new)).collect(Collectors.toList());
            return new UrlValidationResult(true, urls);
        }
        return new UrlValidationResult(false, new ArrayList<>());
    }

}
