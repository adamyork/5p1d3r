package com.github.adamyork.fx5p1d3r.service.progress;

/**
 * Created by Adam York on 10/7/2017.
 * Copyright 2017
 */
public class ProgressState {

    private String message;
    private double progress;

    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    public double getProgress() {
        return progress;
    }

    public void setProgress(final double progress) {
        this.progress = progress;
    }
}
