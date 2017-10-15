package com.github.adamyork.fx5p1d3r.common;

import com.github.adamyork.fx5p1d3r.common.model.GlobalDefault;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Adam York on 3/29/2017.
 * Copyright 2017
 */
@Component
public class GlobalDefaults {

    private final Map<GlobalDefault, String> defaultsMap = new HashMap<>();

    @PostConstruct
    public void init() {
        defaultsMap.put(GlobalDefault.STARTING_URL, "http://www.someurl.org");
        defaultsMap.put(GlobalDefault.ENDING_URL, "http://www.someurl.org");
        defaultsMap.put(GlobalDefault.LINKS_FOLLOW_DEPTH, "2");
        defaultsMap.put(GlobalDefault.LINK_PATTERN, ".*");
        defaultsMap.put(GlobalDefault.REQUEST_DELAY, "2000");
        defaultsMap.put(GlobalDefault.SAVE_FILE_NAME, "output");
        defaultsMap.put(GlobalDefault.DOM_QUERY, "img");
        defaultsMap.put(GlobalDefault.SAVE_DIR, "C:\\Users\\Public");
    }

    public String getDefaultForKey(final GlobalDefault globalDefault) {
        return defaultsMap.get(globalDefault);
    }

}
