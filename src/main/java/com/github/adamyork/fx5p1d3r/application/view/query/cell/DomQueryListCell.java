package com.github.adamyork.fx5p1d3r.application.view.query.cell;

import com.github.adamyork.fx5p1d3r.common.command.CommandMap;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ListCell;
import org.jooq.lambda.Unchecked;
import org.jooq.lambda.function.Consumer2;

/**
 * Created by Adam York on 2/24/2017.
 * Copyright 2017
 */
public class DomQueryListCell extends ListCell<DomQuery> {

    private final CommandMap<Boolean, Consumer2<DomQueryListCell, DomQuery>> cellCommandMap = new CommandMap<>();
    private final CommandMap<Boolean, Consumer2<DomQueryListCell, DomQuery>> itemCommandMap = new CommandMap<>();

    public DomQueryListCell() {
        super();
        cellCommandMap.add(true, getCellNotEmpty());
        cellCommandMap.add(false, getCellEmpty());
        itemCommandMap.add(true, getItemNotEmptyConsumer());
        itemCommandMap.add(false, getItemEmptyConsumer());
    }

    @Override
    protected void updateItem(final DomQuery item, final boolean empty) {
        super.updateItem(item, empty);
        cellCommandMap.getCommand(!empty).accept(this, item);
    }

    private Consumer2<DomQueryListCell, DomQuery> getCellNotEmpty() {
        return (domQueryListCell, domQuery) -> itemCommandMap.getCommand(domQuery != null)
                .accept(domQueryListCell, domQuery);
    }

    private Consumer2<DomQueryListCell, DomQuery> getCellEmpty() {
        return (domQueryListCell, domQuery) -> {
            domQueryListCell.setText(null);
            domQueryListCell.setGraphic(null);
        };
    }

    private Consumer2<DomQueryListCell, DomQuery> getItemNotEmptyConsumer() {
        return (domQueryListCell, domQuery) -> {
            final FXMLLoader fxmlLoader = new FXMLLoader(domQueryListCell.getClass().getClassLoader().getResource("fxml/dom-query-list-cell.fxml"));
            final Parent parent = (Parent) Unchecked.function(o -> fxmlLoader.load()).apply(null);
            final DomQueryListCellController casted = fxmlLoader.getController();
            casted.setDomQuery(domQuery);
            setGraphic(parent);
        };
    }

    private Consumer2<DomQueryListCell, DomQuery> getItemEmptyConsumer() {
        return (domQueryListCell, domQuery) -> {
        };
    }
}
