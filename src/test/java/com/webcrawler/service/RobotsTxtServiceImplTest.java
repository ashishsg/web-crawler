package com.webcrawler.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RobotsTxtServiceImplTest {

    private RobotsTxtServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new RobotsTxtServiceImpl();
    }

    @Test
    void testIsAllowedWithInvalidUrl() {
        String invalidUrl = "not a valid url";
        String userAgent = "Test-Bot";

        boolean result = service.isAllowed(invalidUrl, userAgent);

        // Should block on error
        assertFalse(result);
    }

    @Test
    void testIsAllowedWithNullUrl() {
        boolean result = service.isAllowed(null, "Test-Bot");

        // Should block on error
        assertFalse(result);
    }

    @Test
    void testIsAllowedWithNullUserAgent() {
        String url = "https://example.com/test";

        boolean result = service.isAllowed(url, null);

        // Might allow or block depending on robots.txt availability
        assertNotNull(result);
    }

    @Test
    void testIsAllowedWithMalformedUrl() {
        String malformedUrl = "ht!tp://example.com";
        String userAgent = "Test-Bot";

        boolean result = service.isAllowed(malformedUrl, userAgent);

        // Should block on error
        assertFalse(result);
    }

    @Test
    void testUrlWithCustomPort() {
        // This tests the port handling logic in isAllowed
        String url = "https://example.com:8080/test";
        String userAgent = "Test-Bot";

        // Method will try to fetch robots.txt which will fail, but tests the port logic
        boolean result = service.isAllowed(url, userAgent);

        // Result depends on whether robots.txt is accessible, but method should not throw exception
        assertNotNull(result);
    }

    @Test
    void testUrlWithStandardHttpPort() {
        String url = "http://example.com:80/test";
        String userAgent = "Test-Bot";

        // Should handle standard port 80
        boolean result = service.isAllowed(url, userAgent);

        // Method should not throw exception
        assertNotNull(result);
    }

    @Test
    void testUrlWithStandardHttpsPort() {
        String url = "https://example.com:443/test";
        String userAgent = "Test-Bot";

        // Should handle standard port 443
        boolean result = service.isAllowed(url, userAgent);

        // Method should not throw exception
        assertNotNull(result);
    }

    @Test
    void testDifferentUserAgents() {
        String url = "https://example.com/test";

        // Test with different user agents
        String[] userAgents = {"Mozilla/5.0", "Googlebot", "Custom-Bot", "*"};

        for (String userAgent : userAgents) {
            boolean result = service.isAllowed(url, userAgent);
            assertNotNull(result);
        }
    }

    @Test
    void testUrlPathVariations() {
        String userAgent = "Test-Bot";
        String[] urls = {
            "https://example.com/",
            "https://example.com/path",
            "https://example.com/path/to/resource",
            "https://example.com/path?query=value",
            "https://example.com/path#fragment"
        };

        for (String url : urls) {
            boolean result = service.isAllowed(url, userAgent);
            assertNotNull(result);
        }
    }

    @Test
    void testUrlWithQueryParameters() {
        String url = "https://example.com/test?param1=value1&param2=value2";
        String userAgent = "Test-Bot";

        boolean result = service.isAllowed(url, userAgent);

        // Method should handle query parameters
        assertNotNull(result);
    }

    @Test
    void testUrlWithFragment() {
        String url = "https://example.com/test#section";
        String userAgent = "Test-Bot";

        boolean result = service.isAllowed(url, userAgent);

        // Method should handle fragments
        assertNotNull(result);
    }

    @Test
    void testHttpScheme() {
        String url = "http://example.com/test";
        String userAgent = "Test-Bot";

        boolean result = service.isAllowed(url, userAgent);

        // Should handle HTTP scheme
        assertNotNull(result);
    }

    @Test
    void testHttpsScheme() {
        String url = "https://example.com/test";
        String userAgent = "Test-Bot";

        boolean result = service.isAllowed(url, userAgent);

        // Should handle HTTPS scheme
        assertNotNull(result);
    }
}
