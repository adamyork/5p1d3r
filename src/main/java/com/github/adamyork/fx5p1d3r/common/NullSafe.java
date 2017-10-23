package com.github.adamyork.fx5p1d3r.common;

import org.jooq.lambda.Unchecked;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Created by Adam York on 3/9/2017.
 * Copyright 2017
 */
public class NullSafe<T> {

    private Class<T> type;
    private Map<Class, Function> instantiationMap;

    public NullSafe(final Class<T> type) {
        this.type = type;
        instantiationMap = new HashMap<>();
        instantiationMap.put(URL.class, getNewUrlFunction());
        instantiationMap.put(String.class, getNewString());
        instantiationMap.put(File.class, getNewFile());
    }

    @SuppressWarnings("unchecked")
    public T getNullSafe(final T value) {
        //TODO COMMAND
        if (value == null) {
            return (T) instantiationMap.get(type).apply(null);
        } else if (value instanceof String && ((String) value).isEmpty()) {
            return (T) instantiationMap.get(String.class).apply(value);
        }
        return value;
    }

    @SuppressWarnings("unchecked")
    private Function<T, T> getNewUrlFunction() {
        return t -> (T) Unchecked.function(o -> new URL("http://www.123456789010101.com")).apply(null);
    }

    @SuppressWarnings("unchecked")
    private Function<T, T> getNewString() {
        return t -> (T) "0";
    }

    @SuppressWarnings("unchecked")
    private Function<T, T> getNewFile() {
        return t -> (T) new File("");
    }

    public static String getSafeString(final String string) {
        final NullSafe<String> nullSafe = new NullSafe<>(String.class);
        return nullSafe.getNullSafe(string);
    }

}
