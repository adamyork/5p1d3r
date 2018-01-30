package com.github.adamyork.fx5p1d3r.application.view.menu.command;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.adamyork.fx5p1d3r.common.model.ApplicationFormState;
import org.jooq.lambda.Unchecked;

import java.io.File;

public class SaveAppConfigCommand implements ManageConfigCommand {

    @Override
    public void execute(final ObjectMapper mapper, final File file, final ApplicationFormState applicationFormState) {
        Unchecked.consumer(consumer -> mapper.writerWithDefaultPrettyPrinter()
                .writeValue(file, applicationFormState))
                .accept(null);
    }
}
