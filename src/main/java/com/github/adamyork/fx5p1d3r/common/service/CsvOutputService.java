package com.github.adamyork.fx5p1d3r.common.service;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.github.adamyork.fx5p1d3r.common.Validator;
import com.github.adamyork.fx5p1d3r.common.model.ApplicationFormState;
import com.github.adamyork.fx5p1d3r.common.service.progress.ProgressService;
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
public class CsvOutputService extends BaseOutputService {


    public CsvOutputService(final ApplicationFormState applicationFormState,
                            final Validator validator,
                            final ProgressService progressService) {
        super(applicationFormState, validator, progressService);
    }

    @Override
    public void writeEntries(String[] object) {
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
        values.add(Arrays.asList(object));
        Unchecked.consumer(o -> mapper.writeValue(fileAndBytes.v1, values)).accept(mapper);
    }

}
