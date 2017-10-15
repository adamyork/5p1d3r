package com.github.adamyork.fx5p1d3r.common.command;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Adam York on 2/23/2017.
 * Copyright 2017
 */
public class CommandMap<T, C> {

    private Map<T, C> map;

    public CommandMap() {
        map = new HashMap<>();
    }

    public void add(final T value, final C command) {
        map.put(value, command);
    }

    public C getCommand(final T clazz) {
        return map.get(clazz);
    }

}
