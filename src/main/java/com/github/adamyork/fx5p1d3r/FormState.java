package com.github.adamyork.fx5p1d3r;

import java.beans.PropertyChangeListener;

/**
 * Created by Adam York on 9/14/2020.
 * Copyright 2020
 */
public interface FormState {

    void addListener(final PropertyChangeListener pcl);

    void removeListener(final PropertyChangeListener pcl);

    void notifyChanged();

}
