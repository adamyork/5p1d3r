package com.github.adamyork.fx5p1d3r.service.parse;

import com.github.adamyork.fx5p1d3r.service.progress.AlertService;
import com.github.adamyork.fx5p1d3r.service.progress.ApplicationProgressService;
import com.github.adamyork.fx5p1d3r.service.progress.ProgressType;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.context.MessageSource;

import java.util.Locale;

public class JSoupDocumentParser implements DocumentParserService {

    private final ApplicationProgressService progressService;
    private final MessageSource messageSource;
    private final AlertService alertService;

    public JSoupDocumentParser(final ApplicationProgressService progressService,
                               final MessageSource messageSource,
                               final AlertService alertService) {
        this.progressService = progressService;
        this.messageSource = messageSource;
        this.alertService = alertService;
    }

    @Override
    public Elements parse(final Document document, final String query) {
        progressService.updateProgress(ProgressType.SELECTOR);
        final Elements elements = document.select(query);
        if (elements.size() == 0) {
            alertService.warn(messageSource.getMessage("alert.no.elements.header", null, Locale.getDefault()),
                    messageSource.getMessage("alert.no.elements.content", null, Locale.getDefault()));
        }
        return elements;
    }

}
