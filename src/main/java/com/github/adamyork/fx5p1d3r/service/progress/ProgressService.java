package com.github.adamyork.fx5p1d3r.service.progress;

import java.beans.PropertyChangeListener;

/**
 * Created by Adam York on 9/13/2020.
 * Copyright 2020
 */
public interface ProgressService {

    void addListener(final PropertyChangeListener pcl);

    ProgressType getCurrentProgressType();

    ProgressState getProgressState();

    void removeListener(final PropertyChangeListener pcl);

    void updateSteps(final int size);

    void updateProgress(final ProgressType progressType);

    void forceComplete();

}
