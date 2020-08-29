package com.github.adamyork.fx5p1d3r.application.view.query.cell;

import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Adam York on 2/25/2017.
 * Copyright 2017
 */
public class DomQueryListCellController implements Initializable {

    @FXML
    private TextField domQueryListCellTextField;

    @FXML
    private ToggleButton domQueryListCellEditButton;

    private DomQuery domQuery;

    void setDomQuery(final DomQuery domQuery) {
        this.domQuery = domQuery;
        domQueryListCellTextField.setText(domQuery.getQuery());
    }

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        domQueryListCellTextField.textProperty().addListener(this::handleTextfieldChanged);
        domQueryListCellTextField.setEditable(false);
        domQueryListCellTextField.setMouseTransparent(true);
        domQueryListCellTextField.setFocusTraversable(false);
        domQueryListCellEditButton.setOnAction(this::handleEdit);
    }

    private void handleTextfieldChanged(final ObservableValue<? extends String> observableValue,
                                        final String oldValue, final String newValue) {
        domQuery.setQuery(newValue);
    }

    private void handleEdit(final ActionEvent actionEvent) {
        domQueryListCellTextField.setEditable(domQueryListCellEditButton.isSelected());
        domQueryListCellTextField.setMouseTransparent(!domQueryListCellEditButton.isSelected());
        domQueryListCellTextField.setFocusTraversable(domQueryListCellEditButton.isSelected());
    }

}
