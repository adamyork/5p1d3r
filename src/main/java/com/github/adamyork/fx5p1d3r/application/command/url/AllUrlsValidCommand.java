package com.github.adamyork.fx5p1d3r.application.command.url;

import com.github.adamyork.fx5p1d3r.common.command.alert.AlertCommand;
import com.github.adamyork.fx5p1d3r.common.model.AllValidUrls;
import org.jooq.lambda.Unchecked;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Adam York on 10/24/2017.
 * Copyright 2017
 */
@Component
public class AllUrlsValidCommand implements AlertCommand<AllValidUrls> {

    @Override
    public AllValidUrls execute(final String header, final String content) {
        return null;
    }

    @Override
    public AllValidUrls execute(final String header, final String content, final List<String> urlStrings) {
        final List<URL> urls = urlStrings.stream().map(Unchecked.function(URL::new)).collect(Collectors.toList());
        return new AllValidUrls(true, urls);
    }

    @Override
    public AllValidUrls execute(final String header, final String content, final File file) {
        return null;
    }

}
