package com.webcrawler.service;

import com.webcrawler.config.SeleniumConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.quality.Strictness;
import org.mockito.junit.jupiter.MockitoSettings;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class JavaScriptRendererImplTest {

    @Mock
    private SeleniumConfig seleniumConfig;

    @InjectMocks
    private JavaScriptRendererImpl service;

    @BeforeEach
    void setUp() {
        lenient().when(seleniumConfig.isEnabled()).thenReturn(false);
        lenient().when(seleniumConfig.isHeadless()).thenReturn(true);
        lenient().when(seleniumConfig.getPageLoadTimeout()).thenReturn(30);
        lenient().when(seleniumConfig.getImplicitWait()).thenReturn(10);
        lenient().when(seleniumConfig.getJavascriptWaitTime()).thenReturn(3000);
    }

    @Test
    void testInitWhenDisabled() {
        service.init();
        assertFalse(service.isAvailable());
    }

    @Test
    void testIsAvailableWhenDisabled() {
        service.init();
        assertFalse(service.isAvailable());
    }

    @Test
    void testRenderPageWhenNotAvailable() {
        service.init();

        Exception exception = assertThrows(IllegalStateException.class, () -> {
            service.renderPage("https://example.com");
        });

        assertEquals("JavaScript rendering is not available", exception.getMessage());
    }

    @Test
    void testCleanup() {
        service.init();
        // Cleanup should not throw exception even when driver is null
        assertDoesNotThrow(() -> service.cleanup());
    }

    @Test
    void testInitWithFailure() {
        // When initialization fails, should set available to false
        service.init();
        assertFalse(service.isAvailable());
    }

    @Test
    void testMultipleCleanupCalls() {
        service.init();
        assertDoesNotThrow(() -> {
            service.cleanup();
            service.cleanup(); // Second call should also not throw
        });
    }
}
