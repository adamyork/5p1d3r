package com.github.adamyork.fx5p1d3r.application.view.method.choice;

import com.github.adamyork.fx5p1d3r.common.model.URLMethod;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.awt.*;

/**
 * Created by Adam York on 2/22/2017.
 * Copyright 2017
 */
public class URLMethodChoice extends Choice {

    private URLMethod urlMethod;
    private String displayString;

    private URLMethodChoice(final URLMethod urlMethod, final String displayString) {
        this.urlMethod = urlMethod;
        this.displayString = displayString;
    }

    @Override
    public String toString() {
        return displayString;
    }

    public URLMethod getUrlMethod() {
        return urlMethod;
    }

    public static ObservableList<URLMethodChoice> getURLMethodChoices() {
        return FXCollections.observableArrayList(new URLMethodChoice(URLMethod.URL, "URL"),
                new URLMethodChoice(URLMethod.URL_LIST, "URL List"));
    }

}
