package com.github.adamyork.fx5p1d3r.service.output;

import com.github.adamyork.fx5p1d3r.ApplicationFormState;
import com.github.adamyork.fx5p1d3r.service.progress.ProgressService;
import com.github.adamyork.fx5p1d3r.service.progress.ProgressType;
import com.github.adamyork.fx5p1d3r.service.url.data.DocumentListWithMemo;
import javafx.concurrent.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jooq.lambda.Unchecked;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;
import org.jooq.lambda.tuple.Tuple4;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

public class BaseOutputTask extends Task<List<Tuple4<List<Boolean>, Document, List<URL>, Optional<DocumentListWithMemo>>>> {

    private static final Logger logger = LogManager.getLogger(BaseOutputTask.class);

    protected final ApplicationFormState applicationFormState;
    protected final ProgressService progressService;

    public BaseOutputTask(final ApplicationFormState applicationFormState,
                          final ProgressService progressService) {
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
        try {
            final byte[] data = Files.readAllBytes(path);
            return Tuple.tuple(destFile, data);
        } catch (final IOException exception) {
            logger.error("Exception reading from output file");
            return Tuple.tuple(destFile, null);
        }
    }

    @Override
    protected List<Tuple4<List<Boolean>, Document, List<URL>, Optional<DocumentListWithMemo>>> call() {
        return null;
    }
}
