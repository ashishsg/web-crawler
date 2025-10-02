package com.webcrawler.service;

import com.webcrawler.model.ContentClassification;
import lombok.extern.slf4j.Slf4j;
import opennlp.tools.langdetect.Language;
import opennlp.tools.langdetect.LanguageDetector;
import opennlp.tools.langdetect.LanguageDetectorME;
import opennlp.tools.langdetect.LanguageDetectorModel;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.InputStream;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Implementation of TextClassificationService using OpenNLP and keyword-based classification
 */
@Service
@Slf4j
public class TextClassificationServiceImpl implements TextClassificationService {

    private LanguageDetector languageDetector;

    // Category keywords for classification
    private static final Map<String, List<Pattern>> CATEGORY_PATTERNS = new HashMap<>();

    static {
        // E-commerce / Shopping
        CATEGORY_PATTERNS.put("ecommerce", Arrays.asList(
            Pattern.compile("\\b(buy|purchase|cart|checkout|price|shipping|product|seller|amazon|ebay|shop)\\b", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\b(add to cart|buy now|free shipping|customer review|rating)\\b", Pattern.CASE_INSENSITIVE)
        ));

        // News / Politics
        CATEGORY_PATTERNS.put("news", Arrays.asList(
            Pattern.compile("\\b(breaking|report|announced|statement|government|president|minister|official|sources say)\\b", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\b(according to|reuters|cnn|bbc|news|politics|election)\\b", Pattern.CASE_INSENSITIVE)
        ));

        // Technology
        CATEGORY_PATTERNS.put("technology", Arrays.asList(
            Pattern.compile("\\b(software|hardware|app|application|developer|code|programming|algorithm|AI|machine learning)\\b", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\b(tech|technology|digital|cyber|cloud|database|API)\\b", Pattern.CASE_INSENSITIVE)
        ));

        // Sports
        CATEGORY_PATTERNS.put("sports", Arrays.asList(
            Pattern.compile("\\b(game|match|tournament|championship|team|player|score|goal|win|loss)\\b", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\b(football|basketball|baseball|soccer|tennis|cricket|olympics)\\b", Pattern.CASE_INSENSITIVE)
        ));

        // Entertainment
        CATEGORY_PATTERNS.put("entertainment", Arrays.asList(
            Pattern.compile("\\b(movie|film|actor|actress|director|music|concert|album|song|artist)\\b", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\b(entertainment|celebrity|hollywood|netflix|streaming|episode|season)\\b", Pattern.CASE_INSENSITIVE)
        ));

        // Health / Medical
        CATEGORY_PATTERNS.put("health", Arrays.asList(
            Pattern.compile("\\b(health|medical|doctor|patient|hospital|disease|treatment|medicine|drug|vaccine)\\b", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\b(symptoms|diagnosis|therapy|healthcare|wellness|fitness)\\b", Pattern.CASE_INSENSITIVE)
        ));

        // Finance / Business
        CATEGORY_PATTERNS.put("finance", Arrays.asList(
            Pattern.compile("\\b(stock|market|investment|trading|finance|bank|loan|mortgage|interest rate)\\b", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\b(business|company|corporation|revenue|profit|economy|economic)\\b", Pattern.CASE_INSENSITIVE)
        ));

        // Education
        CATEGORY_PATTERNS.put("education", Arrays.asList(
            Pattern.compile("\\b(school|university|college|student|teacher|professor|course|learning|education)\\b", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\b(study|academic|degree|diploma|curriculum|lecture)\\b", Pattern.CASE_INSENSITIVE)
        ));
    }

    @PostConstruct
    public void init() {
        try {
            // Load language detection model
            ClassPathResource modelResource = new ClassPathResource("models/en-doccat.bin");
            try (InputStream modelIn = modelResource.getInputStream()) {
                LanguageDetectorModel model = new LanguageDetectorModel(modelIn);
                languageDetector = new LanguageDetectorME(model);
                log.info("Language detection model loaded successfully");
            }
        } catch (Exception e) {
            log.warn("Could not load language detection model: {} - Language detection will be disabled", e.getMessage());
            languageDetector = null;
        }
    }

    @Override
    public ContentClassification classify(String title, String description, String textContent) {
        // Combine all text for analysis
        String combinedText = String.join(" ",
            Optional.ofNullable(title).orElse(""),
            Optional.ofNullable(description).orElse(""),
            Optional.ofNullable(textContent).orElse("").substring(0, Math.min(1000, Optional.ofNullable(textContent).orElse("").length()))
        ).trim();

        // Detect language
        String detectedLanguage = "unknown";
        double languageConfidence = 0.0;

        if (languageDetector != null && !combinedText.isEmpty()) {
            try {
                Language language = languageDetector.predictLanguage(combinedText);
                detectedLanguage = language.getLang();
                languageConfidence = language.getConfidence();
                log.debug("Detected language: {} with confidence: {}", detectedLanguage, languageConfidence);
            } catch (Exception e) {
                log.warn("Language detection failed: {}", e.getMessage());
            }
        }

        // Classify category using keyword matching - get top 3
        List<ContentClassification.CategoryScore> topCategories = new ArrayList<>();

        if (!combinedText.isEmpty()) {
            Map<String, Integer> categoryScores = new HashMap<>();

            for (Map.Entry<String, List<Pattern>> entry : CATEGORY_PATTERNS.entrySet()) {
                int score = 0;
                for (Pattern pattern : entry.getValue()) {
                    long matchCount = pattern.matcher(combinedText).results().count();
                    score += matchCount;
                }
                if (score > 0) {
                    categoryScores.put(entry.getKey(), score);
                }
            }

            if (!categoryScores.isEmpty()) {
                // Calculate total matches for confidence scoring
                int totalMatches = categoryScores.values().stream().mapToInt(Integer::intValue).sum();

                // Sort categories by score and take top 3
                topCategories = categoryScores.entrySet().stream()
                    .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                    .limit(3)
                    .map(entry -> ContentClassification.CategoryScore.builder()
                        .category(entry.getKey())
                        .confidence(Math.min(1.0, (double) entry.getValue() / Math.max(1, totalMatches)))
                        .build())
                    .toList();

                log.debug("Top categories: {}", topCategories.stream()
                    .map(cs -> cs.getCategory() + ":" + String.format("%.2f", cs.getConfidence()))
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("none"));
            }
        }

        // If no categories matched, add "general" as default
        if (topCategories.isEmpty()) {
            topCategories = List.of(ContentClassification.CategoryScore.builder()
                .category("general")
                .confidence(0.0)
                .build());
        }

        return ContentClassification.builder()
            .categories(topCategories)
            .language(detectedLanguage)
            .languageConfidence(languageConfidence)
            .build();
    }
}
