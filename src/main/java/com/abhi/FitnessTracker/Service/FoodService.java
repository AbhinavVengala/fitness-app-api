package com.abhi.FitnessTracker.Service;

import com.abhi.FitnessTracker.Model.Food;
import com.abhi.FitnessTracker.Repository.FoodRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
     * Search foods by name (case-insensitive)
     */
    public List<Food> searchByName(String query) {
        if (query == null || query.trim().isEmpty()) {
            return List.of();
        }
        return foodRepository.findByNameContainingIgnoreCase(query.trim());
    }
    
    /**
     * Get all foods
     */
    public List<Food> getAll() {
        return foodRepository.findAll();
    }
    
    /**
     * Get foods by category
     */
    public List<Food> getByCategory(String category) {
        return foodRepository.findByCategory(category);
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
    public boolean deleteFood(String id) {
        if (!foodRepository.existsById(id)) {
            return false;
        }
        foodRepository.deleteById(id);
        return true;
    }
}

