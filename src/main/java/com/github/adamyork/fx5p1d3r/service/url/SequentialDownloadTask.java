package com.github.adamyork.fx5p1d3r.service.url;

import com.github.adamyork.fx5p1d3r.ApplicationFormState;
import com.github.adamyork.fx5p1d3r.LogDirectoryHelper;
import com.github.adamyork.fx5p1d3r.service.progress.ProgressService;
import com.github.adamyork.fx5p1d3r.service.progress.ProgressType;
import javafx.concurrent.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jooq.lambda.Unchecked;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.List;

public class SequentialDownloadTask extends Task<List<Boolean>> {

    private static final Logger logger = LogManager.getLogger(SequentialDownloadTask.class);
    private final List<URL> urls;
    private final ApplicationFormState applicationFormState;
    private final ProgressService progressService;

    public SequentialDownloadTask(final List<URL> urls,
                                  final ApplicationFormState applicationFormState,
                                  final ProgressService progressService) {
        this.urls = urls;
        this.applicationFormState = applicationFormState;
        this.progressService = progressService;
    }

    public static String toValidFileName(String input) {
        return input.replaceAll("[:\\\\/*\"?|<>']", "_");
    }

    @Override
    protected List<Boolean> call() {
        LogDirectoryHelper.manage();
        final List<Boolean> downloaded = new ArrayList<>();
        try {
            urls.forEach(url -> {
                logger.debug("Downloading url " + url);
                progressService.updateProgress(ProgressType.FETCH);
                final InputStream input = Unchecked.function(o -> url.openStream()).apply(null);
                final ReadableByteChannel readableByteChannel = Channels.newChannel(input);
                progressService.updateProgress(ProgressType.RETRIEVED);
                final String dest = applicationFormState.getDownloadOutputFile().getAbsolutePath() + "\\" + toValidFileName(url.toString());
                final FileOutputStream fileOutputStream = Unchecked.function(o -> new FileOutputStream(dest)).apply(null);
                final FileChannel fileChannel = fileOutputStream.getChannel();
                try {
                    progressService.updateProgress(ProgressType.OUTPUT);
                    fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                downloaded.add(true);
                if (progressService.getCurrentProgressType().equals(ProgressType.ABORT)) {
                    throw new RuntimeException("Abort detected cancelling throttled request chain");
                } else {
                    if (applicationFormState.throttling()) {
                        final long requestDelay = applicationFormState.getThrottleMs().getValue();
                        logger.debug("Document downloaded .. waiting " + requestDelay);
                        Unchecked.consumer(o -> Thread.sleep(requestDelay)).accept(null);
                    } else {
                        logger.debug("Document downloaded ");
                    }
                }
            });
        } catch (final Exception ex) {
            logger.debug("Abort detected cancelling, exception caught.");
        }
        return downloaded;
    }

}
