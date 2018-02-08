package com.github.adamyork.fx5p1d3r.application.view.control.command;

import com.github.adamyork.fx5p1d3r.common.command.CommandMap;
import org.springframework.stereotype.Component;

@Component
public class ControlNoopCommand implements ControlStartCommand {

    @Override
    public void execute(final String nullSafeFileString, final int extensionIndex, CommandMap<Boolean, ControlCommand> controlCommandMap) {

    }

}
