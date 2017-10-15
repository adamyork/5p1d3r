package com.github.adamyork.fx5p1d3r.common.command;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/**
 * Created by Adam York on 3/17/2017.
 * Copyright 2017
 */
public interface ParserCommand {

    Elements execute(final Document document, final String query);

}
