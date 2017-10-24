package com.github.adamyork.fx5p1d3r.common.model;

import java.net.URL;
import java.util.List;

/**
 * Created by Adam York on 10/24/2017.
 * Copyright 2017
 */
public class AllValidUrls {

    private boolean validity;
    private List<URL> urls;

    public AllValidUrls(final boolean validity,
                        final List<URL> urls) {
        this.validity = validity;
        this.urls = urls;
    }

    public boolean isValidity() {
        return validity;
    }

    public List<URL> getUrls() {
        return urls;
    }

}
