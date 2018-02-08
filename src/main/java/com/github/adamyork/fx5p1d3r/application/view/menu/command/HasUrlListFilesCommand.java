package com.github.adamyork.fx5p1d3r.application.view.menu.command;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.adamyork.fx5p1d3r.common.model.ApplicationFormState;

import java.io.File;

/**
 * Created by Adam York on 1/2/2018.
 * Copyright 2018
 */
public class HasUrlListFilesCommand implements FormStateConfigCommand {
    @Override
    public void execute(final ApplicationFormState applicationFormState, final JsonNode jsonNode) {

    }

    @Override
    public void execute(final ApplicationFormState applicationFormState, final String urlFileListString) {
        applicationFormState.setUrlListFile(new File(urlFileListString));
    }
}
