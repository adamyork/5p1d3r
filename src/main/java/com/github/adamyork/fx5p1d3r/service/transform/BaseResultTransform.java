package com.github.adamyork.fx5p1d3r.service.transform;

import com.github.adamyork.fx5p1d3r.ApplicationFormState;
import com.github.adamyork.fx5p1d3r.service.progress.AlertService;
import com.github.adamyork.fx5p1d3r.service.progress.ApplicationProgressService;
import com.github.adamyork.fx5p1d3r.service.url.data.DocumentListWithMemo;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import javafx.concurrent.Task;
import org.apache.commons.io.FileUtils;
import org.jooq.lambda.Unchecked;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;
import org.jooq.lambda.tuple.Tuple4;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.context.MessageSource;

import java.io.File;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

/**
 * Created by Adam York on 8/28/2020.
 * Copyright 2020
 */
public class BaseResultTransform extends Task<List<Tuple4<List<Object>, Document, List<URL>, Optional<DocumentListWithMemo>>>> {

    protected final ApplicationFormState applicationFormState;
    protected final ApplicationProgressService progressService;
    protected final MessageSource messageSource;
    protected final AlertService alertService;

    public BaseResultTransform(final ApplicationFormState applicationFormState,
                               final ApplicationProgressService progressService,
                               final MessageSource messageSource,
                               final AlertService alertService) {
        this.applicationFormState = applicationFormState;
        this.progressService = progressService;
        this.messageSource = messageSource;
        this.alertService = alertService;
    }

    protected Tuple2<String, GroovyShell> initGroovy(final File file, final Element element, final Document document) {
        final Binding binding = new Binding();
        final String script = Unchecked.function(o -> FileUtils.readFileToString(file, StandardCharsets.UTF_8))
                .apply(null);
        final GroovyShell shell = new GroovyShell(binding);
        binding.setProperty("element", element);
        binding.setProperty("document", document);
        return Tuple.tuple(script, shell);
    }

    @Override
    protected List<Tuple4<List<Object>, Document, List<URL>, Optional<DocumentListWithMemo>>> call() {
        return null;
    }

}
