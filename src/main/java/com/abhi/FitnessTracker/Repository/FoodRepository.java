package com.abhi.FitnessTracker.Repository;

import com.abhi.FitnessTracker.Model.Food;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface FoodRepository extends MongoRepository<Food, String> {
    List<Food> findByNameContainingIgnoreCase(String name);
    List<Food> findByCategory(String category);
}
