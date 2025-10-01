package com.webcrawler.service;

import com.webcrawler.model.CrawlResponse;

/**
 * Interface for web crawling operations
 */
public interface CrawlerService {

    /**
     * Crawls the specified URL and returns parsed result
     *
     * @param url the URL to crawl
     * @return CrawlResponse containing the crawl results
     */
    CrawlResponse crawl(String url);
}
