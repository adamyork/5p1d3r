package com.github.adamyork.fx5p1d3r.application.view.query.cell;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ListCell;
import org.jooq.lambda.Unchecked;

/**
 * Created by Adam York on 2/24/2017.
 * Copyright 2017
 */
public class DomQueryListCell extends ListCell<DomQuery> {

    @Override
    protected void updateItem(final DomQuery item, final boolean empty) {
        super.updateItem(item, empty);
        //TODO COMMAND
        if (!empty) {
            //TODO COMMAND
            if (item != null) {
                final FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("dom-query-list-cell.fxml"));
                final Parent parent = (Parent) Unchecked.function(o -> fxmlLoader.load()).apply(null);
                final DomQueryListCellController casted = fxmlLoader.getController();
                casted.setDomQuery(item);
                setGraphic(parent);
            }
        } else {
            setText(null);
            setGraphic(null);
        }
    }
}
