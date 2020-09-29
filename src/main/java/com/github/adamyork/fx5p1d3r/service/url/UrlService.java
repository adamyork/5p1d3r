package com.github.adamyork.fx5p1d3r.service.url;

import com.github.adamyork.fx5p1d3r.service.url.data.UrlValidationResult;
import org.jsoup.select.Elements;

import java.net.URL;
import java.util.List;

/**
 * Created by Adam York on 8/28/2020.
 * Copyright 2020
 */
public interface UrlService {

    UrlValidationResult validateUrls(final List<String> urlStrings);

    List<URL> getUrlListFromElements(final Elements elements);

}
