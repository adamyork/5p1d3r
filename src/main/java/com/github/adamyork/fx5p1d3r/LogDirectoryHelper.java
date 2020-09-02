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

    public static void manage() {
        final String systemTempDirectory = System.getProperty("java.io.tmpdir");
        final Path tempDirectoryPath = Path.of(systemTempDirectory + File.separator + "51p3dr");
        if (Files.notExists(tempDirectoryPath)) {
            final boolean tempDirectorySucceeded = new File(tempDirectoryPath.toUri()).mkdirs();
            if (!tempDirectorySucceeded) {
                throw new RuntimeException("Can create temporary directory.");
            }
        }
        MDC.put("LOG_PATH", tempDirectoryPath.toString() + File.separator);
    }

}
