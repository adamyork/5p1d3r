package com.github.adamyork.fx5p1d3r.application.view.menu;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.github.adamyork.fx5p1d3r.application.view.query.cell.DomQuery;
import com.github.adamyork.fx5p1d3r.common.model.*;

import java.io.File;
import java.io.IOException;

/**
 * Created by Adam York on 10/12/2017.
 * Copyright 2017
 */
public class FormStateDeserializer extends StdDeserializer<ApplicationFormState> {

    final ApplicationFormState applicationFormState;

    public FormStateDeserializer(final ApplicationFormState applicationFormState) {
        super(ApplicationFormState.class);
        this.applicationFormState = applicationFormState;
    }

    @Override
    public ApplicationFormState deserialize(final JsonParser parser, final DeserializationContext context) throws IOException, JsonProcessingException {
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
        //TODO COMMAND
        if (queryNodes.size() > 0) {
            applicationFormState.getDomQueryObservableList().clear();
            for (int i = 0; i < queryNodes.size(); i++) {
                final JsonNode queryNode = queryNodes.get(i);
                final DomQuery query = new DomQuery.Builder().id(i).query(queryNode.get("query").asText()).build();
                applicationFormState.getDomQueryObservableList().add(query);
            }
        }
        final JsonNode transFormNodes = node.get("resultTransformObservableList");
        //TODO COMMAND
        if (transFormNodes.size() > 0) {
            applicationFormState.getResultTransformObservableList().clear();
            for (int i = 0; i < transFormNodes.size(); i++) {
                final JsonNode transFormNode = transFormNodes.get(i);
                final File file = new File(transFormNode.asText());
                applicationFormState.getResultTransformObservableList().add(file);
            }
        }
        applicationFormState.setOutputFile(node.get("outputFile").asText());
        final OutputFileType outputFileType = OutputFileType.valueOf(node.get("outputFileType").asText());
        final String urlFileListString = node.get("urlListFile").asText();
        //TODO COMMAND
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
