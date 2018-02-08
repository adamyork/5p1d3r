package com.github.adamyork.fx5p1d3r.application.view.control.command;

import com.github.adamyork.fx5p1d3r.common.command.CommandMap;

public interface ControlStartCommand {

    void execute(final String nullSafeFileString, final int extensionIndex, final CommandMap<Boolean, ControlCommand> controlCommandMap);

}
