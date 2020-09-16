package com.github.adamyork.fx5p1d3r.application.service.io;

import com.github.adamyork.fx5p1d3r.common.model.ApplicationFormState;
import com.github.adamyork.fx5p1d3r.common.service.AlertService;
import com.github.adamyork.fx5p1d3r.common.service.progress.ApplicationProgressService;
import com.github.adamyork.fx5p1d3r.common.service.progress.ProgressType;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import javafx.collections.ObservableList;
import org.apache.commons.io.FileUtils;
import org.jooq.lambda.Unchecked;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.context.MessageSource;

import java.io.File;
import java.util.Locale;

/**
 * Created by Adam York on 8/28/2020.
 * Copyright 2020
 */
public class BaseDocumentParser implements DocumentParserService {

    protected final ApplicationFormState applicationFormState;
    protected final ApplicationProgressService progressService;
    protected final MessageSource messageSource;
    protected final AlertService alertService;

    public BaseDocumentParser(final ApplicationFormState applicationFormState,
                              final ApplicationProgressService progressService,
                              final MessageSource messageSource,
                              final AlertService alertService) {
        this.applicationFormState = applicationFormState;
        this.progressService = progressService;
        this.messageSource = messageSource;
        this.alertService = alertService;
    }

    protected Tuple2<Elements, ObservableList<File>> getElementsAndWatchList(final Document document, final String query) {
        progressService.updateProgress(ProgressType.SELECTOR);
        final Elements elements = document.select(query);
        final ObservableList<File> resultTransformObservableList = applicationFormState.getResultTransformObservableList();
        progressService.updateProgress(ProgressType.TRANSFORM);
        if (elements.size() == 0) {
            alertService.warn(messageSource.getMessage("alert.no.elements.header", null, Locale.getDefault()),
                    messageSource.getMessage("alert.no.elements.content", null, Locale.getDefault()));
        }
        return Tuple.tuple(elements, resultTransformObservableList);
    }

    protected Tuple2<String, GroovyShell> initGroovy(final File file, final Element element, final Document document) {
        final Binding binding = new Binding();
        final String script = Unchecked.function(o -> FileUtils.readFileToString(file)).apply(null);
        final GroovyShell shell = new GroovyShell(binding);
        binding.setProperty("element", element);
        binding.setProperty("document", document);
        return Tuple.tuple(script, shell);
    }

    @Override
    public void parse(Document document, String query) {

    }
}
