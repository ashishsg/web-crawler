package com.webcrawler.controller;

import com.webcrawler.model.CrawlRequest;
import com.webcrawler.model.CrawlResponse;
import com.webcrawler.service.CrawlerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/crawler")
@RequiredArgsConstructor
@Tag(name = "Web Crawler", description = "API for crawling web pages")
public class CrawlerController {

    private final CrawlerService crawlerService;

    @PostMapping("/crawl")
    @Operation(summary = "Crawl a URL", description = "Crawls the specified URL and returns parsed content in unified schema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully crawled the URL",
                    content = @Content(schema = @Schema(implementation = CrawlResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid URL format"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<CrawlResponse> crawl(@Valid @RequestBody CrawlRequest request) {
        CrawlResponse response = crawlerService.crawl(request.getUrl());
        return ResponseEntity.ok(response);
    }
}
