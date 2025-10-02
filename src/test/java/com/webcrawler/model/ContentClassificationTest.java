package com.webcrawler.model;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ContentClassificationTest {

    @Test
    void testBuilder() {
        List<ContentClassification.CategoryScore> categories = Arrays.asList(
            ContentClassification.CategoryScore.builder()
                .category("news")
                .confidence(0.9)
                .build()
        );

        ContentClassification classification = ContentClassification.builder()
            .categories(categories)
            .language("eng")
            .languageConfidence(0.95)
            .build();

        assertNotNull(classification);
        assertEquals(categories, classification.getCategories());
        assertEquals("eng", classification.getLanguage());
        assertEquals(0.95, classification.getLanguageConfidence());
    }

    @Test
    void testNoArgsConstructor() {
        ContentClassification classification = new ContentClassification();
        assertNotNull(classification);
    }

    @Test
    void testAllArgsConstructor() {
        List<ContentClassification.CategoryScore> categories = Arrays.asList(
            ContentClassification.CategoryScore.builder()
                .category("technology")
                .confidence(0.8)
                .build()
        );

        ContentClassification classification = new ContentClassification(
            categories,
            "eng",
            0.92
        );

        assertNotNull(classification);
        assertEquals(categories, classification.getCategories());
        assertEquals("eng", classification.getLanguage());
        assertEquals(0.92, classification.getLanguageConfidence());
    }

    @Test
    void testSettersAndGetters() {
        ContentClassification classification = new ContentClassification();

        List<ContentClassification.CategoryScore> categories = Arrays.asList(
            ContentClassification.CategoryScore.builder()
                .category("sports")
                .confidence(0.75)
                .build()
        );

        classification.setCategories(categories);
        classification.setLanguage("spa");
        classification.setLanguageConfidence(0.88);

        assertEquals(categories, classification.getCategories());
        assertEquals("spa", classification.getLanguage());
        assertEquals(0.88, classification.getLanguageConfidence());
    }

    @Test
    void testCategoryScoreBuilder() {
        ContentClassification.CategoryScore categoryScore = ContentClassification.CategoryScore.builder()
            .category("ecommerce")
            .confidence(0.85)
            .build();

        assertNotNull(categoryScore);
        assertEquals("ecommerce", categoryScore.getCategory());
        assertEquals(0.85, categoryScore.getConfidence());
    }

    @Test
    void testCategoryScoreNoArgsConstructor() {
        ContentClassification.CategoryScore categoryScore = new ContentClassification.CategoryScore();
        assertNotNull(categoryScore);
    }

    @Test
    void testCategoryScoreAllArgsConstructor() {
        ContentClassification.CategoryScore categoryScore = new ContentClassification.CategoryScore(
            "finance",
            0.77
        );

        assertNotNull(categoryScore);
        assertEquals("finance", categoryScore.getCategory());
        assertEquals(0.77, categoryScore.getConfidence());
    }

    @Test
    void testCategoryScoreSettersAndGetters() {
        ContentClassification.CategoryScore categoryScore = new ContentClassification.CategoryScore();

        categoryScore.setCategory("health");
        categoryScore.setConfidence(0.91);

        assertEquals("health", categoryScore.getCategory());
        assertEquals(0.91, categoryScore.getConfidence());
    }

    @Test
    void testMultipleCategories() {
        List<ContentClassification.CategoryScore> categories = Arrays.asList(
            ContentClassification.CategoryScore.builder()
                .category("news")
                .confidence(0.9)
                .build(),
            ContentClassification.CategoryScore.builder()
                .category("politics")
                .confidence(0.7)
                .build(),
            ContentClassification.CategoryScore.builder()
                .category("general")
                .confidence(0.3)
                .build()
        );

        ContentClassification classification = ContentClassification.builder()
            .categories(categories)
            .language("eng")
            .languageConfidence(0.98)
            .build();

        assertEquals(3, classification.getCategories().size());
        assertEquals("news", classification.getCategories().get(0).getCategory());
        assertEquals("politics", classification.getCategories().get(1).getCategory());
        assertEquals("general", classification.getCategories().get(2).getCategory());
    }
}
