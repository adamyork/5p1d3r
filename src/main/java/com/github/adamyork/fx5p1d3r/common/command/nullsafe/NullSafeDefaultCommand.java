package com.github.adamyork.fx5p1d3r.common.command.nullsafe;

import java.util.Map;
import java.util.function.Function;

public class NullSafeDefaultCommand implements NullSafeInternalCommand {

    @Override
    @SuppressWarnings("unchecked")
    public Object execute(Map<Class, Function> instantiationMap, Class type, Object value) {
        return instantiationMap.get(type).apply(null);
    }
}
