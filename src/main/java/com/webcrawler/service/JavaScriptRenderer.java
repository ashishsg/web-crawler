package com.webcrawler.service;

/**
 * Service interface for rendering JavaScript-heavy pages
 */
public interface JavaScriptRenderer {

    /**
     * Renders a page with JavaScript execution enabled
     *
     * @param url the URL to render
     * @return the fully rendered HTML after JavaScript execution
     */
    String renderPage(String url);

    /**
     * Checks if JavaScript rendering is available
     *
     * @return true if JS rendering is enabled and available
     */
    boolean isAvailable();
}
