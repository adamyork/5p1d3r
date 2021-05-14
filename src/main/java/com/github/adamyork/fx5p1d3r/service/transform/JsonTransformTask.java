package com.github.adamyork.fx5p1d3r.service.transform;

import com.github.adamyork.fx5p1d3r.ApplicationFormState;
import com.github.adamyork.fx5p1d3r.LogDirectoryHelper;
import com.github.adamyork.fx5p1d3r.service.progress.AlertService;
import com.github.adamyork.fx5p1d3r.service.progress.ApplicationProgressService;
import com.github.adamyork.fx5p1d3r.service.url.data.DocumentListWithMemo;
import groovy.lang.GroovyShell;
import javafx.collections.ObservableList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;
import org.jooq.lambda.tuple.Tuple3;
import org.jooq.lambda.tuple.Tuple4;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.context.MessageSource;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by Adam York on 2/28/2017.
 * Copyright 2017
 */
@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class JsonTransformTask extends BaseResultTransform {

    private static final Logger logger = LogManager.getLogger(JsonTransformTask.class);

    private final List<Tuple3<List<Elements>, Document, List<URL>>> processed;
    private final Optional<DocumentListWithMemo> maybeMemo;


    public JsonTransformTask(final ApplicationFormState applicationFormState,
                             final ApplicationProgressService progressService,
                             final MessageSource messageSource,
                             final AlertService alertService,
                             final List<Tuple3<List<Elements>, Document, List<URL>>> processed,
                             final Optional<DocumentListWithMemo> maybeMemo) {
        super(applicationFormState, progressService, messageSource, alertService);
        this.processed = processed;
        this.maybeMemo = maybeMemo;
    }


    @Override
    public List<Tuple4<List<Object>, Document, List<URL>, Optional<DocumentListWithMemo>>> call() {
        LogDirectoryHelper.manage();
        logger.debug("processing selections");
        final ObservableList<File> resultTransforms = applicationFormState.getResultTransformObservableList();
        return processed.stream()
                .map(object -> {
                    final List<Elements> allElements = object.v1;
                    final Document document = object.v2;
                    final List<URL> urls = object.v3;
                    return Tuple.tuple(allElements.stream()
                            .flatMap(elements -> {
                                if (elements.size() == 0) {
                                    alertService.warn(messageSource.getMessage("alert.no.elements.header", null, Locale.getDefault()),
                                            messageSource.getMessage("alert.no.elements.content", null, Locale.getDefault()));

                                }
                                return elements.stream()
                                        .flatMap(element -> resultTransforms.stream()
                                                .map(transform -> {
                                                    LogDirectoryHelper.manage();
                                                    Object result = null;
                                                    try {
                                                        final Tuple2<String, GroovyShell> groovyObjects = initGroovy(transform, element, document);
                                                        logger.debug("Transforming " + element.tagName());
                                                        result = groovyObjects.v2.evaluate(groovyObjects.v1);
                                                    } catch (final Exception exception) {
                                                        applicationFormState.setTransformFailed(true);
                                                        logger.debug("Transform failed", exception);
                                                    }
                                                    return result;
                                                })
                                                .filter(Objects::nonNull));
                            }).collect(Collectors.toList()), document, urls, maybeMemo);
                })
                .collect(Collectors.toList());
    }

}
