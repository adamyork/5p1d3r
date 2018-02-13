package com.github.adamyork.fx5p1d3r.application.view.method.command;

import org.controlsfx.control.ToggleSwitch;
import org.springframework.stereotype.Component;

/**
 * Created by Adam York on 2/12/2018.
 * Copyright 2018
 */
@Component
public class ToggleWhenListUrlCommand implements ManageMethodTogglesCommand {
    @Override
    public void execute(final ToggleSwitch requestThrottlingToggleSwitch,
                        final ToggleSwitch multiThreadingToggleSwitch,
                        final ToggleSwitch followLinksToggleSwitch) {
        multiThreadingToggleSwitch.setDisable(false);
        requestThrottlingToggleSwitch.setDisable(false);
    }
}
