package com.abhi.FitnessTracker.Controller;

import com.abhi.FitnessTracker.Model.Food;
import com.abhi.FitnessTracker.Service.FoodService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    private final com.abhi.FitnessTracker.Repository.UserRepository userRepository;
    
    public FoodController(FoodService foodService, com.abhi.FitnessTracker.Repository.UserRepository userRepository) {
        this.foodService = foodService;
        this.userRepository = userRepository;
    }
    
    /**
     * Get all foods (system + user's custom) - Paginated
     * GET /api/foods?userId={userId}&page=0&size=20
     */
    @GetMapping
    public ResponseEntity<List<Food>> getAllFoods(
            @RequestParam(required = false) String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(foodService.getAll(userId, pageable).getContent());
    }
    
    /**
     * Search foods by name - Paginated
     * GET /api/foods/search?q={query}&userId={userId}&page=0&size=20
     */
    @GetMapping("/search")
    public ResponseEntity<List<Food>> searchFoods(
            @RequestParam String q,
            @RequestParam(required = false) String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
            
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(foodService.searchByName(q, userId, pageable).getContent());
    }
    
    /**
     * Get foods by category - Paginated
     * GET /api/foods/category/{category}?userId={userId}&page=0&size=20
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<List<Food>> getFoodsByCategory(
            @PathVariable String category,
            @RequestParam(required = false) String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
            
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(foodService.getByCategory(category, userId, pageable).getContent());
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
    public ResponseEntity<?> deleteFood(@PathVariable String id, org.springframework.security.core.Authentication authentication) {
        try {
            String email = authentication.getName();
            var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
                
            boolean deleted = foodService.deleteFood(id, user.getId(), user.isAdmin());
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

