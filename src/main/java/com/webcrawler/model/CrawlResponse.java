package com.webcrawler.model;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response model containing crawl results")
public class CrawlResponse {

    @Schema(description = "The URL that was crawled", example = "https://example.com")
    private String url;

    @Schema(description = "HTTP status code received", example = "200")
    private Integer statusCode;

    @Schema(description = "Timestamp when the crawl was performed")
    private LocalDateTime timestamp;

    @Schema(description = "Success indicator")
    private Boolean success;

    @Schema(description = "Message or error description")
    private String message;

    @Schema(description = "Parsed crawler result in unified schema format")
    private JsonNode data;
}
