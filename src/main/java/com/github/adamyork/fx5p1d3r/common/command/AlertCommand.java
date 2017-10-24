package com.github.adamyork.fx5p1d3r.common.command;

import java.io.File;
import java.net.URL;
import java.util.List;

/**
 * Created by Adam York on 10/24/2017.
 * Copyright 2017
 */
public interface AlertCommand<R> {

    R execute(final String header, final String content);

    R execute(final String header, final String content, final List<URL> urls);

    R execute(final String header, final String content, final File file);

}
