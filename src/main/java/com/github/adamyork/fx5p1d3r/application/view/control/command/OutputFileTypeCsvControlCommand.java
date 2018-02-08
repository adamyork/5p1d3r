package com.github.adamyork.fx5p1d3r.application.view.control.command;

import com.github.adamyork.fx5p1d3r.common.model.OutputFileType;
import org.springframework.stereotype.Component;

/**
 * Created by Adam York on 1/2/2018.
 * Copyright 2018
 */
@Component
public class OutputFileTypeCsvControlCommand implements ControlCommand {
    @Override
    public OutputFileType execute() {
        return OutputFileType.CSV;
    }
}
