package com.github.adamyork.fx5p1d3r.application.service.thread;

import com.github.adamyork.fx5p1d3r.application.service.io.DocumentParserService;
import com.github.adamyork.fx5p1d3r.application.view.query.cell.DomQuery;
import com.github.adamyork.fx5p1d3r.common.model.ApplicationFormState;
import com.github.adamyork.fx5p1d3r.common.model.OutputFileType;
import javafx.collections.ObservableList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.nodes.Document;

/**
 * Created by Adam York on 8/28/2020.
 * Copyright 2020
 */
public class BaseObservableProcessor {

    private static final Logger logger = LogManager.getLogger(BaseObservableProcessor.class);

    protected ObservableList<DomQuery> getDomQueryList(final ApplicationFormState applicationFormState) {
        return applicationFormState.getDomQueryObservableList();
    }

    protected void parseQueries(final ObservableList<DomQuery> domQueryObservableList,
                                final ApplicationFormState applicationFormState,
                                final DocumentParserService jsonDocumentParser,
                                final DocumentParserService csvDocumentParser,
                                final Document document) {
        domQueryObservableList.forEach(domQuery -> {
            final String domQueryString = domQuery.getQuery();
            logger.debug("Parsing document for " + domQueryString);
            if (applicationFormState.getOutputFileType().equals(OutputFileType.JSON)) {
                jsonDocumentParser.parse(document, domQueryString);
            } else {
                csvDocumentParser.parse(document, domQueryString);
            }
        });
    }

}
