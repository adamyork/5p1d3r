package com.github.adamyork.fx5p1d3r.application.command.url;

import com.github.adamyork.fx5p1d3r.common.command.ValidatorCommand;
import javafx.scene.control.TextField;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;

/**
 * Created by Adam York on 9/23/2017.
 * Copyright 2017
 */
@Component
public class NoopValidatorCommand implements ValidatorCommand {

    @Override
    public void execute(final List<String> urlStrings) {
        //no-op
    }

    @Override
    public void execute(final File file, final TextField textField) {
        //no-op
    }

    @Override
    public String execute(final Element element, final String href) {
        return href;
    }
}
