package com.github.adamyork.fx5p1d3r.service.output;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.adamyork.fx5p1d3r.ApplicationFormState;
import com.github.adamyork.fx5p1d3r.LogDirectoryHelper;
import com.github.adamyork.fx5p1d3r.service.output.data.OutputJsonObject;
import com.github.adamyork.fx5p1d3r.service.progress.ProgressService;
import com.github.adamyork.fx5p1d3r.service.url.data.DocumentListWithMemo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;
import org.jooq.lambda.tuple.Tuple4;
import org.jsoup.nodes.Document;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by Adam York on 2/28/2017.
 * Copyright 2017
 */
public class JsonOutputTask extends BaseOutputTask {

    private static final Logger logger = LogManager.getLogger(JsonOutputTask.class);

    private final List<Tuple4<List<Object>, Document, List<URL>, Optional<DocumentListWithMemo>>> transformed;

    public JsonOutputTask(final ApplicationFormState applicationFormState,
                          final ProgressService progressService,
                          final List<Tuple4<List<Object>, Document, List<URL>, Optional<DocumentListWithMemo>>> transformed) {
        super(applicationFormState, progressService);
        this.transformed = transformed;
    }

    @Override
    protected List<Tuple4<List<Boolean>, Document, List<URL>, Optional<DocumentListWithMemo>>> call() {
        LogDirectoryHelper.manage();
        logger.debug("writing json output");
        return transformed.stream()
                .map(result -> {
                    final List<Object> elements = result.v1;
                    final Document document = result.v2;
                    final List<URL> urls = result.v3;
                    final Optional<DocumentListWithMemo> maybeMemo = result.v4;
                    final ObjectMapper mapper = new ObjectMapper();
                    return Tuple.tuple(elements.stream().map(entry -> {
                        final Tuple2<File, byte[]> fileAndBytes = getFileAndBytes();
                        try {
                            final OutputJsonObject jsonObject = mapper.readValue(fileAndBytes.v2, OutputJsonObject.class);
                            jsonObject.getObjectList().add(entry);
                            mapper.writeValue(fileAndBytes.v1, jsonObject);
                            return true;
                        } catch (final Exception exception) {
                            final OutputJsonObject outputObject = new OutputJsonObject.Builder(new ArrayList<>()).build();
                            outputObject.getObjectList().add(entry);
                            try {
                                mapper.writeValue(fileAndBytes.v1, outputObject);
                            } catch (final Exception exception1) {
                                logger.error("cant write json output", exception1);
                            }
                            return false;
                        }
                    }).collect(Collectors.toList()), document, urls, maybeMemo);

                })
                .collect(Collectors.toList());

    }


}
