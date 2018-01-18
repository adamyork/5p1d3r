package com.github.adamyork.fx5p1d3r.common.command.nullsafe;

import java.util.Map;
import java.util.function.Function;

public class NullSafeStringCommand implements NullSafeInternalCommand {

    @Override
    @SuppressWarnings("unchecked")
    public Object execute(final Map<Class, Function> instantiationMap,
                          final Class type,
                          final Object value) {
        return instantiationMap.get(String.class).apply(value);
    }
}
