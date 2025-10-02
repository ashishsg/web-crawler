package com.webcrawler.service;

import com.webcrawler.model.ContentClassification;

/**
 * Service interface for text classification and language detection
 */
public interface TextClassificationService {

    /**
     * Classifies the given text content
     *
     * @param title the title of the content
     * @param description the description of the content
     * @param textContent the main text content
     * @return classification result with category, language, and confidence scores
     */
    ContentClassification classify(String title, String description, String textContent);
}
