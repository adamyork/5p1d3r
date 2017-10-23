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

    public boolean validateUrlString(final String urlString) {
        //TODO COMMAND
        if (urlValidator == null) {
            final String[] supportedUrlSchemes = {"http", "https"};
            urlValidator = new UrlValidator(supportedUrlSchemes, UrlValidator.ALLOW_LOCAL_URLS);
        }
        return urlValidator.isValid(urlString);
    }
}
