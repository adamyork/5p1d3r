package com.github.adamyork.fx5p1d3r.application.command;

import com.github.adamyork.fx5p1d3r.application.view.query.cell.DomQuery;
import com.github.adamyork.fx5p1d3r.common.command.NullSafeCommand;
import javafx.scene.control.ListView;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * Created by Adam York on 3/17/2017.
 * Copyright 2017
 */
@Component
public class DomQueryListViewCommand implements NullSafeCommand<ListView<DomQuery>> {

    @Override
    public void execute(final ListView<DomQuery> domQueryListView, final int index) {
        domQueryListView.getItems().remove(index);
    }

    @Override
    public void execute(final ListView<DomQuery> fileListView, final File file) {
        //no-op
    }

}
