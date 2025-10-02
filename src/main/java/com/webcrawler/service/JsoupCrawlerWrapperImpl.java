package com.webcrawler.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.webcrawler.config.JsoupConfig;
import com.webcrawler.model.CrawlResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final TextClassificationService textClassificationService;
    private final JavaScriptRenderer javaScriptRenderer;

    @Override
    public CrawlResponse crawl(String url) {
        log.info("Starting crawl for URL: {}", url);

        try {
            Document document;
            int statusCode = 200;
            boolean usedJavaScriptRenderer = false;

            // Try regular Jsoup first
            try {
                // Execute HTTP request and parse HTML
                // Add headers to mimic real browser and avoid 403 Forbidden errors
                // Note: Jsoup handles gzip decompression automatically, don't set Accept-Encoding manually
                Connection.Response response = Jsoup.connect(url)
                        .userAgent(jsoupConfig.getUserAgent())
                        .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
                        .header("Accept-Language", "en-US,en;q=0.9")
                        .header("Upgrade-Insecure-Requests", "1")
                        .referrer("https://www.google.com/")
                        .timeout(jsoupConfig.getTimeout())
                        .followRedirects(jsoupConfig.isFollowRedirects())
                        .maxBodySize(jsoupConfig.getMaxBodySize())
                        .ignoreContentType(true)
                        .execute();

                statusCode = response.statusCode();
                document = response.parse();

                // Check if page has minimal content (likely JavaScript-rendered)
                String bodyText = document.body() != null ? document.body().text() : "";
                if (bodyText.trim().isEmpty() && javaScriptRenderer.isAvailable()) {
                    log.info("Page appears to be JavaScript-rendered, using headless browser");
                    String renderedHtml = javaScriptRenderer.renderPage(url);
                    document = Jsoup.parse(renderedHtml);
                    usedJavaScriptRenderer = true;
                }

            } catch (IOException e) {
                // If regular crawling fails and JS renderer is available, try with it
                if (javaScriptRenderer.isAvailable()) {
                    log.info("Regular crawling failed, trying with JavaScript renderer");
                    String renderedHtml = javaScriptRenderer.renderPage(url);
                    document = Jsoup.parse(renderedHtml);
                    usedJavaScriptRenderer = true;
                } else {
                    throw e;
                }
            }

            if (usedJavaScriptRenderer) {
                log.info("Successfully rendered page with JavaScript");
            }

            // Extract basic data and create JSON structure
            JsonNode data = extractBasicData(document);

            // Extract text for classification
            String title = document.title();
            String description = getMetaContent(document, "description");
            String textContent = document.body() != null ? document.body().text() : "";

            // Classify the content
            var classification = textClassificationService.classify(title, description, textContent);

            log.info("Successfully crawled URL: {} with status code: {}", url, statusCode);

            return CrawlResponse.builder()
                    .url(url)
                    .statusCode(statusCode)
                    .success(true)
                    .timestamp(LocalDateTime.now())
                    .message(usedJavaScriptRenderer ? "Successfully crawled URL with JavaScript rendering" : "Successfully crawled URL")
                    .data(data)
                    .classification(classification)
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
        // Jackson handles JSON escaping automatically, no need for manual sanitization
        dataNode.put("title", document.title());
        dataNode.put("description", getMetaContent(document, "description"));
        dataNode.put("textContent", document.body() != null ? document.body().text() : "");
        dataNode.put("htmlLength", document.html().length());

        // Extract H1 tags
        var h1Tags = objectMapper.createArrayNode();
        document.select("h1").forEach(h1 -> h1Tags.add(h1.text()));
        dataNode.set("h1Tags", h1Tags);

        return dataNode;
    }

    /**
     * Helper method to extract meta tag content
     */
    private String getMetaContent(Document document, String attribute) {
        var element = document.select("meta[name=" + attribute + "]").first();
        return element != null ? element.attr("content") : "";
    }
}
