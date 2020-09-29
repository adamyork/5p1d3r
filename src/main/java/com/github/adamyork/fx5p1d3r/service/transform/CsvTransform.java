package com.github.adamyork.fx5p1d3r.service.transform;

import com.github.adamyork.fx5p1d3r.ApplicationFormState;
import com.github.adamyork.fx5p1d3r.service.progress.AlertService;
import com.github.adamyork.fx5p1d3r.service.progress.ApplicationProgressService;
import groovy.lang.GroovyShell;
import javafx.collections.ObservableList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jooq.lambda.tuple.Tuple2;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.context.MessageSource;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by Adam York on 2/28/2017.
 * Copyright 2017
 */
public class CsvTransform extends BaseResultTransform implements TransformService {

    private static final Logger logger = LogManager.getLogger(CsvTransform.class);

    public CsvTransform(final ApplicationFormState applicationFormState,
                        final ApplicationProgressService progressService,
                        final MessageSource messageSource,
                        final AlertService alertService) {
        super(applicationFormState, progressService, messageSource, alertService);
    }

    @Override
    public List<Object> transform(final Elements elements, final Document document) {
        if (elements.size() == 0) {
            alertService.warn(messageSource.getMessage("alert.no.elements.header", null, Locale.getDefault()),
                    messageSource.getMessage("alert.no.elements.content", null, Locale.getDefault()));
        }
        final ObservableList<File> resultTransforms = applicationFormState.getResultTransformObservableList();
        return elements.stream()
                .flatMap(element -> resultTransforms.stream()
                        .map(transform -> {
                            final Tuple2<String, GroovyShell> groovyObjects = initGroovy(transform, element, document);
                            Object result = null;
                            try {
                                logger.debug("Transforming " + element.tagName());
                                final Object[] objectArray = (Object[]) groovyObjects.v2.evaluate(groovyObjects.v1);
                                result = Arrays.copyOf(objectArray, objectArray.length, String[].class);
                            } catch (final Exception exception) {
                                alertService.warn(messageSource.getMessage("alert.bad.transform.header", null, Locale.getDefault()),
                                        messageSource.getMessage("alert.bad.transform.content", null, Locale.getDefault()));
                                logger.debug("Transform failed");
                            }
                            return result;
                        })
                        .filter(Objects::nonNull))
                .collect(Collectors.toList());
//        final ExecutorService executorService = Executors.newFixedThreadPool(1);
//        objectList.forEach(strings -> executorService.submit(new CsvOutputTask(applicationFormState, progressService, strings)));
    }

}
