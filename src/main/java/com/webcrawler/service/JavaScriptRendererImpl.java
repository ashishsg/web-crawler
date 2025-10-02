package com.webcrawler.service;

import com.webcrawler.config.SeleniumConfig;
import io.github.bonigarcia.wdm.WebDriverManager;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Implementation of JavaScriptRenderer using Selenium WebDriver
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class JavaScriptRendererImpl implements JavaScriptRenderer {

    private final SeleniumConfig seleniumConfig;
    private final ReentrantLock driverLock = new ReentrantLock();
    private WebDriver driver;
    private boolean available = false;

    @PostConstruct
    public void init() {
        if (!seleniumConfig.isEnabled()) {
            log.info("JavaScript rendering is disabled");
            return;
        }

        try {
            // Setup ChromeDriver automatically
            WebDriverManager.chromedriver().setup();

            // Create Chrome options
            ChromeOptions options = new ChromeOptions();

            if (seleniumConfig.isHeadless()) {
                options.addArguments("--headless=new");
            }

            // Additional options for better performance and stability
            options.addArguments("--disable-gpu");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--disable-blink-features=AutomationControlled");
            options.addArguments("--disable-extensions");
            options.addArguments("--disable-images"); // Speed up by not loading images
            options.addArguments("--blink-settings=imagesEnabled=false");
            options.addArguments("--user-agent=" + getUserAgent());
            options.setPageLoadStrategy(org.openqa.selenium.PageLoadStrategy.EAGER); // Don't wait for all resources

            // Initialize driver
            driver = new ChromeDriver(options);
            driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(seleniumConfig.getPageLoadTimeout()));
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(seleniumConfig.getImplicitWait()));

            available = true;
            log.info("JavaScript rendering initialized successfully with ChromeDriver");

        } catch (Exception e) {
            log.warn("Failed to initialize JavaScript rendering: {} - Dynamic content rendering will be disabled", e.getMessage());
            available = false;
            driver = null;
        }
    }

    @PreDestroy
    public void cleanup() {
        if (driver != null) {
            try {
                driver.quit();
                log.info("ChromeDriver closed successfully");
            } catch (Exception e) {
                log.warn("Error closing ChromeDriver: {}", e.getMessage());
            }
        }
    }

    @Override
    public String renderPage(String url) {
        if (!available || driver == null) {
            throw new IllegalStateException("JavaScript rendering is not available");
        }

        driverLock.lock();
        try {
            log.info("Rendering page with JavaScript: {}", url);

            // Use JavascriptExecutor to stop page load after DOM is ready
            try {
                driver.get(url);
            } catch (org.openqa.selenium.TimeoutException e) {
                // Page load timeout - try to get content anyway as DOM might be ready
                log.warn("Page load timeout for {}, attempting to retrieve content anyway", url);
            }

            // Wait for JavaScript to execute
            Thread.sleep(seleniumConfig.getJavascriptWaitTime());

            // Check if document is ready
            try {
                org.openqa.selenium.JavascriptExecutor js = (org.openqa.selenium.JavascriptExecutor) driver;
                String readyState = (String) js.executeScript("return document.readyState");
                log.debug("Document ready state: {}", readyState);
            } catch (Exception e) {
                log.debug("Could not check document ready state: {}", e.getMessage());
            }

            String html = driver.getPageSource();
            log.info("Successfully rendered page, HTML length: {}", html.length());

            return html;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("JavaScript rendering interrupted", e);
        } catch (Exception e) {
            log.error("Failed to render page with JavaScript: {} - {}", url, e.getMessage());
            throw new RuntimeException("Failed to render page: " + e.getMessage(), e);
        } finally {
            driverLock.unlock();
        }
    }

    @Override
    public boolean isAvailable() {
        return available;
    }

    private String getUserAgent() {
        return "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36";
    }
}
