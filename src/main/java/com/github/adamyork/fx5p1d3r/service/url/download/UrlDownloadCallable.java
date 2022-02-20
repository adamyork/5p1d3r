package com.github.adamyork.fx5p1d3r.service.url.download;

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

/**
 * Created by Adam York on 10/11/2017.
 * Copyright 2017
 */
class UrlDownloadCallable extends Task<Boolean> {

    private static final Logger logger = LogManager.getLogger(UrlDownloadCallable.class);

    private final URL url;
    private final ProgressService progressService;
    private final ApplicationFormState applicationFormState;

    UrlDownloadCallable(final URL url,
                        final ProgressService progressService,
                        final ApplicationFormState applicationFormState) {
        this.url = url;
        this.progressService = progressService;
        this.applicationFormState = applicationFormState;
    }

    @SuppressWarnings("DuplicatedCode")
    @Override
    public Boolean call() {
        LogDirectoryHelper.manage();
        if (progressService.getCurrentProgressType().equals(ProgressType.ABORT)) {
            throw new RuntimeException("Abort detected cancelling throttled request chain");
        }
        progressService.updateProgress(ProgressType.FETCH);
        logger.debug("Fetching " + url);
        final InputStream input = Unchecked.function(o -> url.openStream()).apply(null);
        final ReadableByteChannel readableByteChannel = Channels.newChannel(input);
        if (progressService.getCurrentProgressType().equals(ProgressType.ABORT)) {
            try {
                readableByteChannel.close();
                input.close();
            } catch (final IOException exception) {
                exception.printStackTrace();
            }
            throw new RuntimeException("Abort detected cancelling throttled request chain");
        }
        progressService.updateProgress(ProgressType.RETRIEVED);
        final String dest = applicationFormState.getDownloadOutputFile().getAbsolutePath() + "\\" + SequentialDownloadTask.toValidFileName(url.toString());
        final FileOutputStream fileOutputStream = Unchecked.function(o -> new FileOutputStream(dest)).apply(null);
        final FileChannel fileChannel = fileOutputStream.getChannel();
        try {
            if (progressService.getCurrentProgressType().equals(ProgressType.ABORT)) {
                try {
                    fileChannel.close();
                    fileOutputStream.close();
                    readableByteChannel.close();
                    input.close();
                } catch (final IOException exception) {
                    exception.printStackTrace();
                }
                throw new RuntimeException("Abort detected cancelling throttled request chain");
            }
            progressService.updateProgress(ProgressType.OUTPUT);
            fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
            fileChannel.close();
            fileOutputStream.close();
            readableByteChannel.close();
            input.close();
            return true;
        } catch (final IOException exception) {
            exception.printStackTrace();
            return false;
        }
    }

    public URL getUrl() {
        return url;
    }
}
