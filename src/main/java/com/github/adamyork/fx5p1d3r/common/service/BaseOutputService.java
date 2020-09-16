package com.github.adamyork.fx5p1d3r.common.service;

import com.github.adamyork.fx5p1d3r.common.Validator;
import com.github.adamyork.fx5p1d3r.common.model.ApplicationFormState;
import com.github.adamyork.fx5p1d3r.common.service.progress.ApplicationProgressService;
import com.github.adamyork.fx5p1d3r.common.service.progress.ProgressType;
import org.jooq.lambda.Unchecked;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;
import org.jsoup.select.Elements;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class BaseOutputService implements OutputService {

    protected final Validator validator;
    protected final ApplicationFormState applicationFormState;
    protected final ApplicationProgressService progressService;

    public BaseOutputService(final ApplicationFormState applicationFormState,
                             final Validator validator,
                             final ApplicationProgressService progressService) {
        this.validator = validator;
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

    public List<URL> getUrlListFromElements(final Elements elements) {
        return elements.stream()
                .map(element -> {
                    final String href = element.attr("href");
                    if (href.contains("http") || href.contains("https")) {
                        final URI uri = Unchecked.function(t -> new URI(href)).apply(null);
                        return uri.toString();
                    } else if (href.contains("//")) {
                        return "https:" + href;
                    }
                    return href;
                })
                .filter(validator::validateUrlString)
                .map(Unchecked.function(URL::new))
                .collect(Collectors.toList());
    }
}
