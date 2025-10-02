package com.webcrawler.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Model for content classification result
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContentClassification {

    /**
     * Top categories (up to 3) with their confidence scores
     */
    private List<CategoryScore> categories;

    /**
     * Detected language code (e.g., "en", "es", "fr")
     */
    private String language;

    /**
     * Language detection confidence (0.0 to 1.0)
     */
    private Double languageConfidence;

    /**
     * Individual category score
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryScore {
        /**
         * Category name
         */
        private String category;

        /**
         * Confidence score (0.0 to 1.0)
         */
        private Double confidence;
    }
}
