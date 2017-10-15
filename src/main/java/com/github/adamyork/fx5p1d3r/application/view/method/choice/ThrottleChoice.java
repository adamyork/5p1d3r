package com.github.adamyork.fx5p1d3r.application.view.method.choice;

import com.github.adamyork.fx5p1d3r.common.model.ThrottleMs;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.awt.*;

/**
 * Created by Adam York on 9/20/2017.
 * Copyright 2017
 */
public class ThrottleChoice extends Choice {

    private ThrottleMs throttleMs;
    private int value;

    private ThrottleChoice(final ThrottleMs throttleMs, final int value) {
        this.throttleMs = throttleMs;
        this.value = value;
    }

    @Override
    public String toString() {
        return Integer.toString(value) + " ms";
    }

    public ThrottleMs getThrottleMs() {
        return throttleMs;
    }

    public static ObservableList<ThrottleChoice> getThrottleChoices() {
        return FXCollections.observableArrayList(new ThrottleChoice(ThrottleMs.ZERO, 0),
                new ThrottleChoice(ThrottleMs.TWO_HUNDRED, 200),
                new ThrottleChoice(ThrottleMs.FIVE_HUNDRED, 500),
                new ThrottleChoice(ThrottleMs.THOUSAND, 1000),
                new ThrottleChoice(ThrottleMs.TWO_THOUSAND, 2000),
                new ThrottleChoice(ThrottleMs.FIVE_THOUSAND, 5000),
                new ThrottleChoice(ThrottleMs.TEN_THOUSAND, 10000));
    }

}
