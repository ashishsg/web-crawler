package com.webcrawler.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class CrawlResponseTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testBuilder() {
        LocalDateTime now = LocalDateTime.now();
        JsonNode data = objectMapper.createObjectNode();

        CrawlResponse response = CrawlResponse.builder()
            .url("https://example.com")
            .statusCode(200)
            .timestamp(now)
            .success(true)
            .message("Success")
            .data(data)
            .build();

        assertNotNull(response);
        assertEquals("https://example.com", response.getUrl());
        assertEquals(200, response.getStatusCode());
        assertEquals(now, response.getTimestamp());
        assertTrue(response.getSuccess());
        assertEquals("Success", response.getMessage());
        assertEquals(data, response.getData());
    }

    @Test
    void testNoArgsConstructor() {
        CrawlResponse response = new CrawlResponse();
        assertNotNull(response);
    }

    @Test
    void testAllArgsConstructor() {
        LocalDateTime now = LocalDateTime.now();
        JsonNode data = objectMapper.createObjectNode();
        ContentClassification classification = ContentClassification.builder()
            .categories(Arrays.asList(
                ContentClassification.CategoryScore.builder()
                    .category("news")
                    .confidence(0.9)
                    .build()
            ))
            .language("eng")
            .languageConfidence(0.95)
            .build();

        CrawlResponse response = new CrawlResponse(
            "https://example.com",
            200,
            now,
            true,
            "Success",
            data,
            classification
        );

        assertNotNull(response);
        assertEquals("https://example.com", response.getUrl());
        assertEquals(200, response.getStatusCode());
        assertEquals(now, response.getTimestamp());
        assertTrue(response.getSuccess());
        assertEquals("Success", response.getMessage());
        assertEquals(data, response.getData());
        assertEquals(classification, response.getClassification());
    }

    @Test
    void testSettersAndGetters() {
        CrawlResponse response = new CrawlResponse();
        LocalDateTime now = LocalDateTime.now();
        JsonNode data = objectMapper.createObjectNode();
        ContentClassification classification = ContentClassification.builder()
            .categories(Arrays.asList(
                ContentClassification.CategoryScore.builder()
                    .category("technology")
                    .confidence(0.85)
                    .build()
            ))
            .language("eng")
            .languageConfidence(0.92)
            .build();

        response.setUrl("https://test.com");
        response.setStatusCode(404);
        response.setTimestamp(now);
        response.setSuccess(false);
        response.setMessage("Not Found");
        response.setData(data);
        response.setClassification(classification);

        assertEquals("https://test.com", response.getUrl());
        assertEquals(404, response.getStatusCode());
        assertEquals(now, response.getTimestamp());
        assertFalse(response.getSuccess());
        assertEquals("Not Found", response.getMessage());
        assertEquals(data, response.getData());
        assertEquals(classification, response.getClassification());
    }

    @Test
    void testSuccessResponse() {
        CrawlResponse response = CrawlResponse.builder()
            .url("https://example.com")
            .statusCode(200)
            .timestamp(LocalDateTime.now())
            .success(true)
            .message("Successfully crawled")
            .build();

        assertTrue(response.getSuccess());
        assertEquals(200, response.getStatusCode());
    }

    @Test
    void testErrorResponse() {
        CrawlResponse response = CrawlResponse.builder()
            .url("https://example.com")
            .success(false)
            .timestamp(LocalDateTime.now())
            .message("Failed to crawl")
            .build();

        assertFalse(response.getSuccess());
        assertNull(response.getStatusCode());
        assertNull(response.getData());
    }

    @Test
    void testResponseWithClassification() {
        ContentClassification classification = ContentClassification.builder()
            .categories(Arrays.asList(
                ContentClassification.CategoryScore.builder()
                    .category("ecommerce")
                    .confidence(0.77)
                    .build(),
                ContentClassification.CategoryScore.builder()
                    .category("technology")
                    .confidence(0.15)
                    .build()
            ))
            .language("eng")
            .languageConfidence(0.98)
            .build();

        CrawlResponse response = CrawlResponse.builder()
            .url("https://shop.example.com")
            .statusCode(200)
            .success(true)
            .timestamp(LocalDateTime.now())
            .message("Success")
            .classification(classification)
            .build();

        assertNotNull(response.getClassification());
        assertEquals(2, response.getClassification().getCategories().size());
        assertEquals("ecommerce", response.getClassification().getCategories().get(0).getCategory());
        assertEquals("eng", response.getClassification().getLanguage());
    }
}
