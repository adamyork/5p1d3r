package com.github.adamyork.fx5p1d3r.common.command;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.function.Function;

/**
 * Created by Adam York on 10/28/2017.
 * Copyright 2017
 */
@Component
public class NoAlertCommand implements HandlerCommand<MessageSource, Function, Boolean> {

    @Override
    public Boolean execute(final MessageSource instance, final Function handler) {
        return true;
    }

    @Override
    public Boolean execute(final MessageSource instance, final Function handler, final String header, final String content) {
        return true;
    }

}
