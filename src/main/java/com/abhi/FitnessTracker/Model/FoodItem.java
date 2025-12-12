package com.abhi.FitnessTracker.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Embedded document representing a single food item in the daily food log.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FoodItem {
    private Long id;
    private String foodId;      // Reference to food in foods collection
    private String name;
    private double calories;
    private double protein;
    private double carbs;
    private double fats;
    private double quantity;    // Number of servings (e.g., 1.5)
    private String meal;        // breakfast, lunch, dinner, snack
}

