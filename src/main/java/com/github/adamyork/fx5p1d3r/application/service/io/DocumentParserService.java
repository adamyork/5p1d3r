package com.github.adamyork.fx5p1d3r.application.service.io;

import org.jsoup.nodes.Document;

/**
 * Created by Adam York on 8/28/2020.
 * Copyright 2020
 */
public interface DocumentParserService {

    void parse(final Document document, final String query);

}
