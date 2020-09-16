package com.github.adamyork.fx5p1d3r.common.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.adamyork.fx5p1d3r.common.Validator;
import com.github.adamyork.fx5p1d3r.common.model.ApplicationFormState;
import com.github.adamyork.fx5p1d3r.common.model.OutputJsonObject;
import com.github.adamyork.fx5p1d3r.common.service.progress.ApplicationProgressService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jooq.lambda.Unchecked;
import org.jooq.lambda.tuple.Tuple2;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Adam York on 2/28/2017.
 * Copyright 2017
 */
public class JsonOutputService extends BaseOutputService {

    private static final Logger logger = LogManager.getLogger(CsvOutputService.class);

    public JsonOutputService(final ApplicationFormState applicationFormState,
                             final Validator validator,
                             final ApplicationProgressService progressService) {
        super(applicationFormState, validator, progressService);
    }

    @Override
    public void writeEntry(final Object object) {
        logger.debug("writing json output");
        final Tuple2<File, byte[]> fileAndBytes = getFileAndBytes();
        final ObjectMapper mapper = new ObjectMapper();
        try {
            final OutputJsonObject jsonObject = mapper.readValue(fileAndBytes.v2, OutputJsonObject.class);
            jsonObject.getObjectList().add(object);
            Unchecked.consumer(o -> mapper.writeValue(fileAndBytes.v1, jsonObject)).accept(null);
        } catch (final IOException e) {
            final OutputJsonObject outputObject = new OutputJsonObject.Builder(new ArrayList<>()).build();
            outputObject.getObjectList().add(object);
            Unchecked.consumer(o -> mapper.writeValue(fileAndBytes.v1, outputObject)).accept(null);
        }
    }

}
