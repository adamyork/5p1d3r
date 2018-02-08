package com.github.adamyork.fx5p1d3r.application.view.control.command;

import com.github.adamyork.fx5p1d3r.common.model.OutputFileType;
import org.springframework.stereotype.Component;

@Component
public class OutputFileTypeJsonControlCommand implements ControlCommand {
    @Override
    public OutputFileType execute() {
        return OutputFileType.JSON;
    }
}
