package com.webcrawler.service;

import com.webcrawler.config.JsoupConfig;
import com.webcrawler.model.CrawlResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Implementation of CrawlerService using Jsoup for web crawling
 * with robots.txt validation
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CrawlerServiceImpl implements CrawlerService {

    private final JsoupCrawlerWrapper jsoupCrawlerWrapper;
    private final RobotsTxtService robotsTxtService;
    private final JsoupConfig jsoupConfig;

    @Override
    public CrawlResponse crawl(String url) {
        log.info("Crawling URL: {}", url);

        // Check robots.txt before crawling
        if (!robotsTxtService.isAllowed(url, jsoupConfig.getUserAgent())) {
            log.warn("Crawling blocked by robots.txt for URL: {}", url);
            return CrawlResponse.builder()
                    .url(url)
                    .statusCode(HttpStatus.FORBIDDEN.value())
                    .success(false)
                    .timestamp(LocalDateTime.now())
                    .message("Crawling not allowed by robots.txt")
                    .data(null)
                    .build();
        }

        return jsoupCrawlerWrapper.crawl(url);
    }
}
