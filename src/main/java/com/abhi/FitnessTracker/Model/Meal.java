package com.abhi.FitnessTracker.Model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;

@Data
@Document(collection = "meals")
public class Meal {
    @Id
    private String id;
    private String userId;
    private String mealType;
    private LocalDate date;
    private double totalCalories;
    private String notes;
    private List<MealItem> items;

    @Data
    public static class MealItem {
        private String foodName;
        private double calories;
        private double proteinG;
        private double carbsG;
        private double fatsG;
        private String quantity;
    }
}

