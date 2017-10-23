package com.github.adamyork.fx5p1d3r.application.command;

import com.github.adamyork.fx5p1d3r.common.OutputManager;
import com.github.adamyork.fx5p1d3r.common.command.OutputCommand;
import com.github.adamyork.fx5p1d3r.common.model.OutputCsvObject;
import com.github.adamyork.fx5p1d3r.common.model.OutputJsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by Adam York on 3/1/2017.
 * Copyright 2017
 */
@Component
public class OutputFileTypeCsvCommand implements OutputCommand {

    private final OutputManager outputManager;

    @Autowired
    public OutputFileTypeCsvCommand(final OutputManager outputManager) {
        this.outputManager = outputManager;
    }

    @Override
    public void execute(final OutputJsonObject outputJsonObject) {
        //no-op
    }

    @Override
    public void execute(final OutputCsvObject outputCsvObject) {
        final List<String[]> objectList = outputCsvObject.getObjectList();
        objectList.forEach(outputManager::writeCsvEntry);
    }

}
