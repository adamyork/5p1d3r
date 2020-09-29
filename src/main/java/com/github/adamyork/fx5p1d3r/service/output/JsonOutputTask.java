package com.github.adamyork.fx5p1d3r.service.output;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.adamyork.fx5p1d3r.ApplicationFormState;
import com.github.adamyork.fx5p1d3r.service.output.data.OutputJsonObject;
import com.github.adamyork.fx5p1d3r.service.progress.ApplicationProgressService;
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
public class JsonOutputTask extends BaseOutputTask {

    private static final Logger logger = LogManager.getLogger(CsvOutputTask.class);

    private final Object work;

    public JsonOutputTask(final ApplicationFormState applicationFormState,
                          final ApplicationProgressService progressService,
                          final Object work) {
        super(applicationFormState, progressService);
        this.work = work;
    }

    @Override
    protected Boolean call() {
        logger.debug("writing json output");
        final Tuple2<File, byte[]> fileAndBytes = getFileAndBytes();
        final ObjectMapper mapper = new ObjectMapper();
        try {
            final OutputJsonObject jsonObject = mapper.readValue(fileAndBytes.v2, OutputJsonObject.class);
            jsonObject.getObjectList().add(work);
            Unchecked.consumer(o -> mapper.writeValue(fileAndBytes.v1, jsonObject)).accept(null);
        } catch (final IOException e) {
            final OutputJsonObject outputObject = new OutputJsonObject.Builder(new ArrayList<>()).build();
            outputObject.getObjectList().add(work);
            Unchecked.consumer(o -> mapper.writeValue(fileAndBytes.v1, outputObject)).accept(null);
        }
        return true;
    }


}
