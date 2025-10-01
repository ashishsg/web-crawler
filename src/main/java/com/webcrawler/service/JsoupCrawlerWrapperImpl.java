package com.webcrawler.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.webcrawler.config.JsoupConfig;
import com.webcrawler.model.CrawlResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.StringEscapeUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * Implementation of JsoupCrawlerWrapper using Jsoup library
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class JsoupCrawlerWrapperImpl implements JsoupCrawlerWrapper {

    private final JsoupConfig jsoupConfig;
    private final ObjectMapper objectMapper;

    @Override
    public CrawlResponse crawl(String url) {
        log.info("Starting crawl for URL: {}", url);

        try {
            // Execute HTTP request and parse HTML
            // Add headers to mimic real browser and avoid 403 Forbidden errors
            Connection.Response response = Jsoup.connect(url)
                    .userAgent(jsoupConfig.getUserAgent())
                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
                    .header("Accept-Language", "en-US,en;q=0.9")
                    .header("Accept-Encoding", "gzip, deflate, br")
                    .header("Upgrade-Insecure-Requests", "1")
                    .referrer("https://www.google.com/")
                    .timeout(jsoupConfig.getTimeout())
                    .followRedirects(jsoupConfig.isFollowRedirects())
                    .maxBodySize(jsoupConfig.getMaxBodySize())
                    .ignoreContentType(true)
                    .execute();

            Document document = response.parse();

            // Extract basic data and create JSON structure
            JsonNode data = extractBasicData(document);

            log.info("Successfully crawled URL: {} with status code: {}", url, response.statusCode());

            return CrawlResponse.builder()
                    .url(url)
                    .statusCode(response.statusCode())
                    .success(true)
                    .timestamp(LocalDateTime.now())
                    .message("Successfully crawled URL")
                    .data(data)
                    .build();

        } catch (IOException e) {
            log.error("Failed to crawl URL: {}", url, e);

            return CrawlResponse.builder()
                    .url(url)
                    .success(false)
                    .timestamp(LocalDateTime.now())
                    .message("Failed to crawl URL: " + e.getMessage())
                    .data(null)
                    .build();
        }
    }

    /**
     * Extracts basic data from the HTML document
     * This is a placeholder for the unified schema to be implemented later
     */
    private JsonNode extractBasicData(Document document) {
        ObjectNode dataNode = objectMapper.createObjectNode();

        // Basic extraction - to be replaced with unified schema
        // Use StringEscapeUtils to sanitize text and prevent JSON control character issues
        dataNode.put("title", sanitizeText(document.title()));
        dataNode.put("description", sanitizeText(getMetaContent(document, "description")));
        dataNode.put("textContent", sanitizeText(document.body() != null ? document.body().text() : ""));
        dataNode.put("htmlLength", document.html().length());

        return dataNode;
    }

    /**
     * Helper method to extract meta tag content
     */
    private String getMetaContent(Document document, String attribute) {
        var element = document.select("meta[name=" + attribute + "]").first();
        return element != null ? element.attr("content") : "";
    }

    /**
     * Sanitizes text using Apache Commons Text to escape JSON control characters
     * Prevents JSON parsing errors caused by unescaped control characters
     */
    private String sanitizeText(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        // Unescape first (in case already escaped), then re-escape properly for JSON
        return StringEscapeUtils.escapeJson(text);
    }
}
