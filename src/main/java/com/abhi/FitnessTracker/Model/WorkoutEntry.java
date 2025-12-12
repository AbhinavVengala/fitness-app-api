package com.abhi.FitnessTracker.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Embedded document representing a single workout entry in the daily workout log.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutEntry {
    private String id;
    private String exerciseId;  // Reference to exercise in exercises collection
    private String name;
    private String type;        // "reps" or "duration"
    private String category;    // home, gym, yoga, hiit, sports
    private Integer reps;       // Reps per set (for reps-based exercises)
    private Integer sets;       // Number of sets (for reps-based exercises)
    private Integer duration;   // Duration in minutes (for duration-based exercises)
    private double caloriesBurned;
    private LocalDateTime timestamp;
    private List<Boolean> completedSets;  // For set-by-set tracking
}

