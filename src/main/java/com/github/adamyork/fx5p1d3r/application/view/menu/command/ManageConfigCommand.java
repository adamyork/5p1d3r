package com.github.adamyork.fx5p1d3r.application.view.menu.command;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.adamyork.fx5p1d3r.common.model.ApplicationFormState;

import java.io.File;

public interface ManageConfigCommand {

    void execute(final ObjectMapper mapper, final File file, final ApplicationFormState applicationFormState);

}
