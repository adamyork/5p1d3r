package com.github.adamyork.fx5p1d3r.common.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.adamyork.fx5p1d3r.Main;
import com.github.adamyork.fx5p1d3r.application.view.query.cell.DomQuery;
import javafx.collections.ObservableList;
import org.apache.commons.io.FileUtils;
import org.jooq.lambda.Unchecked;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.InputStream;
import java.util.Observable;

/**
 * Created by Adam York on 2/27/2017.
 * Copyright 2017
 */
@Component
public class ApplicationFormState extends Observable {

    private UrlMethod urlMethod;
    private String startingUrl;
    @JsonProperty("followLinks")
    private boolean followLinks;
    @JsonProperty("multithreading")
    private boolean multithreading;
    @JsonProperty("throttling")
    private boolean throttling;
    private MultiThreadMax multiThreadMax;
    private ThrottleMs throttleMs;
    private FollowLinksDepth followLinksDepth;
    private String linkFollowPattern;
    private ObservableList<DomQuery> domQueryObservableList;
    private ObservableList<File> resultTransformObservableList;
    private String outputFile;
    private OutputFileType outputFileType;
    private File urlListFile;
    private File defaultJsonTransform;
    private File defaultCsvTransform;

    public UrlMethod getUrlMethod() {
        return urlMethod;
    }

    public void setUrlMethod(final UrlMethod urlMethod) {
        this.urlMethod = urlMethod;
    }

    public String getStartingUrl() {
        return startingUrl;
    }

    public void setStartingUrl(final String startingUrl) {
        this.startingUrl = startingUrl;
    }

    public boolean followLinks() {
        return followLinks;
    }

    public void setFollowLinks(final boolean followLinks) {
        this.followLinks = followLinks;
    }

    public boolean multithreading() {
        return multithreading;
    }

    public void setMultithreading(final boolean multithreading) {
        this.multithreading = multithreading;
    }

    public boolean throttling() {
        return throttling;
    }

    public void setThrottling(final boolean throttling) {
        this.throttling = throttling;
    }

    public MultiThreadMax getMultiThreadMax() {
        return multiThreadMax;
    }

    public void setMultiThreadMax(final MultiThreadMax multiThreadMax) {
        this.multiThreadMax = multiThreadMax;
    }

    public ThrottleMs getThrottleMs() {
        return throttleMs;
    }

    public void setThrottleMs(final ThrottleMs throttleMs) {
        this.throttleMs = throttleMs;
    }

    public FollowLinksDepth getFollowLinksDepth() {
        return followLinksDepth;
    }

    public void setFollowLinksDepth(final FollowLinksDepth followLinksDepth) {
        this.followLinksDepth = followLinksDepth;
    }

    public String getLinkFollowPattern() {
        return linkFollowPattern;
    }

    public void setLinkFollowPattern(final String linkFollowPattern) {
        this.linkFollowPattern = linkFollowPattern;
    }

    public ObservableList<DomQuery> getDomQueryObservableList() {
        return domQueryObservableList;
    }

    public void setDomQueryObservableList(final ObservableList<DomQuery> domQueryObservableList) {
        this.domQueryObservableList = domQueryObservableList;
    }

    public ObservableList<File> getResultTransformObservableList() {
        return resultTransformObservableList;
    }

    public void setResultTransformObservableList(final ObservableList<File> resultTransformObservableList) {
        this.resultTransformObservableList = resultTransformObservableList;
    }

    public String getOutputFile() {
        return outputFile;
    }

    public void setOutputFile(final String outputFile) {
        this.outputFile = outputFile;
    }

    public OutputFileType getOutputFileType() {
        return outputFileType;
    }

    public void setOutputFileType(final OutputFileType outputFileType) {
        this.outputFileType = outputFileType;
    }

    public File getUrlListFile() {
        return urlListFile;
    }

    public void setUrlListFile(final File urlListFile) {
        this.urlListFile = urlListFile;
    }

    public File getDefaultJsonTransform() {
        //TODO COMMAND
        if (defaultJsonTransform == null) {
            final InputStream stream = Main.class.getClassLoader().getResourceAsStream("basicJsonTransform.groovy");
            final File basicJsonTransform = new File("basicJsonTransform");
            basicJsonTransform.deleteOnExit();
            Unchecked.consumer(consumer -> FileUtils.copyInputStreamToFile(stream, basicJsonTransform)).accept(null);
            defaultJsonTransform = basicJsonTransform;
        }
        return defaultJsonTransform;
    }

    public void setDefaultJsonTransform(final File defaultJsonTransform) {
        this.defaultJsonTransform = defaultJsonTransform;
    }

    public File getDefaultCsvTransform() {
        //TODO COMMAND
        if (defaultCsvTransform == null) {
            final InputStream stream = Main.class.getClassLoader().getResourceAsStream(("basicCsvTransform.groovy"));
            final File basicCsvTransform = new File("basicCsvTransform");
            basicCsvTransform.deleteOnExit();
            Unchecked.consumer(consumer -> FileUtils.copyInputStreamToFile(stream, basicCsvTransform)).accept(null);
            defaultCsvTransform = basicCsvTransform;
        }
        return defaultCsvTransform;
    }

    public void setDefaultCsvTransform(final File defaultCsvTransform) {
        this.defaultCsvTransform = defaultCsvTransform;
    }

    public void notifyChanged() {
        setChanged();
        notifyObservers();
    }

    public void clearNotify() {
        clearChanged();
    }

}
