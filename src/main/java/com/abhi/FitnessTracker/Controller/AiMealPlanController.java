package com.abhi.FitnessTracker.Controller;

import com.abhi.FitnessTracker.Service.AiMealPlanService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller for AI Meal Plan Generation
 */
@RestController
@RequestMapping("/api/meal-plans")
public class AiMealPlanController {

    private final AiMealPlanService aiMealPlanService;

    public AiMealPlanController(AiMealPlanService aiMealPlanService) {
        this.aiMealPlanService = aiMealPlanService;
    }

    /**
     * Generate personalized meal plan suggestion.
     * POST /api/meal-plans/generate
     */
    @PostMapping("/generate")
    public ResponseEntity<?> generateMealPlan(@RequestBody Map<String, Object> requestPayload) {
        try {
            String aiSuggestion = aiMealPlanService.generateMealPlan(requestPayload);
            return ResponseEntity.ok(Map.of("suggestion", aiSuggestion));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "error", "Failed to generate meal plan: " + e.getMessage()
            ));
        }
    }
}
