package com.abhi.FitnessTracker.Model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * WorkoutLog document representing a daily workout log for a profile.
 * Each document contains all workout entries logged for a specific date.
 */
@Data
@Document(collection = "workout_logs")
@CompoundIndex(name = "profile_date_idx", def = "{'profileId': 1, 'date': 1}", unique = true)
public class WorkoutLog {
    @Id
    private String id;
    
    private String profileId;
    
    private LocalDate date;
    
    private List<WorkoutEntry> workouts = new ArrayList<>();
    
    /**
     * Add a workout entry to the log
     */
    public void addWorkout(WorkoutEntry workout) {
        if (this.workouts == null) {
            this.workouts = new ArrayList<>();
        }
        this.workouts.add(workout);
    }
    
    /**
     * Update an existing workout entry
     */
    public boolean updateWorkout(String workoutId, WorkoutEntry updatedWorkout) {
        if (this.workouts == null) return false;
        for (int i = 0; i < workouts.size(); i++) {
            if (workouts.get(i).getId().equals(workoutId)) {
                workouts.set(i, updatedWorkout);
                return true;
            }
        }
        return false;
    }
    
    /**
     * Remove a workout entry by ID
     */
    public boolean removeWorkout(String workoutId) {
        if (this.workouts == null) return false;
        return this.workouts.removeIf(w -> w.getId().equals(workoutId));
    }
    
    /**
     * Calculate total calories burned for the day
     */
    public double getTotalCaloriesBurned() {
        if (this.workouts == null) return 0;
        return this.workouts.stream().mapToDouble(WorkoutEntry::getCaloriesBurned).sum();
    }
}
