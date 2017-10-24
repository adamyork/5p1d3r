package com.github.adamyork.fx5p1d3r.application.command;

import com.github.adamyork.fx5p1d3r.common.command.AlertCommand;
import com.github.adamyork.fx5p1d3r.common.command.CommandMap;
import com.github.adamyork.fx5p1d3r.common.command.OutputCommand;
import com.github.adamyork.fx5p1d3r.common.command.ParserCommand;
import com.github.adamyork.fx5p1d3r.common.model.ApplicationFormState;
import com.github.adamyork.fx5p1d3r.common.model.OutputFileType;
import com.github.adamyork.fx5p1d3r.common.model.OutputJsonObject;
import com.github.adamyork.fx5p1d3r.common.service.progress.ProgressService;
import com.github.adamyork.fx5p1d3r.common.service.progress.ProgressType;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import javafx.collections.ObservableList;
import org.apache.commons.io.FileUtils;
import org.jooq.lambda.Unchecked;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Adam York on 2/28/2017.
 * Copyright 2017
 */
@Component
public class DocumentParserJsonCommand implements ParserCommand {

    private final ApplicationFormState applicationFormState;
    private final ProgressService progressService;
    private final CommandMap<OutputFileType, OutputCommand> outputCommandMap;
    private final CommandMap<Boolean, AlertCommand> warnCommandMap;
    private final MessageSource messageSource;

    @Autowired
    public DocumentParserJsonCommand(final ApplicationFormState applicationFormState,
                                     final ProgressService progressService,
                                     final MessageSource messageSource,
                                     @Qualifier("OutputCommandMap") final CommandMap<OutputFileType, OutputCommand> outputCommandMap,
                                     @Qualifier("WarnCommandMap") final CommandMap<Boolean, AlertCommand> warnCommandMap) {
        this.applicationFormState = applicationFormState;
        this.progressService = progressService;
        this.outputCommandMap = outputCommandMap;
        this.warnCommandMap = warnCommandMap;
        this.messageSource = messageSource;
    }

    @Override
    public Elements execute(final Document document, final String query) {
        progressService.updateProgress(ProgressType.SELECTOR);
        final Elements elements = document.select(query);
        final ObservableList<File> resultTransformObservableList = applicationFormState.getResultTransformObservableList();
        final List<Object> objectList = new ArrayList<>();
        progressService.updateProgress(ProgressType.TRANSFORM);
        warnCommandMap.getCommand(elements.size() == 0)
                .execute(messageSource.getMessage("alert.no.elements.header", null, Locale.getDefault()),
                        messageSource.getMessage("alert.no.elements.content", null, Locale.getDefault()));
        elements.forEach(element -> resultTransformObservableList.forEach(file -> {
            final Binding binding = new Binding();
            final String script = Unchecked.function(o -> FileUtils.readFileToString(file)).apply(null);
            final GroovyShell shell = new GroovyShell(binding);
            binding.setProperty("document", document);
            binding.setProperty("element", element);
            final Object obj = shell.evaluate(script);
            objectList.add(obj);
        }));
        final OutputJsonObject outputJsonObject = new OutputJsonObject.Builder().objectList(objectList).build();
        outputCommandMap.getCommand(applicationFormState.getOutputFileType()).execute(outputJsonObject);
        return elements;
    }
}
