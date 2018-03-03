package com.github.adamyork.fx5p1d3r.application.view.control.command;

import com.github.adamyork.fx5p1d3r.common.command.ApplicationCommand;
import com.github.adamyork.fx5p1d3r.common.command.CommandMap;
import com.github.adamyork.fx5p1d3r.common.model.ApplicationFormState;
import com.github.adamyork.fx5p1d3r.common.model.OutputFileType;
import com.github.adamyork.fx5p1d3r.common.model.UrlMethod;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

/**
 * Created by Adam York on 1/2/2018.
 * Copyright 2018
 */
@Component
public class StartSpiderCommand implements ControlStartCommand {

    private final ApplicationFormState applicationFormState;
    private final CommandMap<UrlMethod, ApplicationCommand> urlMethodCommandMap;

    @Inject
    public StartSpiderCommand(final ApplicationFormState applicationFormState,
                              final CommandMap<UrlMethod, ApplicationCommand> urlMethodCommandMap) {
        this.applicationFormState = applicationFormState;
        this.urlMethodCommandMap = urlMethodCommandMap;
    }

    @Override
    public Boolean execute(final String nullSafeFileString, final int extensionIndex, final CommandMap<Boolean, ControlCommand> controlCommandMap) {
        final String fileTypeString = nullSafeFileString.substring(extensionIndex);
        applicationFormState.setOutputFileType(controlCommandMap
                .getCommand(fileTypeString.equals(OutputFileType.JSON.toString())).execute());
        applicationFormState.setOutputFile(nullSafeFileString);
        urlMethodCommandMap.getCommand(applicationFormState.getUrlMethod()).execute();
        return true;
    }
}
