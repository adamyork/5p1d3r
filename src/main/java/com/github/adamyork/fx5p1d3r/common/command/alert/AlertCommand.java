package com.github.adamyork.fx5p1d3r.common.command.alert;

import java.io.File;
import java.util.List;

/**
 * Created by Adam York on 10/24/2017.
 * Copyright 2017
 */
@SuppressWarnings("UnusedReturnValue")
public interface AlertCommand<R> {

    R execute(final String header, final String content);

    R execute(final String header, final String content, final List<String> urlStrings);

    R execute(final String header, final String content, final File file);

}
