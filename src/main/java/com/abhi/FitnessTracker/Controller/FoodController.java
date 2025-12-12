package com.abhi.FitnessTracker.Controller;

import com.abhi.FitnessTracker.Model.Food;
import com.abhi.FitnessTracker.Service.FoodService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * REST Controller for food database endpoints.
 * Supports system foods and user custom foods.
 */
@RestController
@RequestMapping("/api/foods")
@CrossOrigin(origins = "http://localhost:5173")
public class FoodController {
    
    private final FoodService foodService;
    
    public FoodController(FoodService foodService) {
        this.foodService = foodService;
    }
    
    /**
     * Get all foods (system + user's custom)
     * GET /api/foods?userId={userId}
     */
    @GetMapping
    public ResponseEntity<List<Food>> getAllFoods(@RequestParam(required = false) String userId) {
        List<Food> foods = foodService.getAll();
        
        // Filter: show system foods (createdByUserId = null) + user's own custom foods
        if (userId != null) {
            foods = foods.stream()
                .filter(f -> f.getCreatedByUserId() == null || f.getCreatedByUserId().equals(userId))
                .collect(Collectors.toList());
        } else {
            // Only show system foods if no userId provided
            foods = foods.stream()
                .filter(f -> f.getCreatedByUserId() == null)
                .collect(Collectors.toList());
        }
        
        return ResponseEntity.ok(foods);
    }
    
    /**
     * Search foods by name
     * GET /api/foods/search?q={query}&userId={userId}
     */
    @GetMapping("/search")
    public ResponseEntity<List<Food>> searchFoods(
            @RequestParam String q,
            @RequestParam(required = false) String userId) {
        List<Food> foods = foodService.searchByName(q);
        
        if (userId != null) {
            foods = foods.stream()
                .filter(f -> f.getCreatedByUserId() == null || f.getCreatedByUserId().equals(userId))
                .collect(Collectors.toList());
        } else {
            foods = foods.stream()
                .filter(f -> f.getCreatedByUserId() == null)
                .collect(Collectors.toList());
        }
        
        return ResponseEntity.ok(foods);
    }
    
    /**
     * Get foods by category
     * GET /api/foods/category/{category}?userId={userId}
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<List<Food>> getFoodsByCategory(
            @PathVariable String category,
            @RequestParam(required = false) String userId) {
        List<Food> foods = foodService.getByCategory(category);
        
        if (userId != null) {
            foods = foods.stream()
                .filter(f -> f.getCreatedByUserId() == null || f.getCreatedByUserId().equals(userId))
                .collect(Collectors.toList());
        } else {
            foods = foods.stream()
                .filter(f -> f.getCreatedByUserId() == null)
                .collect(Collectors.toList());
        }
        
        return ResponseEntity.ok(foods);
    }
    
    /**
     * Add a new food to the database (user custom or admin)
     * POST /api/foods
     */
    @PostMapping
    public ResponseEntity<Food> addFood(@RequestBody Food food) {
        return ResponseEntity.ok(foodService.addFood(food));
    }
    
    /**
     * Update a food
     * PUT /api/foods/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateFood(@PathVariable String id, @RequestBody Food food) {
        try {
            Food updated = foodService.updateFood(id, food);
            if (updated == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Delete a food
     * DELETE /api/foods/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFood(@PathVariable String id) {
        try {
            boolean deleted = foodService.deleteFood(id);
            if (!deleted) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(Map.of("message", "Food deleted"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Get all categories
     * GET /api/foods/categories
     */
    @GetMapping("/categories")
    public ResponseEntity<List<String>> getCategories() {
        return ResponseEntity.ok(List.of(
            "indian", "protein", "grains", "fruits", "vegetables", 
            "nuts", "dairy", "beverages", "snacks"
        ));
    }
    
    /**
     * Bulk add foods to the database
     * POST /api/foods/bulk
     */
    @PostMapping("/bulk")
    public ResponseEntity<List<Food>> addFoods(@RequestBody List<Food> foods) {
        return ResponseEntity.ok(foodService.addFoods(foods));
    }
}

