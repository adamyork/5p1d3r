package com.github.adamyork.fx5p1d3r.application.command;

import com.github.adamyork.fx5p1d3r.common.OutputManager;
import com.github.adamyork.fx5p1d3r.common.command.OutputCommand;
import com.github.adamyork.fx5p1d3r.common.model.OutputCSVObject;
import com.github.adamyork.fx5p1d3r.common.model.OutputJSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by Adam York on 3/1/2017.
 * Copyright 2017
 */
@Component
public class OutputFileTypeJsonCommand implements OutputCommand {

    private final OutputManager outputManager;

    @Autowired
    public OutputFileTypeJsonCommand(final OutputManager outputManager) {
        this.outputManager = outputManager;
    }

    @Override
    public void execute(final OutputJSONObject outputJSONObject) {
        final List<Object> objectList = outputJSONObject.getObjectList();
        objectList.forEach(outputManager::writeJSONEntry);
    }

    @Override
    public void execute(final OutputCSVObject outputCSVObject) {
        //no-op
    }

}
