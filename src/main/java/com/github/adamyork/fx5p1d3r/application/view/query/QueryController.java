package com.github.adamyork.fx5p1d3r.application.view.query;

import com.github.adamyork.fx5p1d3r.application.view.query.cell.DOMQuery;
import com.github.adamyork.fx5p1d3r.application.view.query.cell.DOMQueryListCell;
import com.github.adamyork.fx5p1d3r.common.GlobalDefaults;
import com.github.adamyork.fx5p1d3r.common.command.CommandMap;
import com.github.adamyork.fx5p1d3r.common.command.NullSafeCommand;
import com.github.adamyork.fx5p1d3r.common.model.ApplicationFormState;
import com.github.adamyork.fx5p1d3r.common.model.GlobalDefault;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Created by Adam York on 3/24/2017.
 * Copyright 2017
 */
@Component
public class QueryController implements Initializable {

    @FXML
    private ListView<DOMQuery> domQueryListView;
    @FXML
    private Button addDOMQuery;
    @FXML
    private Button removeDOMQuery;
    @FXML
    private Label queryCount;
    @FXML
    private Label domQueriesLabel;

    private ApplicationFormState applicationFormState;
    private CommandMap<Boolean, NullSafeCommand> domQueryListViewCommandMap;
    private GlobalDefaults globalDefaults;
    private MessageSource messageSource;

    @Autowired
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
        domQueryListView.setCellFactory(listView -> new DOMQueryListCell());
        addDOMQuery.setOnAction(this::handleAddDOMQuery);
        removeDOMQuery.setOnAction(this::handleRemoveDOMQuery);
        final DOMQuery defaultQuery = new DOMQuery.Builder(globalDefaults.getDefaultForKey(GlobalDefault.DOM_QUERY),
                domQueryListView.getItems().size()).build();
        domQueryListView.getItems().add(defaultQuery);
        applicationFormState.setDomQueryObservableList(domQueryListView.getItems());
        domQueriesLabel.setText(messageSource.getMessage("dom.queries.label", null, Locale.getDefault()));
    }

    @SuppressWarnings("unused")
    private void handleAddDOMQuery(final ActionEvent actionEvent) {
        final ObservableList<DOMQuery> domQueries = domQueryListView.getItems();
        domQueryListView.getItems().add(new DOMQuery.Builder().id(domQueries.size()).build());
        applicationFormState.setDomQueryObservableList(domQueries);
        queryCount.setText(Integer.toString(domQueryListView.getItems().size()));
    }

    @SuppressWarnings({"unchecked", "unused"})
    private void handleRemoveDOMQuery(final ActionEvent actionEvent) {
        final int selectedIndex = domQueryListView.getSelectionModel().getSelectedIndex();
        domQueryListViewCommandMap.getCommand(selectedIndex != -1).execute(domQueryListView, selectedIndex);
        applicationFormState.setDomQueryObservableList(domQueryListView.getItems());
        queryCount.setText(Integer.toString(domQueryListView.getItems().size()));
    }

}
