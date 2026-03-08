package com.abhi.FitnessTracker.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service for generating AI-powered meal plans/suggestions based on profile and remaining macros.
 */
@Service
public class AiMealPlanService {

    @Value("${gemini.api-key:}")
    private String geminiApiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String GEMINI_API_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent";

    private static final String SYSTEM_PROMPT = """
            You are a highly skilled nutritionist and culinary expert.
            The user is asking for meal or snack suggestions based on their fitness profile and EXACT remaining daily macros.
            
            Guidelines for your response:
            1. Suggest 2 to 3 specific, realistic meal or snack options that perfectly fit the 'Remaining Macros' provided below.
            2. For each option, clearly state the estimated macronutrient breakdown (Calories, Protein, Carbs, Fats) to prove it fits.
            3. Prioritize whole foods and healthy ingredients based on the user's fitness goal.
            4. Keep your response concise, encouraging, and format it beautifully in Markdown using emojis, bullet points, and bold text for easy reading.
            5. Do NOT hallucinate macros that are mathematically impossible.
            """;

    /**
     * Generate a meal plan or meal suggestion.
     */
    public String generateMealPlan(Map<String, Object> requestPayload) {
        if (geminiApiKey == null || geminiApiKey.isBlank()) {
            throw new RuntimeException("Gemini API key is not configured.");
        }

        try {
            // Build context string from the request payload
            Map<String, Object> profile = (Map<String, Object>) requestPayload.getOrDefault("profile", new HashMap<>());
            Map<String, Object> currentLog = (Map<String, Object>) requestPayload.getOrDefault("currentLog", new HashMap<>());
            String userQuery = (String) requestPayload.getOrDefault("query", "What should I eat?");

            String userData = String.format("""
                    --- USER CONTEXT ---
                    Profile: %s, %s, %s kg, %s cm
                    Fitness Goal: %s
                    Experience: %s
                    
                    --- TODAY'S CONSUMED TOTALS ---
                    Calories: %s kcal
                    Protein: %s g
                    Carbs: %s g
                    Fats: %s g
                    
                    --- REMAINING TARGETS FOR TODAY ---
                    Calories %s kcal
                    Protein: %s g
                    Carbs: %s g
                    Fats: %s g
                    
                    --- USER QUERY ---
                    "%s"
                    """,
                    profile.get("age"), profile.get("gender"), profile.get("weight"), profile.get("height"),
                    profile.get("fitnessGoal"), profile.get("experienceLevel"),
                    
                    currentLog.get("consumedCalories"), currentLog.get("consumedProtein"), 
                    currentLog.get("consumedCarbs"), currentLog.get("consumedFats"),
                    
                    currentLog.get("remainingCalories"), currentLog.get("remainingProtein"), 
                    currentLog.get("remainingCarbs"), currentLog.get("remainingFats"),
                    
                    userQuery
            );

            return callGemini(userData);

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate meal plan: " + e.getMessage(), e);
        }
    }

    private String callGemini(String userData) {
        Map<String, Object> systemInstruction = new HashMap<>();
        systemInstruction.put("parts", List.of(Map.of("text", SYSTEM_PROMPT)));

        Map<String, Object> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("parts", List.of(Map.of("text", userData)));

        Map<String, Object> generationConfig = new HashMap<>();
        generationConfig.put("temperature", 0.6); // slight creativity for recipes

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("system_instruction", systemInstruction);
        requestBody.put("contents", List.of(userMessage));
        requestBody.put("generationConfig", generationConfig);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        String url = GEMINI_API_URL + "?key=" + geminiApiKey;
        ResponseEntity<JsonNode> response = restTemplate.exchange(url, HttpMethod.POST, entity, JsonNode.class);

        if (response.getBody() == null) {
            throw new RuntimeException("Empty response from Gemini API");
        }

        return extractContent(response.getBody());
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
}
