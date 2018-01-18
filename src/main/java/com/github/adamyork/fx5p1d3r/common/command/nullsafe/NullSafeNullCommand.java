package com.github.adamyork.fx5p1d3r.common.command.nullsafe;

import com.github.adamyork.fx5p1d3r.common.command.CommandMap;

import java.util.Map;
import java.util.function.Function;

public class NullSafeNullCommand implements NullSafeInternalCommand {

    private final CommandMap<Boolean, NullSafeInternalCommand> nullInternalCommandMap;

    public NullSafeNullCommand() {
        nullInternalCommandMap = new CommandMap<>();
        nullInternalCommandMap.add(true, new NullSafeStringCommand());
        nullInternalCommandMap.add(false, new NullSafeDefaultCommand());
    }

    @Override
    public Object execute(final Map<Class, Function> instantiationMap,
                          final Class type,
                          final Object value) {
        return nullInternalCommandMap
                .getCommand(value instanceof String && ((String) value).isEmpty())
                .execute(instantiationMap, type, value);
    }

}
