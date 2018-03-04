package com.github.adamyork.fx5p1d3r.common.command.io;

/**
 * Created by Adam York on 10/28/2017.
 * Copyright 2017
 */
@SuppressWarnings("SameReturnValue")
public interface HandlerCommand<T, F, R> {

    R execute(final T instance, final F handler);

    R execute(final T instance, final F handler, final String header, final String content);

}
