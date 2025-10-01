package com.webcrawler.service;

import com.webcrawler.model.CrawlResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CrawlerServiceImplTest {

    private CrawlerService crawlerService;

    @BeforeEach
    void setUp() {
        crawlerService = new CrawlerServiceImpl();
    }

    @Test
    void crawl_WithValidUrl_ReturnsResponse() {
        // Arrange
        String url = "https://example.com";

        // Act
        CrawlResponse response = crawlerService.crawl(url);

        // Assert
        assertNotNull(response);
        assertEquals(url, response.getUrl());
        assertNotNull(response.getTimestamp());
        assertTrue(response.getSuccess());
    }

    @Test
    void crawl_ReturnsStatusCode200() {
        // Arrange
        String url = "https://example.com";

        // Act
        CrawlResponse response = crawlerService.crawl(url);

        // Assert
        assertEquals(200, response.getStatusCode());
    }

    @Test
    void crawl_ReturnsNonNullMessage() {
        // Arrange
        String url = "https://example.com";

        // Act
        CrawlResponse response = crawlerService.crawl(url);

        // Assert
        assertNotNull(response.getMessage());
        assertFalse(response.getMessage().isEmpty());
    }
}
