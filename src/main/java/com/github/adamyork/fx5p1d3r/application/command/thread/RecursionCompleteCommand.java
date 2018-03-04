package com.github.adamyork.fx5p1d3r.application.command.thread;

import com.github.adamyork.fx5p1d3r.common.model.DocumentListWithMemo;
import com.github.adamyork.fx5p1d3r.common.service.progress.ProgressService;
import com.github.adamyork.fx5p1d3r.common.service.progress.ProgressType;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutorService;

@Component
public class RecursionCompleteCommand implements LinkRecursionCommand {

    @Override
    public void execute(final List<URL> flattened,
                        final ExecutorService executorService,
                        final DocumentListWithMemo memo,
                        final ProgressService progressService,
                        LinksFollowCommand linksFollowCommand) {
        progressService.updateProgress(ProgressType.COMPLETE);
    }
}
