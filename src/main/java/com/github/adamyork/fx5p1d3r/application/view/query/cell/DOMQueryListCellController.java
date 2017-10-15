package com.github.adamyork.fx5p1d3r.application.view.query.cell;

import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Adam York on 2/25/2017.
 * Copyright 2017
 */
public class DOMQueryListCellController implements Initializable {

    @FXML
    private TextField domQueryListCellTextField;

    private DOMQuery domQuery;

    void setDOMQuery(final DOMQuery domQuery) {
        this.domQuery = domQuery;
        domQueryListCellTextField.setText(domQuery.getQuery());
    }

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        domQueryListCellTextField.textProperty().addListener(this::handleTextfieldChanged);
    }

    @SuppressWarnings("unused")
    private void handleTextfieldChanged(final ObservableValue<? extends String> observableValue,
                                        final String oldValue, final String newValue) {
        domQuery.setQuery(newValue);
    }

}
