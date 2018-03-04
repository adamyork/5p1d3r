package com.github.adamyork.fx5p1d3r.common.command;

import javafx.scene.control.TextField;
import org.jsoup.nodes.Element;

import java.io.File;
import java.util.List;

/**
 * Created by Adam York on 3/23/2017.
 * Copyright 2017
 */
public interface ValidatorCommand {

    void execute(final List<String> urlStrings);

    void execute(final File file, final TextField textField);

    String execute(final Element element, final String href);

}
