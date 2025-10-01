package com.webcrawler.service;

import crawlercommons.robots.BaseRobotRules;
import crawlercommons.robots.SimpleRobotRulesParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Implementation of RobotsTxtService using crawler-commons library
 */
@Service
@Slf4j
public class RobotsTxtServiceImpl implements RobotsTxtService {

    private final SimpleRobotRulesParser robotParser = new SimpleRobotRulesParser();

    @Override
    public boolean isAllowed(String url, String userAgent) {
        try {
            URI uri = new URI(url);
            String baseUrl = uri.getScheme() + "://" + uri.getHost();
            if (uri.getPort() != -1 && uri.getPort() != 80 && uri.getPort() != 443) {
                baseUrl += ":" + uri.getPort();
            }

            BaseRobotRules rules = getRobotRules(baseUrl, userAgent);
            boolean allowed = rules.isAllowed(url);

            if (allowed) {
                log.debug("Robots.txt check for URL: {} - Allowed", url);
            } else {
                log.info("Robots.txt check for URL: {} - BLOCKED by robots.txt", url);
            }
            return allowed;

        } catch (Exception e) {
            log.error("Error checking robots.txt for URL: {} - Error: {} - Blocking by default",
                     url, e.getMessage());
            // If we encounter an unexpected error, block crawling to be safe
            return false;
        }
    }

    @Cacheable("robotsTxt")
    private RobotsTxtResult fetchRobotsTxt(String baseUrl, String userAgent) {
        String robotsUrl = baseUrl + "/robots.txt";
        return fetchRobotsTxtFromUrl(robotsUrl, userAgent);
    }

    private RobotsTxtResult fetchRobotsTxtFromUrl(String robotsUrl, String userAgent) {
        try {
            URL url = new URL(robotsUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent", userAgent);
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.setInstanceFollowRedirects(false); // We'll handle redirects manually

            int statusCode = connection.getResponseCode();
            if (statusCode == HttpStatus.OK.value()) {
                byte[] content = connection.getInputStream().readAllBytes();
                String robotsTxt = new String(content, StandardCharsets.UTF_8);
                return new RobotsTxtResult(robotsTxt, statusCode, true);
            } else if (statusCode == HttpStatus.NOT_FOUND.value()) {
                log.debug("robots.txt not found for {} (404), allowing crawling", robotsUrl);
                return new RobotsTxtResult(null, statusCode, true);
            } else if (statusCode == HttpStatus.MOVED_PERMANENTLY.value() ||
                       statusCode == HttpStatus.FOUND.value() ||
                       statusCode == HttpStatus.TEMPORARY_REDIRECT.value() ||
                       statusCode == HttpStatus.PERMANENT_REDIRECT.value()) {
                // Redirect - try to follow it
                String redirectUrl = connection.getHeaderField("Location");
                if (redirectUrl != null && !redirectUrl.isEmpty()) {
                    log.debug("robots.txt redirect from {} to {}, following redirect", robotsUrl, redirectUrl);
                    return fetchRobotsTxtFromUrl(redirectUrl, userAgent);
                } else {
                    log.warn("robots.txt returned redirect {} for {} but no Location header, blocking", statusCode, robotsUrl);
                    return new RobotsTxtResult(null, statusCode, false);
                }
            } else {
                // 403, 401, 500, etc. - Cannot determine rules, should block
                log.warn("robots.txt returned {} for {}, blocking crawling to be safe", statusCode, robotsUrl);
                return new RobotsTxtResult(null, statusCode, false);
            }

        } catch (IOException e) {
            log.warn("Failed to fetch robots.txt from {}: {} - Blocking crawling to be safe", robotsUrl, e.getMessage());
            return new RobotsTxtResult(null, 0, false);
        }
    }

    /**
     * Result wrapper for robots.txt fetch operation
     */
    private static class RobotsTxtResult {
        final String content;
        final int statusCode;
        final boolean allowByDefault;

        RobotsTxtResult(String content, int statusCode, boolean allowByDefault) {
            this.content = content;
            this.statusCode = statusCode;
            this.allowByDefault = allowByDefault;
        }
    }

    /**
     * Fetches and parses robots.txt rules for a given base URL
     */
    @Cacheable("robotRules")
    private BaseRobotRules getRobotRules(String baseUrl, String userAgent) {
        try {
            RobotsTxtResult result = fetchRobotsTxt(baseUrl, userAgent);

            if (result.content == null || result.content.isEmpty()) {
                if (result.allowByDefault) {
                    // 404 - No robots.txt found, allow all
                    log.debug("No robots.txt found for {}, allowing all URLs", baseUrl);
                    return robotParser.failedFetch(HttpStatus.NOT_FOUND.value());
                } else {
                    // 403, 401, 5xx, or network error - Cannot determine, block all
                    log.warn("Cannot access robots.txt for {}, blocking all URLs", baseUrl);
                    // Create rules that block everything by parsing a restrictive robots.txt
                    String blockAllRobotsTxt = "User-agent: *\nDisallow: /";
                    byte[] blockAllContent = blockAllRobotsTxt.getBytes(StandardCharsets.UTF_8);
                    return robotParser.parseContent(
                        baseUrl + "/robots.txt",
                        blockAllContent,
                        "text/plain",
                        userAgent
                    );
                }
            }

            byte[] content = result.content.getBytes(StandardCharsets.UTF_8);
            return robotParser.parseContent(
                baseUrl + "/robots.txt",
                content,
                "text/plain",
                userAgent
            );

        } catch (Exception e) {
            log.warn("Error parsing robots.txt for {} - Error: {} - Blocking all URLs",
                    baseUrl, e.getMessage());
            // Create rules that block everything
            String blockAllRobotsTxt = "User-agent: *\nDisallow: /";
            byte[] blockAllContent = blockAllRobotsTxt.getBytes(StandardCharsets.UTF_8);
            return robotParser.parseContent(
                baseUrl + "/robots.txt",
                blockAllContent,
                "text/plain",
                userAgent
            );
        }
    }
}
