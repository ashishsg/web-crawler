package com.webcrawler.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CrawlRequestTest {

    @Test
    void testNoArgsConstructor() {
        CrawlRequest request = new CrawlRequest();
        assertNotNull(request);
    }

    @Test
    void testAllArgsConstructor() {
        CrawlRequest request = new CrawlRequest("https://example.com");

        assertNotNull(request);
        assertEquals("https://example.com", request.getUrl());
    }

    @Test
    void testSetterAndGetter() {
        CrawlRequest request = new CrawlRequest();

        request.setUrl("https://test.com");

        assertEquals("https://test.com", request.getUrl());
    }

    @Test
    void testConstructorWithUrl() {
        CrawlRequest request = new CrawlRequest("https://example.com/test");

        assertNotNull(request);
        assertEquals("https://example.com/test", request.getUrl());
    }

    @Test
    void testNullUrl() {
        CrawlRequest request = new CrawlRequest();
        assertNull(request.getUrl());
    }

    @Test
    void testEmptyUrl() {
        CrawlRequest request = new CrawlRequest("");
        assertEquals("", request.getUrl());
    }

    @Test
    void testUrlWithQueryParameters() {
        String url = "https://example.com/test?param1=value1&param2=value2";
        CrawlRequest request = new CrawlRequest(url);

        assertEquals(url, request.getUrl());
    }

    @Test
    void testUrlWithFragment() {
        String url = "https://example.com/test#section";
        CrawlRequest request = new CrawlRequest(url);

        assertEquals(url, request.getUrl());
    }

    @Test
    void testComplexUrl() {
        String url = "https://example.com:8080/path/to/resource?key=value#fragment";
        CrawlRequest request = new CrawlRequest(url);

        assertEquals(url, request.getUrl());
    }
}
