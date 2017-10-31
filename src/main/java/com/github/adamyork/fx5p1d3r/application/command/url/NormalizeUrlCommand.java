package com.github.adamyork.fx5p1d3r.application.command.url;

import com.github.adamyork.fx5p1d3r.common.command.ValidatorCommand;
import javafx.scene.control.TextField;
import org.jooq.lambda.Unchecked;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.URI;
import java.util.List;

/**
 * Created by Adam York on 10/31/2017.
 * Copyright 2017
 */
@Component
public class NormalizeUrlCommand implements ValidatorCommand {

    @Override
    public void execute(final List<String> urlStrings) {

    }

    @Override
    public void execute(final File file, final TextField textField) {

    }

    @Override
    public String execute(final Element element, final String href) {
        final String baseUri = element.baseUri();
        final URI uri = Unchecked.function(t -> new URI(baseUri)).apply(null);
        final String baseUriTrimmed = uri.getScheme() + "://" + uri.getHost();
        return baseUriTrimmed + href;
    }
}
