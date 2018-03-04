package com.github.adamyork.fx5p1d3r.application.command.transform;

import com.github.adamyork.fx5p1d3r.common.command.NullSafeCommand;
import javafx.scene.control.ListView;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * Created by Adam York on 3/17/2017.
 * Copyright 2017
 */
@Component
public class ResultTransformListViewCommand implements NullSafeCommand<ListView<File>> {

    @Override
    public void execute(final ListView<File> fileListView, int index) {
        fileListView.getItems().remove(index);
    }

    @Override
    public void execute(final ListView<File> fileListView, final File file) {
        fileListView.getItems().add(file);
    }

    @Override
    public ListView<File> execute(final ListView<File> instance) {
        return null;
    }

}
