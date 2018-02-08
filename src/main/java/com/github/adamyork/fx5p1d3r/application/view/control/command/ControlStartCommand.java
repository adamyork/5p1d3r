package com.github.adamyork.fx5p1d3r.application.view.control.command;

import com.github.adamyork.fx5p1d3r.common.command.CommandMap;

/**
 * Created by Adam York on 1/2/2018.
 * Copyright 2018
 */
public interface ControlStartCommand {

    void execute(final String nullSafeFileString, final int extensionIndex, final CommandMap<Boolean, ControlCommand> controlCommandMap);

}
