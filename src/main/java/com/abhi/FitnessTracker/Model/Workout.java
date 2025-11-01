package com.abhi.FitnessTracker.Model;

import jakarta.persistence.Id;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;

@Data
@Document(collection = "workouts")
public class Workout {
    @Id
    private String id;

    private String userId;
    private LocalDate date;
    private int durationMin;
    private double caloriesBurned;
    private String notes;

    private List<Exercise> exercises;

    @Data
    public static class Exercise {
        private String name;
        private int sets;
        private int reps;
        private double weightKg;
    }
}
