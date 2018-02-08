package com.github.adamyork.fx5p1d3r.application.view.menu.command;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.adamyork.fx5p1d3r.common.model.ApplicationFormState;

import java.io.File;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by Adam York on 1/2/2018.
 * Copyright 2018
 */
public class HasTransformNodesCommand implements FormStateConfigCommand {
    @Override
    public void execute(final ApplicationFormState applicationFormState, final JsonNode jsonNode) {
        applicationFormState.getResultTransformObservableList().clear();
        applicationFormState.getResultTransformObservableList().addAll(
                IntStream.range(0, jsonNode.size()).mapToObj(i -> {
                    final JsonNode transFormNode = jsonNode.get(i);
                    return new File(transFormNode.asText());
                }).collect(Collectors.toList()));
    }

    @Override
    public void execute(final ApplicationFormState applicationFormState, final String urlFileListString) {

    }
}
