package com.github.adamyork.fx5p1d3r.common.model;

/**
 * Created by Adam York on 9/20/2017.
 * Copyright 2017
 */
public enum ThrottleMs {

    ZERO(0),
    TWO_HUNDRED(200),
    FIVE_HUNDRED(500),
    THOUSAND(1000),
    TWO_THOUSAND(2000),
    FIVE_THOUSAND(5000),
    TEN_THOUSAND(10000);

    private final int throttleMsValue;

    ThrottleMs(final int throttleMsValue) {
        this.throttleMsValue = throttleMsValue;
    }

    @Override
    public String toString() {
        return Integer.toString(throttleMsValue);
    }

    public int getValue() {
        return throttleMsValue;
    }
}
