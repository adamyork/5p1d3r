package com.github.adamyork.fx5p1d3r.common.command;

import com.github.adamyork.fx5p1d3r.common.model.OutputCsvObject;
import com.github.adamyork.fx5p1d3r.common.model.OutputJsonObject;

/**
 * Created by Adam York on 3/17/2017.
 * Copyright 2017
 */
public interface OutputCommand {

    void execute(final OutputJsonObject outputJsonObject);

    void execute(final OutputCsvObject outputCsvObject);

}
