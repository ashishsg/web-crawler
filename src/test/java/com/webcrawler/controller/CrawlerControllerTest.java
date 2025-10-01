package com.webcrawler.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webcrawler.model.CrawlRequest;
import com.webcrawler.model.CrawlResponse;
import com.webcrawler.service.CrawlerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CrawlerController.class)
class CrawlerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CrawlerService crawlerService;

    @Test
    void crawl_WithValidUrl_ReturnsSuccessResponse() throws Exception {
        // Arrange
        CrawlRequest request = new CrawlRequest("https://example.com");
        CrawlResponse response = CrawlResponse.builder()
                .url("https://example.com")
                .statusCode(200)
                .success(true)
                .timestamp(LocalDateTime.now())
                .message("Crawled successfully")
                .build();

        when(crawlerService.crawl(anyString())).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/v1/crawler/crawl")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.url").value("https://example.com"))
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void crawl_WithInvalidUrl_ReturnsBadRequest() throws Exception {
        // Arrange
        CrawlRequest request = new CrawlRequest("invalid-url");

        // Act & Assert
        mockMvc.perform(post("/api/v1/crawler/crawl")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void crawl_WithEmptyUrl_ReturnsBadRequest() throws Exception {
        // Arrange
        CrawlRequest request = new CrawlRequest("");

        // Act & Assert
        mockMvc.perform(post("/api/v1/crawler/crawl")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
