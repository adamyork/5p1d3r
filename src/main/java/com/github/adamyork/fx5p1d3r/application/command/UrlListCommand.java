package com.github.adamyork.fx5p1d3r.application.command;

import com.github.adamyork.fx5p1d3r.common.command.ApplicationCommand;
import com.github.adamyork.fx5p1d3r.common.model.ApplicationFormState;
import com.github.adamyork.fx5p1d3r.common.service.progress.ProgressService;
import com.github.adamyork.fx5p1d3r.common.service.progress.ProgressType;
import javafx.scene.control.Alert;
import org.apache.commons.io.FileUtils;
import org.jooq.lambda.Unchecked;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * Created by Adam York on 2/23/2017.
 * Copyright 2017
 */
@Component
@Qualifier("UrlListCommand")
public class UrlListCommand implements ApplicationCommand {

    protected final ApplicationFormState applicationFormState;
    protected final UrlValidatorCommand urlValidatorCommand;
    protected final ProgressService progressService;

    @Autowired
    public UrlListCommand(final ApplicationFormState applicationFormState,
                          final UrlValidatorCommand urlValidatorCommand,
                          final ProgressService progressService) {
        this.applicationFormState = applicationFormState;
        this.urlValidatorCommand = urlValidatorCommand;
        this.progressService = progressService;
    }

    @Override
    public void execute() {
        final File urlListFile = applicationFormState.getUrlListFile();
        if (urlListFile == null) {
            progressService.updateSteps(0);
            progressService.updateProgress(ProgressType.ABORT);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("No URL list selected.");
            alert.setContentText("No URL list selected. Please select a URL list.");
            alert.showAndWait();
            return;
        }
        final List<String> urlStrings = Unchecked.function(o -> FileUtils.readLines(urlListFile)).apply(null);
        progressService.updateSteps(urlStrings.size());
        progressService.updateProgress(ProgressType.START);
        urlValidatorCommand.execute(urlStrings);
    }

    @Override
    public void execute(final List<URL> urls) {}

    @Override
    public void execute(final List<URL> urls, final ExecutorService executorService) {}

}
