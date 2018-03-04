package com.github.adamyork.fx5p1d3r.application.view.menu;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.github.adamyork.fx5p1d3r.application.view.menu.command.*;
import com.github.adamyork.fx5p1d3r.common.command.CommandMap;
import com.github.adamyork.fx5p1d3r.common.model.*;

import java.io.IOException;

/**
 * Created by Adam York on 10/12/2017.
 * Copyright 2017
 */
public class FormStateDeserializer extends StdDeserializer<ApplicationFormState> {

    private final ApplicationFormState applicationFormState;
    private final CommandMap<Boolean, FormStateConfigCommand> queryNodesCommandMap;
    private final CommandMap<Boolean, FormStateConfigCommand> transFormNodesCommandMap;
    private final CommandMap<Boolean, FormStateConfigCommand> urlListFileCommandMap;

    public FormStateDeserializer(final ApplicationFormState applicationFormState) {
        super(ApplicationFormState.class);
        this.applicationFormState = applicationFormState;
        queryNodesCommandMap = new CommandMap<>();
        queryNodesCommandMap.add(true, new HasQueryNodesCommand());
        queryNodesCommandMap.add(false, new FormStateConfigNoopCommand());
        transFormNodesCommandMap = new CommandMap<>();
        transFormNodesCommandMap.add(true, new HasTransformNodesCommand());
        transFormNodesCommandMap.add(false, new FormStateConfigNoopCommand());
        urlListFileCommandMap = new CommandMap<>();
        urlListFileCommandMap.add(true, new HasUrlListFilesCommand());
        urlListFileCommandMap.add(false, new FormStateConfigNoopCommand());
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
        queryNodesCommandMap.getCommand(queryNodes.size() > 0).execute(applicationFormState, queryNodes);
        final JsonNode transFormNodes = node.get("resultTransformObservableList");
        transFormNodesCommandMap.getCommand(transFormNodes.size() > 0).execute(applicationFormState, transFormNodes);
        applicationFormState.setOutputFile(node.get("outputFile").asText());
        final OutputFileType outputFileType = OutputFileType.valueOf(node.get("outputFileType").asText());
        final String urlFileListString = node.get("urlListFile").asText();
        urlListFileCommandMap.getCommand(urlFileListString != null).execute(applicationFormState, urlFileListString);
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
