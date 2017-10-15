package com.github.adamyork.fx5p1d3r.common.model;

import com.github.adamyork.fx5p1d3r.Main;
import com.github.adamyork.fx5p1d3r.application.view.query.cell.DOMQuery;
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

    private URLMethod urlMethod;
    private String startingURL;
    private boolean followLinks;
    private boolean multithreading;
    private boolean throttling;
    private MultiThreadMax multiThreadMax;
    private ThrottleMs throttleMs;
    private FollowLinksDepth followLinksDepth;
    private String linkFollowPattern;
    private ObservableList<DOMQuery> domQueryObservableList;
    private ObservableList<File> resultTransformObservableList;
    private String outputFile;
    private OutputFileType outputFileType;
    private File urlListFile;
    private File defaultJSONTransform;
    private File defaultCSVTransform;

    public URLMethod getUrlMethod() {
        return urlMethod;
    }

    public void setUrlMethod(final URLMethod urlMethod) {
        this.urlMethod = urlMethod;
    }

    public String getStartingURL() {
        return startingURL;
    }

    public void setStartingURL(final String startingURL) {
        this.startingURL = startingURL;
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

    public ObservableList<DOMQuery> getDomQueryObservableList() {
        return domQueryObservableList;
    }

    public void setDomQueryObservableList(final ObservableList<DOMQuery> domQueryObservableList) {
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

    public File getDefaultJSONTransform() {
        if (defaultJSONTransform == null) {
            final InputStream stream = Main.class.getClassLoader().getResourceAsStream("basicJSONTransform.groovy");
            final File basicJsonTransform = new File("basicJSONTransform");
            basicJsonTransform.deleteOnExit();
            Unchecked.consumer(consumer -> FileUtils.copyInputStreamToFile(stream, basicJsonTransform)).accept(null);
            defaultJSONTransform = basicJsonTransform;
        }
        return defaultJSONTransform;
    }

    public void setDefaultJSONTransform(final File defaultJSONTransform) {
        this.defaultJSONTransform = defaultJSONTransform;
    }

    public File getDefaultCSVTransform() {
        if (defaultCSVTransform == null) {
            final InputStream stream = Main.class.getClassLoader().getResourceAsStream(("basicCSVTransform.groovy"));
            final File basicCsvTransform = new File("basicCSVTransform");
            basicCsvTransform.deleteOnExit();
            Unchecked.consumer(consumer -> FileUtils.copyInputStreamToFile(stream, basicCsvTransform)).accept(null);
            defaultCSVTransform = basicCsvTransform;
        }
        return defaultCSVTransform;
    }

    public void setDefaultCSVTransform(final File defaultCSVTransform) {
        this.defaultCSVTransform = defaultCSVTransform;
    }

    public void notifyChanged() {
        setChanged();
        notifyObservers();
    }

    public void clearNotify() {
        clearChanged();
    }

}
