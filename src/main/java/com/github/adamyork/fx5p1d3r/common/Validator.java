package com.github.adamyork.fx5p1d3r.common;

import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

/**
 * Created by Adam York on 2/23/2017.
 * Copyright 2017
 */
@Component
public class Validator {

    private final UrlValidator urlValidator;

    @Inject
    public Validator(final UrlValidator urlValidator) {
        this.urlValidator = urlValidator;
    }

    public boolean validateUrlString(final String urlString) {
        return urlValidator.isValid(urlString);
    }
}
