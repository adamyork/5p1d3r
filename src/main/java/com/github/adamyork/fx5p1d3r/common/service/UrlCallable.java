package com.github.adamyork.fx5p1d3r.common.service;

import org.jooq.lambda.Unchecked;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Whitelist;

import java.net.URL;
import java.util.concurrent.Callable;

/**
 * Created by Adam York on 3/8/2017.
 * Copyright 2017
 */
public class UrlCallable implements Callable<Document> {

    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.2; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/32.0.1667.0 Safari/537.36";

    private URL url;
    private Whitelist whitelist;

    public UrlCallable(final URL url, final Whitelist whitelist) {
        this.url = url;
        this.whitelist = whitelist;
    }

    @Override
    public Document call() {
        //final Cleaner cleaner = new Cleaner(whitelist);
        //cleaned = cleaner.clean(dirty);
        //outputManager.outputToApplication("Document Cleaned...");
        return Unchecked.function(urlToCall -> Jsoup.connect(urlToCall.toString()).userAgent(USER_AGENT).timeout(60000).get()).apply(url);
    }

    public URL getUrl() {
        return url;
    }
}
