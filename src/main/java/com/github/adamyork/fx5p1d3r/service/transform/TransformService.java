package com.github.adamyork.fx5p1d3r.service.transform;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.List;

/**
 * Created by Adam York on 8/28/2020.
 * Copyright 2020
 */
public interface TransformService {

    List<Object> transform(final Elements elements, final Document document);

}
