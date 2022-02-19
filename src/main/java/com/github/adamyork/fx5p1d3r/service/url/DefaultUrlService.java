package com.github.adamyork.fx5p1d3r.service.url;

import com.github.adamyork.fx5p1d3r.service.progress.ProgressService;
import com.github.adamyork.fx5p1d3r.service.progress.ProgressType;
import com.github.adamyork.fx5p1d3r.service.url.data.UrlValidationResult;
import org.apache.commons.validator.routines.UrlValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jooq.lambda.Unchecked;
import org.jsoup.select.Elements;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Adam York on 3/23/2017.
 * Copyright 2017
 */
public class DefaultUrlService implements UrlService {

    private static final Logger logger = LogManager.getLogger(DefaultUrlService.class);

    private final UrlValidator urlValidator;
    private final ProgressService progressService;

    public DefaultUrlService(final UrlValidator urlValidator,
                             final ProgressService progressService) {
        this.urlValidator = urlValidator;
        this.progressService = progressService;
    }

    @Override
    public UrlValidationResult validateUrls(final List<String> urlStrings) {
        progressService.updateProgress(ProgressType.VALIDATE);
        final List<Map<String, Boolean>> validityMap = urlStrings.stream()
                .map(urlString -> {
                    final Map<String, Boolean> map = new HashMap<>();
                    final Boolean valid = validateUrlString(urlString);
                    map.put(urlString, valid);
                    return map;
                }).toList();
        final boolean isValid = validityMap.stream()
                .allMatch(stringBooleanMap -> stringBooleanMap.values()
                        .stream()
                        .allMatch(Boolean::booleanValue));
        if (isValid) {
            final List<URL> urls = urlStrings.stream().map(Unchecked.function(URL::new))
                    .collect(Collectors.toList());
            return new UrlValidationResult(true, urls);
        }
        return new UrlValidationResult(false, new ArrayList<>());
    }

    @Override
    public List<URL> getUrlListFromElements(final Elements elements) {
        return elements.stream()
                .map(element -> {
                    final String href = element.attr("href");
                    try {
                        final URL baseUrl = new URI(element.baseUri()).toURL();
                        if (href.contains("http") || href.contains("https")) {
                            return href;
                        } else {
                            if (href.length() == 0) {
                                return "";
                            }
                            final boolean startsWithSlash = String.valueOf(href.charAt(0)).equals("/");
                            final String port = Optional.of(baseUrl.getPort())
                                    .filter(integer -> integer != -1)
                                    .map(integer -> ":" + integer)
                                    .orElse("");
                            if (href.length() >= 2 &&
                                    startsWithSlash &&
                                    String.valueOf(href.charAt(1)).equals("/")) {
                                return baseUrl.getProtocol() + "://" + baseUrl.getHost() + port + "/" + href.substring(2);
                            } else if (startsWithSlash) {
                                return baseUrl.getProtocol() + "://" + baseUrl.getHost() + port + "/" + href.substring(1);
                            }
                        }
                        logger.warn("Url format not handled " + href);
                        return "";
                    } catch (final MalformedURLException | URISyntaxException exception) {
                        logger.warn("Url format not handled {}", href);
                        return "";
                    }
                })
                .filter(this::validateUrlString)
                .map(Unchecked.function(URL::new))
                .collect(Collectors.toList());
    }

    private boolean validateUrlString(final String urlString) {
        final boolean isValid = urlValidator.isValid(urlString);
        if (!isValid) {
            logger.warn("Invalid url detected from crawl, filtering " + urlString);
        }
        if (urlString.contains("https://web.archive.org/")) {
            return true;
        }
        return isValid;
    }

}
