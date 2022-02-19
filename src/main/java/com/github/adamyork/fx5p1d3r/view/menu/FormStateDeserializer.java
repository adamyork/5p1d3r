package com.github.adamyork.fx5p1d3r.view.menu;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.github.adamyork.fx5p1d3r.ApplicationFormState;
import com.github.adamyork.fx5p1d3r.service.output.data.OutputFileType;
import com.github.adamyork.fx5p1d3r.service.url.data.FollowLinksDepth;
import com.github.adamyork.fx5p1d3r.service.url.data.MultiThreadMax;
import com.github.adamyork.fx5p1d3r.service.url.data.ThrottleMs;
import com.github.adamyork.fx5p1d3r.service.url.data.UrlMethod;
import com.github.adamyork.fx5p1d3r.view.query.cell.DomQuery;

import java.io.File;
import java.io.IOException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by Adam York on 10/12/2017.
 * Copyright 2017
 */
public class FormStateDeserializer extends StdDeserializer<ApplicationFormState> {

    private final ApplicationFormState applicationFormState;

    public FormStateDeserializer(final ApplicationFormState applicationFormState) {
        super(ApplicationFormState.class);
        this.applicationFormState = applicationFormState;
    }

    @Override
    public ApplicationFormState deserialize(final JsonParser parser, final DeserializationContext context) throws IOException {
        final JsonNode node = parser.getCodec().readTree(parser);
        final UrlMethod urlMethod = UrlMethod.valueOf(node.get("urlMethod").asText());
        final String startingUrl = node.get("startingUrl").asText();
        applicationFormState.setThrottling(node.get("throttling").asBoolean());
        applicationFormState.setMultithreading(node.get("multithreading").asBoolean());
        applicationFormState.setFollowLinks(node.get("followLinks").asBoolean());
        final MultiThreadMax multiThreadMax = MultiThreadMax.valueOf(node.get("multiThreadMax").asText());
        final ThrottleMs throttleMs = ThrottleMs.valueOf(node.get("throttleMs").asText());
        final FollowLinksDepth followLinksDepth = FollowLinksDepth.valueOf(node.get("followLinksDepth").asText());
        final String linkFollowPattern = node.get("linkFollowPattern").asText();
        final JsonNode queryNodes = node.get("domQueryObservableList");
        if (queryNodes.size() > 0) {
            applicationFormState.getDomQueryObservableList().clear();
            applicationFormState.getDomQueryObservableList().addAll(
                    IntStream.range(0, queryNodes.size()).mapToObj(i -> {
                        final JsonNode queryNode = queryNodes.get(i);
                        return new DomQuery.Builder().id(i).query(queryNode.get("query").asText()).build();
                    }).toList());
        }
        final JsonNode transFormNodes = node.get("resultTransformObservableList");
        if (transFormNodes.size() > 0) {
            applicationFormState.getResultTransformObservableList().clear();
            applicationFormState.getResultTransformObservableList().addAll(
                    IntStream.range(0, transFormNodes.size()).mapToObj(i -> {
                        final JsonNode transFormNode = transFormNodes.get(i);
                        return new File(transFormNode.asText());
                    }).toList());
        }
        applicationFormState.setOutputFile(node.get("outputFile").asText());
        final OutputFileType outputFileType = OutputFileType.valueOf(node.get("outputFileType").asText());
        final String urlFileListString = node.get("urlListFile").asText();
        if (urlFileListString != null) {
            applicationFormState.setUrlListFile(new File(urlFileListString));
        }
        applicationFormState.setUrlMethod(urlMethod);
        applicationFormState.setStartingUrl(startingUrl);
        applicationFormState.setMultiThreadMax(multiThreadMax);
        applicationFormState.setThrottleMs(throttleMs);
        applicationFormState.setFollowLinksDepth(followLinksDepth);
        applicationFormState.setLinkFollowPattern(linkFollowPattern);
        applicationFormState.setOutputFileType(outputFileType);
        return applicationFormState;
    }
}
