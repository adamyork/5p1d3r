package com.github.adamyork.fx5p1d3r.common.command;

import org.jsoup.nodes.Document;

import java.util.List;

/**
 * Created by Adam York on 10/19/2017.
 * Copyright 2017
 */
public interface DocumentRetrieveHandler {

    void onDocumentsRetrieved(final List<Document> documents);

}
