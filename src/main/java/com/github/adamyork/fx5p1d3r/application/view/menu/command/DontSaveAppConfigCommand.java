package com.github.adamyork.fx5p1d3r.application.view.menu.command;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.adamyork.fx5p1d3r.common.model.ApplicationFormState;

import java.io.File;

/**
 * Created by Adam York on 1/2/2018.
 * Copyright 2018
 */
public class DontSaveAppConfigCommand implements ManageConfigCommand {
    @Override
    public void execute(final ObjectMapper mapper, final File file, final ApplicationFormState applicationFormState) {
    }
}
