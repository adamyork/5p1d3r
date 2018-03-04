package com.github.adamyork.fx5p1d3r.application.view.control.command;

import com.github.adamyork.fx5p1d3r.common.command.CommandMap;
import org.springframework.stereotype.Component;

/**
 * Created by Adam York on 1/2/2018.
 * Copyright 2018
 */
@Component
public class ControlNoopCommand implements ControlStartCommand {

    @Override
    public Boolean execute(final String nullSafeFileString, final int extensionIndex, CommandMap<Boolean, ControlCommand> controlCommandMap) {
        return false;
    }

}
