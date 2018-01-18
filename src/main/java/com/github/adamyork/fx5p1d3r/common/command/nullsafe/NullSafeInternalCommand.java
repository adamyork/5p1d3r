package com.github.adamyork.fx5p1d3r.common.command.nullsafe;

import java.util.Map;
import java.util.function.Function;

public interface NullSafeInternalCommand {

    Object execute(final Map<Class, Function> instantiationMap, final Class type, final Object value);

}
