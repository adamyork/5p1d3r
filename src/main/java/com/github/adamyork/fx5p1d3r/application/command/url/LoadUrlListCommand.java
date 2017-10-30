package com.github.adamyork.fx5p1d3r.application.command.url;

import com.github.adamyork.fx5p1d3r.common.NullSafe;
import com.github.adamyork.fx5p1d3r.common.command.ValidatorCommand;
import com.github.adamyork.fx5p1d3r.common.model.ApplicationFormState;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.File;
import java.util.List;

/**
 * Created by Adam York on 9/23/2017.
 * Copyright 2017
 */
@Component
public class LoadUrlListCommand implements ValidatorCommand {

    private ApplicationFormState applicationFormState;

    @Inject
    public LoadUrlListCommand(final ApplicationFormState applicationFormState) {
        this.applicationFormState = applicationFormState;
    }

    @Override
    public void execute(final List<String> urlStrings) {
        //no-op
    }

    @Override
    public void execute(final File file, final TextField textField) {
        final NullSafe<File> nullSafe = new NullSafe<>(File.class);
        final File nullSafeFile = nullSafe.getNullSafe(file);
        final String nullSafeFilePath = nullSafeFile.toString();
        textField.setText(nullSafeFilePath);
        applicationFormState.setUrlListFile(nullSafeFile);
    }
}
