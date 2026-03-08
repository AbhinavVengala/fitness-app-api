package com.abhi.FitnessTracker.Service;

import com.abhi.FitnessTracker.Model.Food;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * AI-powered food search service using Google Gemini API.
 * Parses natural language meal descriptions into structured food items with estimated macros.
 */
@Service
public class AiFoodService {

    @Value("${gemini.api-key:}")
    private String geminiApiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String GEMINI_API_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent";

    private static final String SYSTEM_PROMPT = """
            You are a nutrition expert AI. The user will describe a meal or food items in natural language.
            Your job is to break down the description into individual food items and estimate the nutritional macros for each.
            
            Rules:
            - Return ONLY a valid JSON array, no markdown, no explanation, no code fences.
            - Each item should have: name, calories, protein, carbs, fats, category, confidence.
            - Macros should be per serving (a typical single serving size).
            - category must be one of: indian, protein, grains, fruits, vegetables, nuts, dairy, beverages, snacks.
            - confidence must be one of: high, medium, low.
            - Be accurate with Indian foods (roti, dal, biryani, dosa, etc.) and international foods.
            - If the user specifies a quantity (e.g., "2 rotis"), multiply the macros by that quantity and reflect it in the name.
            - Round calories to nearest integer, macros to 1 decimal place.
            
            Example input: "2 rotis with paneer butter masala and a glass of lassi"
            Example output:
            [
              {"name": "Roti (2 pieces)", "calories": 240, "protein": 6.8, "carbs": 50.2, "fats": 1.6, "category": "indian", "confidence": "high"},
              {"name": "Paneer Butter Masala (1 serving)", "calories": 350, "protein": 14.0, "carbs": 12.5, "fats": 26.0, "category": "indian", "confidence": "high"},
              {"name": "Lassi (1 glass)", "calories": 150, "protein": 5.0, "carbs": 22.0, "fats": 4.5, "category": "beverages", "confidence": "medium"}
            ]
            """;

    /**
     * Parse a natural language food description using Gemini AI.
     *
     * @param query the natural language meal description
     * @return list of Food objects with estimated macros
     */
    public List<Food> parseNaturalLanguage(String query) {
        if (geminiApiKey == null || geminiApiKey.isBlank()) {
            throw new RuntimeException("Gemini API key is not configured. Set GEMINI_API_KEY environment variable.");
        }

        try {
            // Build request body
            Map<String, Object> requestBody = buildGeminiRequest(query);

            // Set headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            // Call Gemini API
            String url = GEMINI_API_URL + "?key=" + geminiApiKey;
            ResponseEntity<JsonNode> response = restTemplate.exchange(url, HttpMethod.POST, entity, JsonNode.class);

            if (response.getBody() == null) {
                throw new RuntimeException("Empty response from Gemini API");
            }

            // Extract text content from Gemini response
            String content = extractContent(response.getBody());

            // Clean the response (remove any markdown code fences if present)
            content = cleanJsonResponse(content);

            // Parse JSON array into Food objects
            List<Map<String, Object>> items = objectMapper.readValue(content, new TypeReference<>() {});

            List<Food> foods = new ArrayList<>();
            for (Map<String, Object> item : items) {
                Food food = new Food();
                food.setId("ai-" + UUID.randomUUID().toString().substring(0, 8));
                food.setName((String) item.get("name"));
                food.setCalories(toDouble(item.get("calories")));
                food.setProtein(toDouble(item.get("protein")));
                food.setCarbs(toDouble(item.get("carbs")));
                food.setFats(toDouble(item.get("fats")));
                food.setCategory((String) item.getOrDefault("category", "snacks"));
                foods.add(food);
            }

            return foods;

        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse food with AI: " + e.getMessage(), e);
        }
    }

    private Map<String, Object> buildGeminiRequest(String userQuery) {
        // System instruction
        Map<String, Object> systemInstruction = new HashMap<>();
        systemInstruction.put("parts", List.of(Map.of("text", SYSTEM_PROMPT)));

        // User message
        Map<String, Object> userContent = new HashMap<>();
        userContent.put("role", "user");
        userContent.put("parts", List.of(Map.of("text", userQuery)));

        // Generation config for JSON output
        Map<String, Object> generationConfig = new HashMap<>();
        generationConfig.put("temperature", 0.3);
        generationConfig.put("responseMimeType", "application/json");

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("system_instruction", systemInstruction);
        requestBody.put("contents", List.of(userContent));
        requestBody.put("generationConfig", generationConfig);

        return requestBody;
    }

    private String extractContent(JsonNode responseBody) {
        JsonNode candidates = responseBody.path("candidates");
        if (candidates.isArray() && !candidates.isEmpty()) {
            JsonNode content = candidates.get(0).path("content").path("parts");
            if (content.isArray() && !content.isEmpty()) {
                return content.get(0).path("text").asText();
            }
        }
        throw new RuntimeException("Could not extract content from Gemini response");
    }

    private String cleanJsonResponse(String content) {
        if (content == null) return "[]";
        content = content.trim();
        // Remove markdown code fences if present
        if (content.startsWith("```json")) {
            content = content.substring(7);
        } else if (content.startsWith("```")) {
            content = content.substring(3);
        }
        if (content.endsWith("```")) {
            content = content.substring(0, content.length() - 3);
        }
        return content.trim();
    }

    private double toDouble(Object value) {
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        if (value instanceof String) {
            try {
                return Double.parseDouble((String) value);
            } catch (NumberFormatException e) {
                return 0.0;
            }
        }
        return 0.0;
    }
}
