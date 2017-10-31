package com.github.adamyork.fx5p1d3r.common;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.github.adamyork.fx5p1d3r.common.command.CommandMap;
import com.github.adamyork.fx5p1d3r.common.command.ParserCommand;
import com.github.adamyork.fx5p1d3r.common.command.ValidatorCommand;
import com.github.adamyork.fx5p1d3r.common.model.ApplicationFormState;
import com.github.adamyork.fx5p1d3r.common.model.OutputJsonObject;
import com.github.adamyork.fx5p1d3r.common.service.progress.ProgressService;
import com.github.adamyork.fx5p1d3r.common.service.progress.ProgressType;
import org.jooq.lambda.Unchecked;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Adam York on 2/28/2017.
 * Copyright 2017
 */
@Component
public class OutputManager {

    private final CommandMap<Boolean, ParserCommand> createNewFileCommandMap;
    private final CommandMap<Boolean, ValidatorCommand> normalizeUrlCommandMap;
    private final ApplicationFormState applicationFormState;
    private final Validator validator;
    private final ProgressService progressService;

    @Inject
    public OutputManager(@Qualifier("CreateNewFileCommandMap") final CommandMap<Boolean, ParserCommand> createNewFileCommandMap,
                         @Qualifier("NormalizeUrlCommandMap") final CommandMap<Boolean, ValidatorCommand> normalizeUrlCommandMap,
                         final ApplicationFormState applicationFormState,
                         final Validator validator,
                         final ProgressService progressService) {
        this.createNewFileCommandMap = createNewFileCommandMap;
        this.normalizeUrlCommandMap = normalizeUrlCommandMap;
        this.applicationFormState = applicationFormState;
        this.validator = validator;
        this.progressService = progressService;
    }

    public void writeJsonEntry(final Object object) {
        progressService.updateProgress(ProgressType.OUTPUT);
        final String outputFile = applicationFormState.getOutputFile();
        final File destFile = new File(outputFile);
        createNewFileCommandMap.getCommand(destFile.exists()).execute(destFile);
        final Path path = Paths.get(outputFile);
        final byte[] jsonData = Unchecked.function(p -> Files.readAllBytes((Path) p)).apply(path);
        final ObjectMapper mapper = new ObjectMapper();
        //TODO nice to not have to catch here.
        try {
            final OutputJsonObject jsonObject = mapper.readValue(jsonData, OutputJsonObject.class);
            jsonObject.getObjectList().add(object);
            Unchecked.consumer(o -> mapper.writeValue(destFile, jsonObject)).accept(null);
        } catch (IOException e) {
            final OutputJsonObject outputObject = new OutputJsonObject.Builder(new ArrayList<>()).build();
            outputObject.getObjectList().add(object);
            Unchecked.consumer(o -> mapper.writeValue(destFile, outputObject)).accept(null);
        }
    }

    public void writeCsvEntry(final String[] object) {
        progressService.updateProgress(ProgressType.OUTPUT);
        final String outputFile = applicationFormState.getOutputFile();
        final File destFile = new File(outputFile);
        createNewFileCommandMap.getCommand(destFile.exists()).execute(destFile);
        final Path path = Paths.get(outputFile);
        final byte[] csvData = Unchecked.function(p -> Files.readAllBytes((Path) p)).apply(path);
        final CsvMapper mapper = new CsvMapper();
        mapper.enable(CsvParser.Feature.WRAP_AS_ARRAY);
        final MappingIterator<Object> iterator = Unchecked.function(o -> mapper.readerFor(String[].class).readValues(csvData)).apply(null);
        final List<List<String>> values = new ArrayList<>();
        while (iterator.hasNext()) {
            final String[] innerValues = (String[]) iterator.next();
            final List<String> entry = new ArrayList<>();
            entry.addAll(Arrays.asList(innerValues));
            values.add(entry);
        }
        values.add(Arrays.asList(object));
        Unchecked.consumer(o -> mapper.writeValue(destFile, values)).accept(mapper);
    }

    public List<URL> getUrlListFromElements(final Elements elements) {
        return elements.stream()
                .map(element -> {
                    final String href = element.attr("href");
                    return normalizeUrlCommandMap.getCommand(href.contains("http") || href.contains("https"))
                            .execute(element, href);
                })
                .filter(validator::validateUrlString)
                .map(Unchecked.function(URL::new))
                .collect(Collectors.toList());
    }

}
