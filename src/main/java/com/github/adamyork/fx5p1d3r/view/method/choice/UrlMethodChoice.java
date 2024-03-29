package com.github.adamyork.fx5p1d3r.view.method.choice;

import com.github.adamyork.fx5p1d3r.service.url.data.UrlMethod;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.awt.*;

/**
 * Created by Adam York on 2/22/2017.
 * Copyright 2017
 */
public class UrlMethodChoice extends Choice {

    private final String displayString;
    private final UrlMethod urlMethod;

    private UrlMethodChoice(final UrlMethod urlMethod, final String displayString) {
        this.urlMethod = urlMethod;
        this.displayString = displayString;
    }

    public static ObservableList<UrlMethodChoice> getUrlMethodChoices() {
        return FXCollections.observableArrayList(new UrlMethodChoice(UrlMethod.URL, "URL"),
                new UrlMethodChoice(UrlMethod.URL_LIST, "URL List"));
    }

    @Override
    public String toString() {
        return displayString;
    }

    public UrlMethod getUrlMethod() {
        return urlMethod;
    }

}
