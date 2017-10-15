package com.github.adamyork.fx5p1d3r.common.model;

import java.util.List;

/**
 * Created by Adam York on 3/2/2017.
 * Copyright 2017
 */
public class OutputCSVObject {

    private List<String[]> objectList;

    private OutputCSVObject(final Builder builder) {
        this.objectList = builder.objectList;
    }

    public List<String[]> getObjectList() {
        return objectList;
    }

    public static class Builder {

        private List<String[]> objectList;

        public Builder() {}

        public Builder objectList(final List<String[]> objectList) {
            this.objectList = objectList;
            return this;
        }

        public OutputCSVObject build() {
            return new OutputCSVObject(this);
        }

    }
}
