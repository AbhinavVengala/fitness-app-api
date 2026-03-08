package com.abhi.FitnessTracker.Service;

import com.abhi.FitnessTracker.Model.FoodLog;
import com.abhi.FitnessTracker.Model.WorkoutLog;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service for generating AI-powered insights from user data.
 */
@Service
public class AiInsightsService {

    @Value("${gemini.api-key:}")
    private String geminiApiKey;

    private final FoodLogService foodLogService;
    private final WorkoutLogService workoutLogService;
    private final RestTemplate restTemplate = new RestTemplate();

    private static final String GEMINI_API_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent";

    private static final String SYSTEM_PROMPT = """
            You are an expert, encouraging AI Fitness Coach embedded in the PacePlate app.
            Your job is to analyze the user's weekly fitness data and provide a "Weekly AI Insight".
            
            ## Instructions
            1. Keep it short, punchy, and highly readable (max 3 short paragraphs).
            2. Use markdown formatting (bolding key numbers, using relevant emojis like 🔥, 🍏, 💧, 💪).
            3. Highlight their **biggest win** for the week (e.g., consistency, high protein, great workouts).
            4. Provide **one actionable tip** for the upcoming week based on their data.
            5. Adopt a warm, motivational tone.
            
            ## Data Context
            You will be given summary data for the past 7 days (calories consumed vs burned, macros, workouts).
            Do not list out every single day's data, just use it to form your overall insight.
            """;

    public AiInsightsService(FoodLogService foodLogService, WorkoutLogService workoutLogService) {
        this.foodLogService = foodLogService;
        this.workoutLogService = workoutLogService;
    }

    /**
     * Generate a weekly insight summary for a profile between two dates.
     */
    public String generateWeeklyInsight(String profileId, LocalDate startDate, LocalDate endDate) {
        if (geminiApiKey == null || geminiApiKey.isBlank()) {
            throw new RuntimeException("Gemini API key is not configured.");
        }

        try {
            // 1. Gather data
            List<FoodLog> foodLogs = foodLogService.getLogsBetweenDates(profileId, startDate, endDate);
            List<WorkoutLog> workoutLogs = workoutLogService.getLogsBetweenDates(profileId, startDate, endDate);

            // 2. Aggregate data
            double totalCaloriesConsumed = foodLogs.stream().mapToDouble(FoodLog::getTotalCalories).sum();
            double totalProtein = foodLogs.stream().mapToDouble(FoodLog::getTotalProtein).sum();
            double totalWorkouts = workoutLogs.stream().mapToInt(log -> log.getWorkouts() != null ? log.getWorkouts().size() : 0).sum();
            double totalCaloriesBurned = workoutLogs.stream().mapToDouble(WorkoutLog::getTotalCaloriesBurned).sum();
            
            int daysWithFoodLogged = foodLogs.size();
            int daysWithWorkoutLogged = workoutLogs.size();

            // 3. Build context string
            String contextData = String.format("""
                    **Weekly Summary (%s to %s)**
                    - Days with food logged: %d/7
                    - Days with workout logged: %d/7
                    - Total Calories Consumed: %.0f kcal
                    - Total Protein Consumed: %.0f g
                    - Total Workouts Completed: %.0f
                    - Total Calories Burned (Active): %.0f kcal
                    """, 
                    startDate.toString(), endDate.toString(), 
                    daysWithFoodLogged, daysWithWorkoutLogged,
                    totalCaloriesConsumed, totalProtein, totalWorkouts, totalCaloriesBurned);

            // 4. Call Gemini
            return callGemini(contextData);

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate weekly insight: " + e.getMessage(), e);
        }
    }

    private String callGemini(String userData) {
        Map<String, Object> systemInstruction = new HashMap<>();
        systemInstruction.put("parts", List.of(Map.of("text", SYSTEM_PROMPT)));

        Map<String, Object> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("parts", List.of(Map.of("text", "Please analyze this weekly data and give me my insight:\n\n" + userData)));

        Map<String, Object> generationConfig = new HashMap<>();
        generationConfig.put("temperature", 0.7);

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
