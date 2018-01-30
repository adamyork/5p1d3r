package com.github.adamyork.fx5p1d3r.application.view.menu.command;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.adamyork.fx5p1d3r.common.model.ApplicationFormState;

public interface FormStateConfigCommand {

    void execute(final ApplicationFormState applicationFormState, final JsonNode jsonNode);

    void execute(final ApplicationFormState applicationFormState, final String urlFileListString);

}
