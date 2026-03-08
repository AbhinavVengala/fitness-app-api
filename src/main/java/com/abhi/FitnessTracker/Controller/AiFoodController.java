package com.abhi.FitnessTracker.Controller;

import com.abhi.FitnessTracker.Model.Food;
import com.abhi.FitnessTracker.Service.AiFoodService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST Controller for AI-powered food search.
 * Accepts natural language meal descriptions and returns structured food items with estimated macros.
 */
@RestController
@RequestMapping("/api/foods")
public class AiFoodController {

    private final AiFoodService aiFoodService;

    public AiFoodController(AiFoodService aiFoodService) {
        this.aiFoodService = aiFoodService;
    }

    /**
     * AI-powered food search endpoint.
     * POST /api/foods/ai-search
     * Body: { "query": "2 rotis with dal and a glass of buttermilk" }
     * Returns: List of Food objects with AI-estimated macros
     */
    @PostMapping("/ai-search")
    public ResponseEntity<?> aiSearch(@RequestBody Map<String, String> request) {
        String query = request.get("query");

        if (query == null || query.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Query is required"));
        }

        try {
            List<Food> foods = aiFoodService.parseNaturalLanguage(query.trim());
            return ResponseEntity.ok(foods);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "error", "AI search failed: " + e.getMessage()
            ));
        }
    }
}
