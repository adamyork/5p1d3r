package com.github.adamyork.fx5p1d3r.application.view.method.command;

import org.controlsfx.control.ToggleSwitch;

/**
 * Created by Adam York on 2/12/2018.
 * Copyright 2018
 */
public interface ManageMethodTogglesCommand {

    void execute(final ToggleSwitch requestThrottlingToggleSwitch,
                 final ToggleSwitch multiThreadingToggleSwitch,
                 final ToggleSwitch followLinksToggleSwitch);
}
