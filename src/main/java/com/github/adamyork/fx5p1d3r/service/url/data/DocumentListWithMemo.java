package com.github.adamyork.fx5p1d3r.service.url.data;

import org.jsoup.nodes.Document;

import java.util.List;

public class DocumentListWithMemo {

    private List<Document> documents;
    private int threadPoolSize;
    private int currentDepth;
    private int maxDepth;

    public List<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(final List<Document> documents) {
        this.documents = documents;
    }

    public int getThreadPoolSize() {
        return threadPoolSize;
    }

    public void setThreadPoolSize(final int threadPoolSize) {
        this.threadPoolSize = threadPoolSize;
    }

    public int getCurrentDepth() {
        return currentDepth;
    }

    public void setCurrentDepth(final int currentDepth) {
        this.currentDepth = currentDepth;
    }

    public int getMaxDepth() {
        return maxDepth;
    }

    public void setMaxDepth(final int maxDepth) {
        this.maxDepth = maxDepth;
    }
}
