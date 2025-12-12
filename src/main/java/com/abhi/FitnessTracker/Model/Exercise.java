package com.abhi.FitnessTracker.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Exercise document representing an exercise in the reference database.
 * Used for workout planning and calorie calculation.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "exercises")
public class Exercise {
    @Id
    private String id;
    
    private String name;
    private String type; // "reps" or "duration"
    private Double caloriesPerRep; // for rep-based exercises
    private Double met; // for duration-based exercises (metabolic equivalent)
    private String category; // home, gym, yoga, hiit, sports
    private String createdByUserId; // null = system/admin, userId = user custom item
    
    // Constructor without createdByUserId for seed data
    public Exercise(String id, String name, String type, Double caloriesPerRep, Double met, String category) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.caloriesPerRep = caloriesPerRep;
        this.met = met;
        this.category = category;
        this.createdByUserId = null;
    }
}

