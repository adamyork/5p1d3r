package com.github.adamyork.fx5p1d3r.service.output;

import com.github.adamyork.fx5p1d3r.ApplicationFormState;
import com.github.adamyork.fx5p1d3r.service.progress.ApplicationProgressService;
import com.github.adamyork.fx5p1d3r.service.progress.ProgressType;
import javafx.concurrent.Task;
import org.jooq.lambda.Unchecked;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class BaseOutputTask extends Task<Boolean> {

    protected final ApplicationFormState applicationFormState;
    protected final ApplicationProgressService progressService;

    public BaseOutputTask(final ApplicationFormState applicationFormState,
                          final ApplicationProgressService progressService) {
        this.applicationFormState = applicationFormState;
        this.progressService = progressService;
    }

    protected Tuple2<File, byte[]> getFileAndBytes() {
        progressService.updateProgress(ProgressType.OUTPUT);
        final String outputFile = applicationFormState.getOutputFile();
        final File destFile = new File(outputFile);
        if (!destFile.exists()) {
            //noinspection ResultOfMethodCallIgnored
            Unchecked.consumer(f -> ((File) f).createNewFile()).accept(destFile);
        }
        final Path path = Paths.get(outputFile);
        final byte[] data = Unchecked.function(p -> Files.readAllBytes((Path) p)).apply(path);
        return Tuple.tuple(destFile, data);
    }

    @Override
    protected Boolean call() {
        return null;
    }
}
