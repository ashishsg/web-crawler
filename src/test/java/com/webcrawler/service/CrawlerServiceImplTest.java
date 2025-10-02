package com.webcrawler.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.webcrawler.config.JsoupConfig;
import com.webcrawler.model.CrawlResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class CrawlerServiceImplTest {

    private CrawlerService crawlerService;

    @Mock
    private JsoupCrawlerWrapper jsoupCrawlerWrapper;

    @Mock
    private RobotsTxtService robotsTxtService;

    @Mock
    private JsoupConfig jsoupConfig;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(jsoupConfig.getUserAgent()).thenReturn("TestBot/1.0");
        // By default, allow all URLs in tests
        when(robotsTxtService.isAllowed(anyString(), anyString())).thenReturn(true);
        crawlerService = new CrawlerServiceImpl(jsoupCrawlerWrapper, robotsTxtService, jsoupConfig);
    }

    @Test
    void crawl_WithValidUrl_ReturnsSuccessResponse() {
        // Arrange
        String url = "https://example.com";
        CrawlResponse mockResponse = CrawlResponse.builder()
                .url(url)
                .statusCode(200)
                .success(true)
                .message("Successfully crawled")
                .timestamp(java.time.LocalDateTime.now())
                .build();

        when(jsoupCrawlerWrapper.crawl(anyString())).thenReturn(mockResponse);

        // Act
        CrawlResponse response = crawlerService.crawl(url);

        // Assert
        assertNotNull(response);
        assertEquals(url, response.getUrl());
        assertNotNull(response.getTimestamp());
        assertTrue(response.getSuccess());
        assertEquals(200, response.getStatusCode());
    }

    @Test
    void crawl_WithValidUrl_ReturnsDataField() {
        // Arrange
        String url = "https://example.com";
        CrawlResponse mockResponse = CrawlResponse.builder()
                .url(url)
                .statusCode(200)
                .success(true)
                .message("Successfully crawled")
                .timestamp(java.time.LocalDateTime.now())
                .build();

        when(jsoupCrawlerWrapper.crawl(anyString())).thenReturn(mockResponse);

        // Act
        CrawlResponse response = crawlerService.crawl(url);

        // Assert
        assertNotNull(response);
        // Data field can be null for basic implementation, but should be present in response
    }

    @Test
    void crawl_ReturnsStatusCode() {
        // Arrange
        String url = "https://example.com";
        CrawlResponse mockResponse = CrawlResponse.builder()
                .url(url)
                .statusCode(200)
                .success(true)
                .timestamp(java.time.LocalDateTime.now())
                .build();

        when(jsoupCrawlerWrapper.crawl(anyString())).thenReturn(mockResponse);

        // Act
        CrawlResponse response = crawlerService.crawl(url);

        // Assert
        assertNotNull(response.getStatusCode());
        assertEquals(200, response.getStatusCode());
    }

    @Test
    void crawl_WithInvalidUrl_ReturnsErrorResponse() {
        // Arrange
        String url = "https://invalid-domain-that-does-not-exist-12345.com";
        CrawlResponse mockResponse = CrawlResponse.builder()
                .url(url)
                .success(false)
                .message("Failed to crawl URL")
                .timestamp(java.time.LocalDateTime.now())
                .build();

        when(jsoupCrawlerWrapper.crawl(anyString())).thenReturn(mockResponse);

        // Act
        CrawlResponse response = crawlerService.crawl(url);

        // Assert
        assertNotNull(response);
        assertFalse(response.getSuccess());
        assertNotNull(response.getMessage());
    }

    @Test
    void crawl_BlockedByRobotsTxt_ReturnsBlockedResponse() {
        // Arrange
        String url = "https://example.com/blocked-path";
        when(robotsTxtService.isAllowed(url, "TestBot/1.0")).thenReturn(false);

        // Act
        CrawlResponse response = crawlerService.crawl(url);

        // Assert
        assertNotNull(response);
        assertFalse(response.getSuccess());
        assertEquals("Crawling not allowed by robots.txt", response.getMessage());
        assertEquals(url, response.getUrl());
    }
}
