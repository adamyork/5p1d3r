package com.github.adamyork.fx5p1d3r.application.service.url;

import com.github.adamyork.fx5p1d3r.common.model.UrlValidationResult;

import java.util.List;

/**
 * Created by Adam York on 8/28/2020.
 * Copyright 2020
 */
public interface ValidatorService {

    UrlValidationResult validateUrls(final List<String> urlStrings);

}
