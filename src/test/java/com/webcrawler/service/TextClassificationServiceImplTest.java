package com.webcrawler.service;

import com.webcrawler.model.ContentClassification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TextClassificationServiceImplTest {

    private TextClassificationServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new TextClassificationServiceImpl();
        service.init();
    }

    @Test
    void testClassifyNewsContent() {
        String title = "Breaking News: President announces new policy";
        String description = "Government officials report major announcement";
        String content = "CNN reports that the president made an official statement today according to sources.";

        ContentClassification result = service.classify(title, description, content);

        assertNotNull(result);
        assertNotNull(result.getCategories());
        assertFalse(result.getCategories().isEmpty());
        assertEquals("news", result.getCategories().get(0).getCategory());
        assertTrue(result.getCategories().get(0).getConfidence() > 0);
        assertEquals("eng", result.getLanguage());
    }

    @Test
    void testClassifyEcommerceContent() {
        String title = "Buy Amazing Toaster - Free Shipping";
        String description = "Shop now and get the best price on this product";
        String content = "Add to cart and checkout today. Customer reviews show 5 star ratings. Buy now with free shipping!";

        ContentClassification result = service.classify(title, description, content);

        assertNotNull(result);
        assertNotNull(result.getCategories());
        assertFalse(result.getCategories().isEmpty());
        assertEquals("ecommerce", result.getCategories().get(0).getCategory());
        assertTrue(result.getCategories().get(0).getConfidence() > 0);
    }

    @Test
    void testClassifyTechnologyContent() {
        String title = "New Software Update Released";
        String description = "Latest API improvements for developers";
        String content = "The new software update includes improvements to the algorithm and machine learning features. Developers can now access the cloud database through the API.";

        ContentClassification result = service.classify(title, description, content);

        assertNotNull(result);
        assertNotNull(result.getCategories());
        assertFalse(result.getCategories().isEmpty());
        assertEquals("technology", result.getCategories().get(0).getCategory());
        assertTrue(result.getCategories().get(0).getConfidence() > 0);
    }

    @Test
    void testClassifySportsContent() {
        String title = "Team Wins Championship Game";
        String description = "Final score shows victory for home team";
        String content = "The match ended with the team winning the tournament. The player scored the winning goal in the championship game.";

        ContentClassification result = service.classify(title, description, content);

        assertNotNull(result);
        assertNotNull(result.getCategories());
        assertFalse(result.getCategories().isEmpty());
        assertEquals("sports", result.getCategories().get(0).getCategory());
        assertTrue(result.getCategories().get(0).getConfidence() > 0);
    }

    @Test
    void testClassifyEmptyContent() {
        ContentClassification result = service.classify("", "", "");

        assertNotNull(result);
        assertNotNull(result.getCategories());
        assertEquals(1, result.getCategories().size());
        assertEquals("general", result.getCategories().get(0).getCategory());
        assertEquals(0.0, result.getCategories().get(0).getConfidence());
        assertEquals("unknown", result.getLanguage());
    }

    @Test
    void testClassifyNullValues() {
        ContentClassification result = service.classify(null, null, null);

        assertNotNull(result);
        assertNotNull(result.getCategories());
        assertEquals(1, result.getCategories().size());
        assertEquals("general", result.getCategories().get(0).getCategory());
        assertEquals(0.0, result.getCategories().get(0).getConfidence());
    }

    @Test
    void testClassifyMultipleCategories() {
        String title = "Health Tech Startup Raises Funds";
        String description = "New medical software for healthcare providers";
        String content = "The health technology company developed new software for medical diagnosis and treatment. The app uses AI algorithms for healthcare providers in hospitals.";

        ContentClassification result = service.classify(title, description, content);

        assertNotNull(result);
        assertNotNull(result.getCategories());
        // Should have multiple categories (health + technology)
        assertTrue(result.getCategories().size() >= 2);
        assertTrue(result.getCategories().size() <= 3);

        // Verify all categories have valid confidence scores
        for (ContentClassification.CategoryScore category : result.getCategories()) {
            assertNotNull(category.getCategory());
            assertTrue(category.getConfidence() > 0);
            assertTrue(category.getConfidence() <= 1.0);
        }
    }

    @Test
    void testLanguageDetection() {
        String title = "Test Article";
        String description = "This is a test description in English";
        String content = "This is sample content written in the English language for testing purposes.";

        ContentClassification result = service.classify(title, description, content);

        assertNotNull(result);
        assertEquals("eng", result.getLanguage());
        assertTrue(result.getLanguageConfidence() > 0);
    }

    @Test
    void testClassifyEntertainmentContent() {
        String title = "New Movie Release";
        String description = "Famous actor stars in latest film";
        String content = "The movie features a celebrity actress and was directed by a Hollywood director. The album and music for the film was composed by an award-winning artist.";

        ContentClassification result = service.classify(title, description, content);

        assertNotNull(result);
        assertNotNull(result.getCategories());
        assertFalse(result.getCategories().isEmpty());
        assertEquals("entertainment", result.getCategories().get(0).getCategory());
    }

    @Test
    void testClassifyHealthContent() {
        String title = "New Medical Treatment Approved";
        String description = "Doctors report success with new therapy";
        String content = "The hospital announced a new treatment for patients. The medical diagnosis and therapy show promise according to healthcare providers.";

        ContentClassification result = service.classify(title, description, content);

        assertNotNull(result);
        assertNotNull(result.getCategories());
        assertFalse(result.getCategories().isEmpty());
        assertEquals("health", result.getCategories().get(0).getCategory());
    }

    @Test
    void testClassifyFinanceContent() {
        String title = "Stock Market Update";
        String description = "Investment trends show economic growth";
        String content = "The stock market and trading volume increased today. Business revenue and profit margins improved for major corporations. Interest rates remain stable according to the bank.";

        ContentClassification result = service.classify(title, description, content);

        assertNotNull(result);
        assertNotNull(result.getCategories());
        assertFalse(result.getCategories().isEmpty());
        assertEquals("finance", result.getCategories().get(0).getCategory());
    }

    @Test
    void testClassifyEducationContent() {
        String title = "University Launches New Program";
        String description = "Students can now enroll in innovative courses";
        String content = "The school and college are offering new academic programs. Teachers and professors will provide lectures for students pursuing their degree.";

        ContentClassification result = service.classify(title, description, content);

        assertNotNull(result);
        assertNotNull(result.getCategories());
        assertFalse(result.getCategories().isEmpty());
        assertEquals("education", result.getCategories().get(0).getCategory());
    }
}
