package com.github.adamyork.fx5p1d3r.view.query.cell;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ListCell;
import org.jooq.lambda.Unchecked;
import org.springframework.context.MessageSource;

/**
 * Created by Adam York on 2/24/2017.
 * Copyright 2017
 */
public class DomQueryListCell extends ListCell<DomQuery> {

    private final MessageSource messageSource;

    public DomQueryListCell(final MessageSource messageSource) {
        super();
        this.messageSource = messageSource;
    }

    @Override
    protected void updateItem(final DomQuery item, final boolean empty) {
        super.updateItem(item, empty);
        if (!empty) {
            if (item != null) {
                final FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getClassLoader().getResource("fxml/dom-query-list-cell.fxml"));
                final Parent parent = (Parent) Unchecked.function(o -> fxmlLoader.load()).apply(null);
                final DomQueryListCellController casted = fxmlLoader.getController();
                casted.setDomQuery(item, messageSource);
                setGraphic(parent);
            }
        } else {
            this.setText(null);
            this.setGraphic(null);
        }
    }

}
