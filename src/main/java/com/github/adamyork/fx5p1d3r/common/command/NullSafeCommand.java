package com.github.adamyork.fx5p1d3r.common.command;

import java.io.File;

/**
 * Created by Adam York on 3/17/2017.
 * Copyright 2017
 */
public interface NullSafeCommand<T> {

    void execute(final T listView, final int index);

    void execute(final T listView, final File file);

    T execute(final T instance);

}
