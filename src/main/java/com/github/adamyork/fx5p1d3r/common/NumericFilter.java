package com.github.adamyork.fx5p1d3r.common;

import javafx.scene.control.TextFormatter;

import java.util.function.UnaryOperator;

/**
 * Created by Adam York on 3/28/2017.
 * Copyright 2017
 */

public class NumericFilter {

    private TextFormatter<String> formatter;

    public NumericFilter() {
        final NumericOnlyOperator operator = new NumericOnlyOperator();
        formatter = new TextFormatter<>(operator);
    }

    public TextFormatter<String> getFormatter() {
        return formatter;
    }

    class NumericOnlyOperator implements UnaryOperator<TextFormatter.Change> {

        @Override
        public TextFormatter.Change apply(final TextFormatter.Change change) {
            final String input = change.getText();
            //TODO COMMAND
            if (input.matches("[0-9]*")) {
                return change;
            }
            return null;
        }
    }
}

