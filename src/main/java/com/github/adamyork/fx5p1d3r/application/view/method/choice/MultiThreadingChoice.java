package com.github.adamyork.fx5p1d3r.application.view.method.choice;

import com.github.adamyork.fx5p1d3r.common.model.MultiThreadMax;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.awt.*;

/**
 * Created by Adam York on 2/23/2017.
 * Copyright 2017
 */
public class MultiThreadingChoice extends Choice {

    private MultiThreadMax multiThreadMax;
    private int value;

    private MultiThreadingChoice(final MultiThreadMax multiThreadMax, final int value) {
        this.multiThreadMax = multiThreadMax;
        this.value = value;
    }

    @Override
    public String toString() {
        return Integer.toString(value);
    }

    public MultiThreadMax getMultiThreadMax() {
        return multiThreadMax;
    }

    public static ObservableList<MultiThreadingChoice> getMultiThreadingChoices() {
        return FXCollections.observableArrayList(new MultiThreadingChoice(MultiThreadMax.FIVE, 5),
                new MultiThreadingChoice(MultiThreadMax.TEN, 10),
                new MultiThreadingChoice(MultiThreadMax.FIFTY, 50),
                new MultiThreadingChoice(MultiThreadMax.HUNDRED, 100),
                new MultiThreadingChoice(MultiThreadMax.TWO_HUNDRED, 200),
                new MultiThreadingChoice(MultiThreadMax.FIVE_HUNDRED, 500),
                new MultiThreadingChoice(MultiThreadMax.THOUSAND, 1000));
    }

}
