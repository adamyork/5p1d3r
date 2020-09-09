package com.github.adamyork.fx5p1d3r.common.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.adamyork.fx5p1d3r.LogDirectoryHelper;
import com.github.adamyork.fx5p1d3r.Main;
import com.github.adamyork.fx5p1d3r.application.view.query.cell.DomQuery;
import javafx.collections.ObservableList;
import org.apache.commons.io.FileUtils;
import org.jooq.lambda.Unchecked;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Observable;

/**
 * Created by Adam York on 2/27/2017.
 * Copyright 2017
 */
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
        final Path tempDirectoryPath = Path.of(LogDirectoryHelper.getTempDirectoryPath() + File.separator + "basicJsonTransform");
        final InputStream stream = Main.class.getClassLoader().getResourceAsStream("basicJsonTransform.groovy");
        final File basicJsonTransform = new File(tempDirectoryPath.toUri());
        basicJsonTransform.deleteOnExit();
        Unchecked.consumer(consumer -> {
            assert stream != null;
            FileUtils.copyInputStreamToFile(stream, basicJsonTransform);
        }).accept(null);
        return basicJsonTransform;
    }

    public File getDefaultCsvTransform() {
        final Path tempDirectoryPath = Path.of(LogDirectoryHelper.getTempDirectoryPath() + File.separator + "basicCsvTransform");
        final InputStream stream = Main.class.getClassLoader().getResourceAsStream(("basicCsvTransform.groovy"));
        final File basicCsvTransform = new File(tempDirectoryPath.toUri());
        basicCsvTransform.deleteOnExit();
        Unchecked.consumer(consumer -> {
            assert stream != null;
            FileUtils.copyInputStreamToFile(stream, basicCsvTransform);
        }).accept(null);
        return basicCsvTransform;
    }

    public void notifyChanged() {
        setChanged();
        notifyObservers();
    }

    public void clearNotify() {
        clearChanged();
    }

}
