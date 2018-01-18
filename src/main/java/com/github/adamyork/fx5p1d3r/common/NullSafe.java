package com.github.adamyork.fx5p1d3r.common;

import com.github.adamyork.fx5p1d3r.common.command.CommandMap;
import com.github.adamyork.fx5p1d3r.common.command.nullsafe.NullSafeInternalCommand;
import com.github.adamyork.fx5p1d3r.common.command.nullsafe.NullSafeNotNullCommand;
import com.github.adamyork.fx5p1d3r.common.command.nullsafe.NullSafeNullCommand;
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

    private final Class<T> type;
    private final Map<Class, Function> instantiationMap;
    private CommandMap<Boolean, NullSafeInternalCommand> nullSafeInternalCommandMap;

    public NullSafe(final Class<T> type) {
        this.type = type;
        instantiationMap = new HashMap<>();
        instantiationMap.put(URL.class, getNewUrlFunction());
        instantiationMap.put(String.class, getNewString());
        instantiationMap.put(File.class, getNewFile());
        nullSafeInternalCommandMap = new CommandMap<>();
        nullSafeInternalCommandMap.add(true, new NullSafeNullCommand());
        nullSafeInternalCommandMap.add(false, new NullSafeNotNullCommand());
    }

    @SuppressWarnings("unchecked")
    public T getNullSafe(final T value) {
        return (T) nullSafeInternalCommandMap
                .getCommand(value == null)
                .execute(instantiationMap, type, value);
    }

    @SuppressWarnings("unchecked")
    private Function<T, T> getNewUrlFunction() {
        return t -> (T) Unchecked.function(o -> new URL("http://www.123456789010101.com"))
                .apply(null);
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
