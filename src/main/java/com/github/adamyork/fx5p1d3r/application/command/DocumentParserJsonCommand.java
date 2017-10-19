package com.github.adamyork.fx5p1d3r.application.command;

import com.github.adamyork.fx5p1d3r.common.command.CommandMap;
import com.github.adamyork.fx5p1d3r.common.command.OutputCommand;
import com.github.adamyork.fx5p1d3r.common.command.ParserCommand;
import com.github.adamyork.fx5p1d3r.common.model.ApplicationFormState;
import com.github.adamyork.fx5p1d3r.common.model.OutputFileType;
import com.github.adamyork.fx5p1d3r.common.model.OutputJSONObject;
import com.github.adamyork.fx5p1d3r.common.service.AlertService;
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
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Adam York on 2/28/2017.
 * Copyright 2017
 */
@Component
public class DocumentParserJsonCommand implements ParserCommand {

    private final ApplicationFormState applicationFormState;
    private final ProgressService progressService;
    private final AlertService alertService;
    private final CommandMap<OutputFileType, OutputCommand> outputCommandMap;

    @Autowired
    public DocumentParserJsonCommand(final ApplicationFormState applicationFormState,
                                     final ProgressService progressService,
                                     final AlertService alertService,
                                     @Qualifier("OutputCommandMap") final CommandMap<OutputFileType, OutputCommand> outputCommandMap) {
        this.applicationFormState = applicationFormState;
        this.progressService = progressService;
        this.outputCommandMap = outputCommandMap;
        this.alertService = alertService;
    }

    @Override
    public Elements execute(final Document document, final String query) {
        progressService.updateProgress(ProgressType.SELECTOR);
        final Elements elements = document.select(query);
        final ObservableList<File> resultTransformObservableList = applicationFormState.getResultTransformObservableList();
        final List<Object> objectList = new ArrayList<>();
        progressService.updateProgress(ProgressType.TRANSFORM);
        //TODO COMMAND
        if (elements.size() == 0) {
            alertService.warn("No Elements.", "One or more queries returned no elements. Output may be empty");
        }
        elements.forEach(element -> resultTransformObservableList.forEach(file -> {
            final Binding binding = new Binding();
            final String script = Unchecked.function(o -> FileUtils.readFileToString(file)).apply(null);
            final GroovyShell shell = new GroovyShell(binding);
            binding.setProperty("document", document);
            binding.setProperty("element", element);
            final Object obj = shell.evaluate(script);
            objectList.add(obj);
        }));
        final OutputJSONObject outputJSONObject = new OutputJSONObject.Builder().objectList(objectList).build();
        outputCommandMap.getCommand(applicationFormState.getOutputFileType()).execute(outputJSONObject);
        return elements;
    }
}
