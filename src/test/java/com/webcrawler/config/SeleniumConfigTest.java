package com.webcrawler.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SeleniumConfigTest {

    @Test
    void testConfigCreation() {
        SeleniumConfig config = new SeleniumConfig();
        assertNotNull(config);
    }

    @Test
    void testSettersAndGetters() {
        SeleniumConfig config = new SeleniumConfig();

        config.setEnabled(false);
        config.setHeadless(false);
        config.setPageLoadTimeout(60);
        config.setImplicitWait(20);
        config.setJavascriptWaitTime(5000);

        assertFalse(config.isEnabled());
        assertFalse(config.isHeadless());
        assertEquals(60, config.getPageLoadTimeout());
        assertEquals(20, config.getImplicitWait());
        assertEquals(5000, config.getJavascriptWaitTime());
    }

    @Test
    void testEnabled() {
        SeleniumConfig config = new SeleniumConfig();
        config.setEnabled(true);
        assertTrue(config.isEnabled());

        config.setEnabled(false);
        assertFalse(config.isEnabled());
    }

    @Test
    void testHeadless() {
        SeleniumConfig config = new SeleniumConfig();
        config.setHeadless(true);
        assertTrue(config.isHeadless());

        config.setHeadless(false);
        assertFalse(config.isHeadless());
    }

    @Test
    void testPageLoadTimeout() {
        SeleniumConfig config = new SeleniumConfig();
        config.setPageLoadTimeout(45);
        assertEquals(45, config.getPageLoadTimeout());
    }

    @Test
    void testImplicitWait() {
        SeleniumConfig config = new SeleniumConfig();
        config.setImplicitWait(15);
        assertEquals(15, config.getImplicitWait());
    }

    @Test
    void testJavascriptWaitTime() {
        SeleniumConfig config = new SeleniumConfig();
        config.setJavascriptWaitTime(2000);
        assertEquals(2000, config.getJavascriptWaitTime());
    }
}
