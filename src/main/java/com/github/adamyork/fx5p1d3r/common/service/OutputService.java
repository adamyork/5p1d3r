package com.github.adamyork.fx5p1d3r.common.service;

import org.apache.commons.lang.NotImplementedException;
import org.jsoup.select.Elements;

import java.net.URL;
import java.util.List;

public interface OutputService {

    default void writeEntry(final Object object) {
        throw new NotImplementedException("write entry not define for class");
    }

    default void writeEntries(final String[] object) {
        throw new NotImplementedException("write entries not define for class");
    }

    List<URL> getUrlListFromElements(final Elements linksElementsList);

}
