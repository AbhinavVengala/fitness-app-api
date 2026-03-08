package com.abhi.FitnessTracker.Controller;

import com.abhi.FitnessTracker.Service.AiWorkoutPlanService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller for AI Workout Plan Generation
 */
@RestController
@RequestMapping("/api/workout-plans")
public class AiWorkoutPlanController {

    private final AiWorkoutPlanService aiWorkoutPlanService;

    public AiWorkoutPlanController(AiWorkoutPlanService aiWorkoutPlanService) {
        this.aiWorkoutPlanService = aiWorkoutPlanService;
    }

    /**
     * Generate personalized workout plan suggestion.
     * POST /api/workout-plans/generate
     */
    @PostMapping("/generate")
    public ResponseEntity<?> generateWorkoutPlan(@RequestBody Map<String, Object> requestPayload) {
        try {
            String aiSuggestion = aiWorkoutPlanService.generateWorkoutPlan(requestPayload);
            return ResponseEntity.ok(Map.of("suggestion", aiSuggestion));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "error", "Failed to generate workout plan: " + e.getMessage()
            ));
        }
    }
}
