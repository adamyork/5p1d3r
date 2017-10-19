package com.github.adamyork.fx5p1d3r.common;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.github.adamyork.fx5p1d3r.common.model.ApplicationFormState;
import com.github.adamyork.fx5p1d3r.common.model.OutputJSONObject;
import com.github.adamyork.fx5p1d3r.common.service.progress.ProgressService;
import com.github.adamyork.fx5p1d3r.common.service.progress.ProgressType;
import org.jooq.lambda.Unchecked;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.net.URI;
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

    private final ApplicationFormState applicationFormState;
    private final Validator validator;
    private final ProgressService progressService;

    @Autowired
    public OutputManager(final ApplicationFormState applicationFormState,
                         final Validator validator,
                         final ProgressService progressService) {
        this.applicationFormState = applicationFormState;
        this.validator = validator;
        this.progressService = progressService;
    }

    public void writeJSONEntry(final Object object) {
        progressService.updateProgress(ProgressType.OUTPUT);
        final String outputFile = applicationFormState.getOutputFile();
        final File destFile = new File(outputFile);
        //TODO COMMAND
        if (!destFile.exists()) {
            Unchecked.function(file -> ((File) file).createNewFile()).apply(destFile);
        }
        final Path path = Paths.get(outputFile);
        final byte[] jsonData = Unchecked.function(p -> Files.readAllBytes((Path) p)).apply(path);
        final ObjectMapper mapper = new ObjectMapper();
        try {
            final OutputJSONObject jsonObject = mapper.readValue(jsonData, OutputJSONObject.class);
            jsonObject.getObjectList().add(object);
            Unchecked.consumer(o -> mapper.writeValue(destFile, jsonObject)).accept(null);
        } catch (IOException e) {
            final OutputJSONObject outputObject = new OutputJSONObject.Builder(new ArrayList<>()).build();
            outputObject.getObjectList().add(object);
            Unchecked.consumer(o -> mapper.writeValue(destFile, outputObject)).accept(null);
        }
    }

    public void writeCSVEntry(final String[] object) {
        progressService.updateProgress(ProgressType.OUTPUT);
        final String outputFile = applicationFormState.getOutputFile();
        final File destFile = new File(outputFile);
        //TODO COMMAND
        if (!destFile.exists()) {
            Unchecked.function(file -> ((File) file).createNewFile()).apply(destFile);
        }
        final Path path = Paths.get(outputFile);
        final byte[] csvData = Unchecked.function(p -> Files.readAllBytes((Path) p)).apply(path);
        final CsvMapper mapper = new CsvMapper();
        mapper.enable(CsvParser.Feature.WRAP_AS_ARRAY);
        final MappingIterator<Object> iterator = Unchecked.function(o -> mapper.readerFor(String[].class).readValues(csvData)).apply(null);
        final List<List<String>> values = new ArrayList<>();
        while (iterator.hasNext()) {
            final String[] valuez = (String[]) iterator.next();
            final List<String> entry = new ArrayList<>();
            entry.addAll(Arrays.asList(valuez));
            values.add(entry);
        }
        values.add(Arrays.asList(object));
        Unchecked.consumer(o -> mapper.writeValue(destFile, values)).accept(mapper);
    }

    public List<URL> getURLListFromElements(final Elements elements) {
        return elements.stream()
                .map(element -> {
                    final String href = element.attr("href");
                    //TODO COMMAND
                    if (!href.contains("http") && !href.contains("https")) {
                        final String baseUri = element.baseUri();
                        final URI uri = Unchecked.function(t -> new URI(baseUri)).apply(null);
                        final String baseUriTrimmed = uri.getScheme() + "://" + uri.getHost();
                        return baseUriTrimmed + href;
                    }
                    return href;
                })
                .filter(validator::validateURLString)
                .map(Unchecked.function(URL::new))
                .collect(Collectors.toList());
    }

}
