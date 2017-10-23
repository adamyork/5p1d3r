package com.github.adamyork.fx5p1d3r.application.view.method.choice;

import com.github.adamyork.fx5p1d3r.common.model.UrlMethod;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.awt.*;

/**
 * Created by Adam York on 2/22/2017.
 * Copyright 2017
 */
public class UrlMethodChoice extends Choice {

    private UrlMethod urlMethod;
    private String displayString;

    private UrlMethodChoice(final UrlMethod urlMethod, final String displayString) {
        this.urlMethod = urlMethod;
        this.displayString = displayString;
    }

    @Override
    public String toString() {
        return displayString;
    }

    public UrlMethod getUrlMethod() {
        return urlMethod;
    }

    public static ObservableList<UrlMethodChoice> getUrlMethodChoices() {
        return FXCollections.observableArrayList(new UrlMethodChoice(UrlMethod.URL, "URL"),
                new UrlMethodChoice(UrlMethod.URL_LIST, "URL List"));
    }

}
