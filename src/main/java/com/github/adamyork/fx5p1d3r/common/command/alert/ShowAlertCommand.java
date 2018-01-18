package com.github.adamyork.fx5p1d3r.common.command.alert;

import com.github.adamyork.fx5p1d3r.common.command.io.HandlerCommand;
import javafx.scene.control.Alert;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.function.Function;

/**
 * Created by Adam York on 10/28/2017.
 * Copyright 2017
 */
@Component
public class ShowAlertCommand implements HandlerCommand<MessageSource, Function, Boolean> {

    @Override
    public Boolean execute(final MessageSource instance, final Function handler) {
        return true;
    }

    @Override
    public Boolean execute(final MessageSource instance, final Function handler, final String header, final String content) {
        final Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(instance.getMessage("alert.service.warning.label", null, Locale.getDefault()));
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.show();
        return null;
    }
}
