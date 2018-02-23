package com.github.adamyork.fx5p1d3r.application.view.query;

import com.github.adamyork.fx5p1d3r.application.view.query.cell.DomQuery;
import com.github.adamyork.fx5p1d3r.application.view.query.cell.DomQueryListCell;
import com.github.adamyork.fx5p1d3r.common.GlobalDefaults;
import com.github.adamyork.fx5p1d3r.common.command.CommandMap;
import com.github.adamyork.fx5p1d3r.common.command.NullSafeCommand;
import com.github.adamyork.fx5p1d3r.common.model.ApplicationFormState;
import com.github.adamyork.fx5p1d3r.common.model.GlobalDefault;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Created by Adam York on 3/24/2017.
 * Copyright 2017
 */
@Component
public class QueryController implements Initializable {

    private final ApplicationFormState applicationFormState;
    private final CommandMap<Boolean, NullSafeCommand> domQueryListViewCommandMap;
    private final GlobalDefaults globalDefaults;
    private final MessageSource messageSource;

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
                           @Qualifier("DomQueryListViewCommandMap") final CommandMap<Boolean, NullSafeCommand> domQueryListViewCommandMap) {
        this.applicationFormState = applicationFormState;
        this.domQueryListViewCommandMap = domQueryListViewCommandMap;
        this.globalDefaults = globalDefaults;
        this.messageSource = messageSource;
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
    }

    @SuppressWarnings("unused")
    private void handleAddDomQuery(final ActionEvent actionEvent) {
        final ObservableList<DomQuery> domQueries = domQueryListView.getItems();
        domQueryListView.getItems().add(new DomQuery.Builder().id(domQueries.size()).build());
        applicationFormState.setDomQueryObservableList(domQueries);
        queryCount.setText(Integer.toString(domQueryListView.getItems().size()));
    }

    @SuppressWarnings({"unchecked", "unused"})
    private void handleRemoveDomQuery(final ActionEvent actionEvent) {
        final int selectedIndex = domQueryListView.getSelectionModel().getSelectedIndex();
        domQueryListViewCommandMap.getCommand(selectedIndex != -1).execute(domQueryListView, selectedIndex);
        applicationFormState.setDomQueryObservableList(domQueryListView.getItems());
        queryCount.setText(Integer.toString(domQueryListView.getItems().size()));
    }

}
