package com.github.adamyork.fx5p1d3r.common.model;

/**
 * Created by Adam York on 9/20/2017.
 * Copyright 2017
 */
public enum FollowLinksDepth {

    X1(1),
    X2(2),
    X3(3),
    X4(4),
    X5(5),
    X6(6),
    X7(7),
    X8(8),
    X9(9),
    X10(10);

    private final int followLinksDepthValue;

    FollowLinksDepth(final int followLinkDepthValue) {
        this.followLinksDepthValue = followLinkDepthValue;
    }

    @Override
    public String toString() {
        return Integer.toString(followLinksDepthValue);
    }

    public int getValue() {
        return followLinksDepthValue;
    }
}
