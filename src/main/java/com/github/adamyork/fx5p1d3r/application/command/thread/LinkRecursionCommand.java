package com.github.adamyork.fx5p1d3r.application.command.thread;

import com.github.adamyork.fx5p1d3r.common.model.DocumentListWithMemo;
import com.github.adamyork.fx5p1d3r.common.service.progress.ProgressService;

import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutorService;

public interface LinkRecursionCommand {

    void execute(final List<URL> flattened,
                 final ExecutorService executorService,
                 final DocumentListWithMemo memo,
                 final ProgressService progressService,
                 final LinksFollowCommand linksFollowCommand);
}
