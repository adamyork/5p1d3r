package com.github.adamyork.fx5p1d3r.common.model;

/**
 * Created by Adam York on 3/1/2017.
 * Copyright 2017
 */
public enum OutputFileType {

    JSON(".json"),
    CSV(".csv");

    private final String outputFileType;

    OutputFileType(final String outputFileType) {
        this.outputFileType = outputFileType;
    }

    @Override
    public String toString() {
        return outputFileType;
    }
}
