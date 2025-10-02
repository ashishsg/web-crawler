package com.webcrawler.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for Selenium WebDriver
 */
@Configuration
@ConfigurationProperties(prefix = "selenium")
@Data
public class SeleniumConfig {

    /**
     * Enable JavaScript rendering (requires ChromeDriver)
     */
    private boolean enabled = true;

    /**
     * Run browser in headless mode
     */
    private boolean headless = true;

    /**
     * Page load timeout in seconds
     */
    private int pageLoadTimeout = 30;

    /**
     * Implicit wait timeout in seconds
     */
    private int implicitWait = 10;

    /**
     * Additional wait time for JavaScript to execute (milliseconds)
     */
    private int javascriptWaitTime = 3000;
}
