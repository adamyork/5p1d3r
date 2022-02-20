package com.github.adamyork.fx5p1d3r.service.output;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.github.adamyork.fx5p1d3r.ApplicationFormState;
import com.github.adamyork.fx5p1d3r.LogDirectoryHelper;
import com.github.adamyork.fx5p1d3r.service.progress.ProgressService;
import com.github.adamyork.fx5p1d3r.service.url.data.DocumentListWithMemo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jooq.lambda.Unchecked;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;
import org.jooq.lambda.tuple.Tuple4;
import org.jsoup.nodes.Document;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by Adam York on 2/28/2017.
 * Copyright 2017
 */
public class CsvOutputTask extends BaseOutputTask {

    private static final Logger logger = LogManager.getLogger(CsvOutputTask.class);

    private final List<Tuple4<List<Object>, Document, List<URL>, Optional<DocumentListWithMemo>>> transformed;

    public CsvOutputTask(final ApplicationFormState applicationFormState,
                         final ProgressService progressService,
                         final List<Tuple4<List<Object>, Document, List<URL>, Optional<DocumentListWithMemo>>> transformed) {
        super(applicationFormState, progressService);
        this.transformed = transformed;
    }

    @Override
    protected List<Tuple4<List<Boolean>, Document, List<URL>, Optional<DocumentListWithMemo>>> call() {
        LogDirectoryHelper.manage();
        logger.debug("writing csv output");
        return transformed.stream()
                .map(result -> {
                    final List<Object> elements = result.v1;
                    final Document document = result.v2;
                    final List<URL> urls = result.v3;
                    final Optional<DocumentListWithMemo> maybeMemo = result.v4;
                    final CsvMapper mapper = new CsvMapper();
                    mapper.enable(CsvParser.Feature.WRAP_AS_ARRAY);
                    return Tuple.tuple(elements.stream().map(entry -> {
                        final Tuple2<File, byte[]> fileAndBytes = getFileAndBytes();
                        final MappingIterator<Object> iterator = Unchecked.function(o -> mapper.readerFor(String[].class)
                                .readValues(fileAndBytes.v2)).apply(null);
                        final List<List<String>> values = new ArrayList<>();
                        while (iterator.hasNext()) {
                            final String[] innerValues = (String[]) iterator.next();
                            final List<String> e = new ArrayList<>(Arrays.asList(innerValues));
                            values.add(e);
                        }
                        values.add(Arrays.asList((String[]) entry));
                        Unchecked.consumer(o -> mapper.writeValue(fileAndBytes.v1, values)).accept(mapper);
                        return true;
                    }).collect(Collectors.toList()), document, urls, maybeMemo);

                })
                .collect(Collectors.toList());
    }

}
