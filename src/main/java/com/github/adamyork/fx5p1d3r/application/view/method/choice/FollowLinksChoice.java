package com.github.adamyork.fx5p1d3r.application.view.method.choice;

import com.github.adamyork.fx5p1d3r.common.model.FollowLinksDepth;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.awt.*;

/**
 * Created by Adam York on 9/20/2017.
 * Copyright 2017
 */
public class FollowLinksChoice extends Choice {

    private final FollowLinksDepth followLinksDepth;
    private final int value;

    private FollowLinksChoice(final FollowLinksDepth followLinksDepth, final int value) {
        this.followLinksDepth = followLinksDepth;
        this.value = value;
    }

    @Override
    public String toString() {
        return "x" + Integer.toString(value);
    }

    public FollowLinksDepth getFollowLinksDepth() {
        return followLinksDepth;
    }

    public static ObservableList<FollowLinksChoice> getFollowLinksChoices() {
        return FXCollections.observableArrayList(new FollowLinksChoice(FollowLinksDepth.X1, 1),
                new FollowLinksChoice(FollowLinksDepth.X2, 2),
                new FollowLinksChoice(FollowLinksDepth.X3, 3),
                new FollowLinksChoice(FollowLinksDepth.X4, 4),
                new FollowLinksChoice(FollowLinksDepth.X5, 5),
                new FollowLinksChoice(FollowLinksDepth.X6, 6),
                new FollowLinksChoice(FollowLinksDepth.X7, 7),
                new FollowLinksChoice(FollowLinksDepth.X8, 8),
                new FollowLinksChoice(FollowLinksDepth.X9, 9),
                new FollowLinksChoice(FollowLinksDepth.X10, 10));
    }
}
