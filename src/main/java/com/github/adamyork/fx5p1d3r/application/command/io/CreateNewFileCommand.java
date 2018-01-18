package com.github.adamyork.fx5p1d3r.application.command.io;

import com.github.adamyork.fx5p1d3r.common.command.io.ParserCommand;
import org.jooq.lambda.Unchecked;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * Created by Adam York on 10/31/2017.
 * Copyright 2017
 */
@Component
public class CreateNewFileCommand implements ParserCommand {

    @Override
    public void execute(final Document document, final String query) {

    }

    @Override
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void execute(final File file) {
        Unchecked.consumer(f -> ((File) f).createNewFile()).accept(file);
    }
}
