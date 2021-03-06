package com.github.adamyork.fx5p1d3r.service.output.data;

import java.util.List;

/**
 * Created by Adam York on 3/2/2017.
 * Copyright 2017
 */
public class OutputCsvObject {

    private final List<String[]> objectList;

    private OutputCsvObject(final Builder builder) {
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

        public OutputCsvObject build() {
            return new OutputCsvObject(this);
        }

    }
}
