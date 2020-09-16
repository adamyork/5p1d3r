package com.github.adamyork.fx5p1d3r.application.view.query;

import com.github.adamyork.fx5p1d3r.application.view.query.cell.DomQuery;
import com.github.adamyork.fx5p1d3r.application.view.query.cell.DomQueryListCell;
import com.github.adamyork.fx5p1d3r.common.GlobalDefaults;
import com.github.adamyork.fx5p1d3r.common.model.ApplicationFormState;
import com.github.adamyork.fx5p1d3r.common.model.GlobalDefault;
import com.github.adamyork.fx5p1d3r.common.service.progress.ProgressService;
import com.github.adamyork.fx5p1d3r.common.service.progress.ProgressType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Created by Adam York on 3/24/2017.
 * Copyright 2017
 */
@Component
public class QueryController implements Initializable, PropertyChangeListener {

    private final ApplicationFormState applicationFormState;
    private final GlobalDefaults globalDefaults;
    private final MessageSource messageSource;
    private final ProgressService progressService;

    @FXML
    private ListView<DomQuery> domQueryListView;
    @FXML
    private Button addDomQuery;
    @FXML
    private Button removeDomQuery;
    @FXML
    private Label queryCount;
    @FXML
    private Label domQueriesLabel;

    @Inject
    public QueryController(final ApplicationFormState applicationFormState,
                           final GlobalDefaults globalDefaults,
                           final MessageSource messageSource,
                           final ProgressService progressService) {
        this.applicationFormState = applicationFormState;
        this.globalDefaults = globalDefaults;
        this.messageSource = messageSource;
        this.progressService = progressService;
    }

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        domQueryListView.setCellFactory(listView -> new DomQueryListCell());
        addDomQuery.setOnAction(this::handleAddDomQuery);
        removeDomQuery.setOnAction(this::handleRemoveDomQuery);
        final DomQuery defaultQuery = new DomQuery.Builder(globalDefaults.getDefaultForKey(GlobalDefault.DOM_QUERY),
                domQueryListView.getItems().size()).build();
        domQueryListView.getItems().add(defaultQuery);
        FXCollections.observableArrayList(domQueryListView.getItems());
        applicationFormState.setDomQueryObservableList(domQueryListView.getItems());
        domQueriesLabel.setText(messageSource.getMessage("dom.queries.label", null, Locale.getDefault()));

        progressService.addListener(this);
    }

    private void handleAddDomQuery(final ActionEvent actionEvent) {
        final ObservableList<DomQuery> domQueries = domQueryListView.getItems();
        domQueryListView.getItems().add(new DomQuery.Builder().id(domQueries.size()).build());
        applicationFormState.setDomQueryObservableList(domQueries);
        queryCount.setText(Integer.toString(domQueryListView.getItems().size()));
    }

    private void handleRemoveDomQuery(final ActionEvent actionEvent) {
        final int selectedIndex = domQueryListView.getSelectionModel().getSelectedIndex();
        if (selectedIndex != -1) {
            domQueryListView.getItems().remove(selectedIndex);
        }
        applicationFormState.setDomQueryObservableList(domQueryListView.getItems());
        queryCount.setText(Integer.toString(domQueryListView.getItems().size()));
    }

    @Override
    public void propertyChange(final PropertyChangeEvent evt) {
        if (progressService.getCurrentProgressType().equals(ProgressType.COMPLETE) ||
                progressService.getCurrentProgressType().equals(ProgressType.ABORT)) {
            domQueryListView.setDisable(false);
            addDomQuery.setDisable(false);
            removeDomQuery.setDisable(false);
        } else {
            domQueryListView.setDisable(true);
            addDomQuery.setDisable(true);
            removeDomQuery.setDisable(true);
        }
    }
}
