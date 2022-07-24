package com.github.adamyork.fx5p1d3r.service.url;

import java.io.File;

/**
 * Created by Adam York on 8/28/2020.
 * Copyright 2020
 */
public interface SpiderService {

    default void execute() {
    }

    default void execute(final File urlListFile) {
    }

    void close();

}
