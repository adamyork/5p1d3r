package com.github.adamyork.fx5p1d3r.common.command;

import org.springframework.stereotype.Component;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * Created by Adam York on 3/17/2017.
 * Copyright 2017
 */
@Component
public class NoopCommand<T> implements NullSafeCommand<T>, ApplicationCommand, AlertCommand<T> {

    @Override
    public void execute(final T listView, final int index) {}

    @Override
    public void execute(final T listView, final File file) {}

    @Override
    public T execute(final T instance) {
        return instance;
    }

    @Override
    public void execute() {}

    @Override
    public void execute(final List<URL> urls) {}

    @Override
    public void execute(final List<URL> urls, final ExecutorService executorService) {}

    @Override
    public T execute(final String header, final String content) {
        return null;
    }

    @Override
    public T execute(final String header, final String content, final List list) {
        return null;
    }

    @Override
    public T execute(final String header, final String content, final File file) {
        return null;
    }
}
