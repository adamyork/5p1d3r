package com.github.adamyork.fx5p1d3r.service.url.data;

/**
 * Created by Adam York on 2/23/2017.
 * Copyright 2017
 */
public enum MultiThreadMax {

    FIVE(5),
    TEN(10),
    FIFTY(50),
    HUNDRED(100),
    TWO_HUNDRED(200),
    FIVE_HUNDRED(500),
    THOUSAND(1000);

    private final int multiThreadMax;

    MultiThreadMax(final int multiThreadMax) {
        this.multiThreadMax = multiThreadMax;
    }

    @Override
    public String toString() {
        return Integer.toString(multiThreadMax);
    }

}
