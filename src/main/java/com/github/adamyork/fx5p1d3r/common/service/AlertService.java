package com.github.adamyork.fx5p1d3r.common.service;

import com.github.adamyork.fx5p1d3r.common.command.CommandMap;
import com.github.adamyork.fx5p1d3r.common.command.HandlerCommand;
import com.github.adamyork.fx5p1d3r.common.service.progress.ProgressService;
import com.github.adamyork.fx5p1d3r.common.service.progress.ProgressType;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogEvent;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Function;

/**
 * Created by Adam York on 10/18/2017.
 * Copyright 2017
 */
@Component
public class AlertService {

    private final CommandMap<Boolean, HandlerCommand<MessageSource, Function, Boolean>> warnHandlerCommandMap;
    private final ProgressService progressService;
    private final MessageSource messageSource;
    private boolean warningShown = false;

    @Inject
    public AlertService(final ProgressService progressService,
                        final MessageSource messageSource,
                        @Qualifier("WarnHandlerCommandMap") final CommandMap<Boolean, HandlerCommand<MessageSource, Function, Boolean>> warnHandlerCommandMap) {
        this.progressService = progressService;
        this.messageSource = messageSource;
        this.warnHandlerCommandMap = warnHandlerCommandMap;
    }

    public Optional<ButtonType> error(final String header, final String content) {
        progressService.updateSteps(0);
        progressService.updateProgress(ProgressType.ABORT);
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(messageSource.getMessage("alert.service.error.label", null, Locale.getDefault()));
        alert.setHeaderText(header);
        alert.setContentText(content);
        return alert.showAndWait();
    }

    public void warn(final String header, final String content) {
        warningShown = warnHandlerCommandMap.getCommand(warningShown)
                .execute(messageSource, getHandlerFunction(), header, content);
    }

    private Function<Alert, Alert> getHandlerFunction() {
        return alert -> {
            alert.setOnHidden(this::handleOnHidden);
            return alert;
        };
    }

    private void handleOnHidden(final DialogEvent dialogEvent) {
        warningShown = false;
    }
}
