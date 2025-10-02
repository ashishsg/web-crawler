package com.webcrawler.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JsoupConfigTest {

    @Test
    void testConfigCreation() {
        JsoupConfig config = new JsoupConfig();
        assertNotNull(config);
    }

    @Test
    void testGetters() {
        JsoupConfig config = new JsoupConfig();

        // Getters should not throw exceptions
        assertDoesNotThrow(() -> config.getUserAgent());
        assertDoesNotThrow(() -> config.getTimeout());
        assertDoesNotThrow(() -> config.isFollowRedirects());
        assertDoesNotThrow(() -> config.getMaxBodySize());
    }
}
