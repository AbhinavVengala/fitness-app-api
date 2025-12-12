package com.abhi.FitnessTracker.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Food document representing a food item in the reference database.
 * Used for food search functionality.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "foods")
public class Food {
    @Id
    private String id;
    
    private String name;
    private double calories;
    private double protein;
    private double carbs;
    private double fats;
    private String category; // indian, protein, grains, fruits, vegetables, nuts, dairy, beverages, snacks
    private String createdByUserId; // null = system/admin, userId = user custom item
    
    // Constructor without createdByUserId for seed data
    public Food(String id, String name, double calories, double protein, double carbs, double fats, String category) {
        this.id = id;
        this.name = name;
        this.calories = calories;
        this.protein = protein;
        this.carbs = carbs;
        this.fats = fats;
        this.category = category;
        this.createdByUserId = null;
    }
}

