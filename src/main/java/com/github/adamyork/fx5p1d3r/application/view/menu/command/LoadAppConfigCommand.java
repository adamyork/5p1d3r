package com.github.adamyork.fx5p1d3r.application.view.menu.command;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.github.adamyork.fx5p1d3r.application.view.menu.FormStateDeserializer;
import com.github.adamyork.fx5p1d3r.common.model.ApplicationFormState;
import org.jooq.lambda.Unchecked;

import java.io.File;

/**
 * Created by Adam York on 1/2/2018.
 * Copyright 2018
 */
public class LoadAppConfigCommand implements ManageConfigCommand {
    @Override
    public void execute(final ObjectMapper mapper, final File file, final ApplicationFormState applicationFormState) {
        mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
        final SimpleModule module = new SimpleModule();
        module.addDeserializer(ApplicationFormState.class, new FormStateDeserializer(applicationFormState));
        mapper.registerModule(module);
        final ApplicationFormState state = Unchecked.function(func -> mapper.readValue(file, ApplicationFormState.class)).apply(null);
        state.notifyChanged();
    }
}
