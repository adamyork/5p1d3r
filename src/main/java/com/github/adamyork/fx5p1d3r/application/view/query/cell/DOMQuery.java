package com.github.adamyork.fx5p1d3r.application.view.query.cell;

/**
 * Created by Adam York on 2/24/2017.
 * Copyright 2017
 */
public class DOMQuery {

    private String query;
    private int id;

    private DOMQuery(final Builder builder) {
        query = builder.query;
        id = builder.id;
    }

    public DOMQuery() {}

    public String getQuery() {
        return query;
    }

    void setQuery(final String query) {
        this.query = query;
    }

    public int getId() {
        return id;
    }

    public static class Builder {

        private String query;
        private int id;

        public Builder() {}

        public Builder(final String query, final int id) {
            this.query = query;
            this.id = id;
        }

        public Builder query(final String query) {
            this.query = query;
            return this;
        }

        public Builder id(final int id) {
            this.id = id;
            return this;
        }

        public DOMQuery build() {
            return new DOMQuery(this);
        }

    }

}
