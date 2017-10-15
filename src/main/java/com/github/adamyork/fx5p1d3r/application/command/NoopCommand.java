package com.github.adamyork.fx5p1d3r.application.command;

import com.github.adamyork.fx5p1d3r.common.command.ApplicationCommand;
import com.github.adamyork.fx5p1d3r.common.command.NullSafeCommand;
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
public class NoopCommand implements NullSafeCommand<Object>, ApplicationCommand {

    @Override
    public void execute(final Object listView, final int index) {}

    @Override
    public void execute(final Object listView, final File file) {}

    @Override
    public void execute() {}

    @Override
    public void execute(final List<URL> urls) {}

    @Override
    public void execute(final List<URL> urls, final ExecutorService executorService) {}

}
