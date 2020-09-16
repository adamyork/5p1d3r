package com.github.adamyork.fx5p1d3r.application.service.io;

import com.github.adamyork.fx5p1d3r.common.model.ApplicationFormState;
import com.github.adamyork.fx5p1d3r.common.service.AlertService;
import com.github.adamyork.fx5p1d3r.common.service.OutputService;
import com.github.adamyork.fx5p1d3r.common.service.progress.ApplicationProgressService;
import groovy.lang.GroovyShell;
import javafx.collections.ObservableList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jooq.lambda.tuple.Tuple2;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.context.MessageSource;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Created by Adam York on 2/28/2017.
 * Copyright 2017
 */
public class CsvDocumentParser extends BaseDocumentParser {

    private static final Logger logger = LogManager.getLogger(CsvDocumentParser.class);

    private final OutputService outputService;

    public CsvDocumentParser(final ApplicationFormState applicationFormState,
                             final ApplicationProgressService progressService,
                             final MessageSource messageSource,
                             final AlertService alertService,
                             final OutputService outputService) {
        super(applicationFormState, progressService, messageSource, alertService);
        this.outputService = outputService;
    }

    @Override
    public void parse(final Document document, final String query) {
        final List<String[]> objectList = new ArrayList<>();
        final Tuple2<Elements, ObservableList<File>> elementsAndWatchList = getElementsAndWatchList(document, query);
        elementsAndWatchList.v1.forEach(element -> elementsAndWatchList.v2.forEach(file -> {
            final Tuple2<String, GroovyShell> groovyObjects = initGroovy(file, element, document);
            try {
                logger.debug("Transforming " + element.tagName());
                final Object[] objectArray = (Object[]) groovyObjects.v2.evaluate(groovyObjects.v1);
                final String[] stringArray = Arrays.copyOf(objectArray, objectArray.length, String[].class);
                objectList.add(stringArray);
            } catch (final Exception exception) {
                alertService.warn(messageSource.getMessage("alert.bad.transform.header", null, Locale.getDefault()),
                        messageSource.getMessage("alert.bad.transform.content", null, Locale.getDefault()));
                logger.debug("Transform failed");
            }
        }));
        objectList.forEach(outputService::writeEntries);
    }

}
