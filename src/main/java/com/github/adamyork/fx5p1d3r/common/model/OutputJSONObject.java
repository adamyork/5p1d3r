package com.github.adamyork.fx5p1d3r.common.model;

import java.util.List;

/**
 * Created by Adam York on 3/2/2017.
 * Copyright 2017
 */
public class OutputJSONObject {

    private List<Object> objectList;

    public OutputJSONObject() {}

    private OutputJSONObject(final Builder builder) {
        this.objectList = builder.objectList;
    }

    public List<Object> getObjectList() {
        return objectList;
    }

    public static class Builder {

        private List<Object> objectList;

        public Builder() {
        }

        public Builder(final List<Object> objectList) {
            this.objectList = objectList;
        }

        public Builder objectList(final List<Object> objectList) {
            this.objectList = objectList;
            return this;
        }

        public OutputJSONObject build() {
            return new OutputJSONObject(this);
        }

    }
}
