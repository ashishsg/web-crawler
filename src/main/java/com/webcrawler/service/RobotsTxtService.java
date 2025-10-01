package com.webcrawler.service;

/**
 * Service interface for robots.txt validation
 */
public interface RobotsTxtService {

    /**
     * Checks if crawling is allowed for the given URL according to robots.txt
     *
     * @param url the URL to check
     * @param userAgent the user agent string
     * @return true if crawling is allowed, false otherwise
     */
    boolean isAllowed(String url, String userAgent);

    /**
     * Gets the robots.txt rules for a given domain
     * Note: This method is internal and returns implementation-specific result type
     *
     * @param baseUrl the base URL of the domain
     * @param userAgent the user agent string
     * @return robots.txt content or null if not found
     */
    default String getRobotsTxt(String baseUrl, String userAgent) {
        return null;
    }
}
