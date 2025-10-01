package com.webcrawler.service;

import com.webcrawler.model.CrawlResponse;

/**
 * Interface for Jsoup crawler operations
 */
public interface JsoupCrawlerWrapper {

    /**
     * Crawls the specified URL using Jsoup and returns parsed result
     *
     * @param url the URL to crawl
     * @return CrawlResponse containing the crawl results with parsed data
     */
    CrawlResponse crawl(String url);
}
