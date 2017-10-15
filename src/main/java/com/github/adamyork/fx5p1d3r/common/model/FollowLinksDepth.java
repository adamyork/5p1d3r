package com.github.adamyork.fx5p1d3r.common.model;

/**
 * Created by Adam York on 9/20/2017.
 * Copyright 2017
 */
public enum FollowLinksDepth {

    X0(0),
    X1(1),
    X2(2),
    X5(5),
    X10(10),
    X50(50),
    X100(100);

    private int followLinksDepthValue;

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
