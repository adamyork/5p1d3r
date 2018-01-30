package com.github.adamyork.fx5p1d3r.application.view.menu.command;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.adamyork.fx5p1d3r.application.view.query.cell.DomQuery;
import com.github.adamyork.fx5p1d3r.common.model.ApplicationFormState;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class HasQueryNodesCommand implements FormStateConfigCommand {
    @Override
    public void execute(final ApplicationFormState applicationFormState, final JsonNode jsonNode) {
        applicationFormState.getDomQueryObservableList().clear();
        applicationFormState.getDomQueryObservableList().addAll(
                IntStream.range(0, jsonNode.size()).mapToObj(i -> {
                    final JsonNode node = jsonNode.get(i);
                    return new DomQuery.Builder().id(i).query(node.get("query").asText()).build();
                }).collect(Collectors.toList()));
    }

    @Override
    public void execute(final ApplicationFormState applicationFormState, final String urlFileListString) {

    }
}
