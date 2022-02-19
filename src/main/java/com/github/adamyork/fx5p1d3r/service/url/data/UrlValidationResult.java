package com.github.adamyork.fx5p1d3r.service.url.data;

import java.net.URL;
import java.util.List;

/**
 * Created by Adam York on 10/24/2017.
 * Copyright 2017
 */
public record UrlValidationResult(boolean validity, List<URL> urls) {

    public boolean isValid() {
        return validity;
    }

    public List<URL> getUrls() {
        return urls;
    }

}
