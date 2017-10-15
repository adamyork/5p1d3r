package com.github.adamyork.fx5p1d3r.common;

import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by Adam York on 2/23/2017.
 * Copyright 2017
 */
@Component
public class Validator {

    private UrlValidator urlValidator;

    @Autowired
    public Validator(final UrlValidator urlValidator) {
        this.urlValidator = urlValidator;
    }

    public boolean validateURLString(final String urlString) {
        if (urlValidator == null) {
            final String[] supportedURLSchemes = {"http", "https"};
            urlValidator = new UrlValidator(supportedURLSchemes, UrlValidator.ALLOW_LOCAL_URLS);
        }
        return urlValidator.isValid(urlString);
    }
}
