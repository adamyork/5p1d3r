package com.github.adamyork.fx5p1d3r;

import org.slf4j.MDC;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Created by Adam York on 9/2/2020.
 * Copyright 2020
 */
public class LogDirectoryHelper {

    public static Path getTempDirectoryPath() {
        final String systemTempDirectory = System.getProperty("java.io.tmpdir");
        return Path.of(systemTempDirectory + File.separator + "51p3dr");
    }

    public static void manage() {
        final Path tempDirectoryPath = LogDirectoryHelper.getTempDirectoryPath();
        if (Files.notExists(tempDirectoryPath)) {
            final File file = new File(tempDirectoryPath.toUri());
            final boolean tempDirectorySucceeded = file.mkdirs();
            if (!tempDirectorySucceeded) {
                throw new RuntimeException("Can create temporary directory.");
            }
        }
        MDC.put("LOG_PATH", tempDirectoryPath.toString() + File.separator);
    }

}
