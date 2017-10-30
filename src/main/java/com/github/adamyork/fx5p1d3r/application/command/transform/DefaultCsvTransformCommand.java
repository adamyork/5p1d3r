package com.github.adamyork.fx5p1d3r.application.command.transform;

import com.github.adamyork.fx5p1d3r.Main;
import com.github.adamyork.fx5p1d3r.common.command.NullSafeCommand;
import org.apache.commons.io.FileUtils;
import org.jooq.lambda.Unchecked;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.InputStream;

/**
 * Created by Adam York on 10/27/2017.
 * Copyright 2017
 */
@Component
public class DefaultCsvTransformCommand implements NullSafeCommand<File> {

    @Override
    public void execute(final File listView, final int index) {

    }

    @Override
    public void execute(final File listView, final File file) {

    }

    @Override
    public File execute(final File instance) {
        final InputStream stream = Main.class.getClassLoader().getResourceAsStream(("basicCsvTransform.groovy"));
        final File basicCsvTransform = new File("basicCsvTransform");
        basicCsvTransform.deleteOnExit();
        Unchecked.consumer(consumer -> FileUtils.copyInputStreamToFile(stream, basicCsvTransform)).accept(null);
        return basicCsvTransform;
    }
}
