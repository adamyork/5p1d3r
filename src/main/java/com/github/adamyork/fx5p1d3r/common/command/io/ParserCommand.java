package com.github.adamyork.fx5p1d3r.common.command.io;

import org.jsoup.nodes.Document;

import java.io.File;

/**
 * Created by Adam York on 3/17/2017.
 * Copyright 2017
 */
public interface ParserCommand {

    void execute(final Document document, final String query);

    void execute(final File file);

}
