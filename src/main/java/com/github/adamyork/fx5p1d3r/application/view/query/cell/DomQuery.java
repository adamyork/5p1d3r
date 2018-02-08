package com.github.adamyork.fx5p1d3r.application.view.query.cell;

/**
 * Created by Adam York on 2/24/2017.
 * Copyright 2017
 */
public class DomQuery {

    private String query;
    private int id;

    private DomQuery(final Builder builder) {
        query = builder.query;
        id = builder.id;
    }

    public String getQuery() {
        return query;
    }

    void setQuery(final String query) {
        this.query = query;
    }

    @SuppressWarnings("unused")
    public int getId() {
        return id;
    }

    public static class Builder {

        private String query;
        private int id;

        public Builder() {
        }

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

        public DomQuery build() {
            return new DomQuery(this);
        }

    }

}
