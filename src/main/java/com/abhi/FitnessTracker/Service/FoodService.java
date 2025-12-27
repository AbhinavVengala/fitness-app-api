package com.abhi.FitnessTracker.Service;

import com.abhi.FitnessTracker.Model.Food;
import com.abhi.FitnessTracker.Repository.FoodRepository;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Service for managing the food reference database.
 */
@Service
public class FoodService {
    
    private final FoodRepository foodRepository;
    
    public FoodService(FoodRepository foodRepository) {
        this.foodRepository = foodRepository;
    }
    
    /**
     * Search foods by name (paginated)
     */
    public Page<Food> searchByName(String query, String userId, Pageable pageable) {
        if (query == null || query.trim().isEmpty()) {
            return Page.empty();
        }
        if (userId != null) {
            return foodRepository.searchByNameAndUser(query.trim(), userId, pageable);
        }
        return foodRepository.findByNameContainingIgnoreCaseAndCreatedByUserIdIsNull(query.trim(), pageable);
    }
    
    /**
     * Get all foods (paginated)
     */
    public Page<Food> getAll(String userId, Pageable pageable) {
        if (userId != null) {
            return foodRepository.findSystemAndUserFoods(userId, pageable);
        }
        return foodRepository.findByCreatedByUserIdIsNull(pageable);
    }
    
    /**
     * Get foods by category (paginated)
     */
    public Page<Food> getByCategory(String category, String userId, Pageable pageable) {
        if (userId != null) {
            return foodRepository.findByCategoryAndUser(category, userId, pageable);
        }
        return foodRepository.findByCategoryAndCreatedByUserIdIsNull(category, pageable);
    }
    
    /**
     * Add a new food to the database
     */
    public Food addFood(Food food) {
        return foodRepository.save(food);
    }
    
    /**
     * Add multiple foods to the database
     */
    public List<Food> addFoods(List<Food> foods) {
        return foodRepository.saveAll(foods);
    }
    
    /**
     * Update an existing food
     */
    public Food updateFood(String id, Food food) {
        Optional<Food> existing = foodRepository.findById(id);
        if (existing.isEmpty()) {
            return null;
        }
        food.setId(id);
        return foodRepository.save(food);
    }
    
    /**
     * Delete a food by ID
     */
    /**
     * Delete a food by ID (Secure)
     */
    public boolean deleteFood(String id, String requesterId, boolean isAdmin) {
        Optional<Food> foodOpt = foodRepository.findById(id);
        if (foodOpt.isEmpty()) {
            return false;
        }
        Food food = foodOpt.get();
        
        // Admin can delete anything
        if (isAdmin) {
            foodRepository.deleteById(id);
            return true;
        }
        
        // Regular users checks
        if (food.getCreatedByUserId() == null) {
            throw new RuntimeException("You cannot delete system foods");
        }
        
        if (!food.getCreatedByUserId().equals(requesterId)) {
            throw new RuntimeException("You do not have permission to delete this food");
        }
        
        foodRepository.deleteById(id);
        return true;
    }

    /**
     * Get food by barcode (Local DB or OpenFoodFacts)
     */
    public Food getFoodByBarcode(String barcode) {
        // 1. Check local DB
        Optional<Food> localFood = foodRepository.findByBarcode(barcode);
        if (localFood.isPresent()) {
            return localFood.get();
        }

        // 2. Fetch from OpenFoodFacts
        try {
            RestTemplate restTemplate = new RestTemplate();
            String url = "https://world.openfoodfacts.org/api/v0/product/" + barcode + ".json";
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            if (response != null && (int) response.get("status") == 1) {
                Map<String, Object> product = (Map<String, Object>) response.get("product");
                Map<String, Object> nutriments = (Map<String, Object>) product.get("nutriments");

                Food newFood = new Food();
                newFood.setName((String) product.get("product_name"));
                newFood.setBarcode(barcode);
                newFood.setImageUrl((String) product.get("image_url"));
                newFood.setCategory("snacks"); // Default category, maybe infer later

                // Extract macros (per 100g)
                newFood.setCalories(getDoubleValue(nutriments, "energy-kcal_100g"));
                newFood.setProtein(getDoubleValue(nutriments, "proteins_100g"));
                newFood.setCarbs(getDoubleValue(nutriments, "carbohydrates_100g"));
                newFood.setFats(getDoubleValue(nutriments, "fat_100g"));
                
                // Save to local DB for caching
                return foodRepository.save(newFood);
            }
        } catch (Exception e) {
            System.err.println("Error fetching from OpenFoodFacts: " + e.getMessage());
        }

        return null;
    }

    private double getDoubleValue(Map<String, Object> map, String key) {
        Object val = map.get(key);
        if (val instanceof Number) {
            return ((Number) val).doubleValue();
        }
        return 0.0;
    }
}

