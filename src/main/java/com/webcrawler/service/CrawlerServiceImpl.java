package com.webcrawler.service;

import com.webcrawler.model.CrawlResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Basic implementation of CrawlerService
 * Actual crawling functionality to be implemented later
 */
@Service
@Slf4j
public class CrawlerServiceImpl implements CrawlerService {

    @Override
    public CrawlResponse crawl(String url) {
        log.info("Crawling URL: {}", url);

        // Basic implementation - actual crawling logic to be added later
        return CrawlResponse.builder()
                .url(url)
                .statusCode(200)
                .success(true)
                .timestamp(LocalDateTime.now())
                .message("Crawler service initialized - crawling functionality to be implemented")
                .data(null)
                .build();
    }
}
