package com.github.adamyork.fx5p1d3r.service.parse;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public interface DocumentParserService {

    Elements parse(final Document document, final String query);

}
