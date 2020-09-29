package com.github.adamyork.fx5p1d3r.service.output;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.github.adamyork.fx5p1d3r.ApplicationFormState;
import com.github.adamyork.fx5p1d3r.service.progress.ApplicationProgressService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jooq.lambda.Unchecked;
import org.jooq.lambda.tuple.Tuple2;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Adam York on 2/28/2017.
 * Copyright 2017
 */
public class CsvOutputTask extends BaseOutputTask {

    private static final Logger logger = LogManager.getLogger(CsvOutputTask.class);

    private final String[] work;


    public CsvOutputTask(final ApplicationFormState applicationFormState,
                         final ApplicationProgressService progressService,
                         final String[] work) {
        super(applicationFormState, progressService);
        this.work = work;
    }

    @Override
    protected Boolean call() {
        logger.debug("writing csv output");
        final Tuple2<File, byte[]> fileAndBytes = getFileAndBytes();
        final CsvMapper mapper = new CsvMapper();
        mapper.enable(CsvParser.Feature.WRAP_AS_ARRAY);
        final MappingIterator<Object> iterator = Unchecked.function(o -> mapper.readerFor(String[].class).readValues(fileAndBytes.v2)).apply(null);
        final List<List<String>> values = new ArrayList<>();
        while (iterator.hasNext()) {
            final String[] innerValues = (String[]) iterator.next();
            final List<String> entry = new ArrayList<>(Arrays.asList(innerValues));
            values.add(entry);
        }
        values.add(Arrays.asList(work));
        Unchecked.consumer(o -> mapper.writeValue(fileAndBytes.v1, values)).accept(mapper);
        return true;
    }

}
