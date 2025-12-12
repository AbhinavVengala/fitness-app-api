package com.abhi.FitnessTracker.Service;

import com.abhi.FitnessTracker.Model.WorkoutEntry;
import com.abhi.FitnessTracker.Model.WorkoutLog;
import com.abhi.FitnessTracker.Repository.WorkoutLogRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * Service for managing daily workout logs.
 */
@Service
public class WorkoutLogService {
    
    private final WorkoutLogRepository workoutLogRepository;
    
    public WorkoutLogService(WorkoutLogRepository workoutLogRepository) {
        this.workoutLogRepository = workoutLogRepository;
    }
    
    /**
     * Get today's workout log for a profile, create if doesn't exist
     */
    public WorkoutLog getTodayLog(String profileId) {
        return getLogByDate(profileId, LocalDate.now());
    }
    
    /**
     * Get workout log for a specific date
     */
    public WorkoutLog getLogByDate(String profileId, LocalDate date) {
        return workoutLogRepository.findByProfileIdAndDate(profileId, date)
            .orElseGet(() -> {
                WorkoutLog newLog = new WorkoutLog();
                newLog.setProfileId(profileId);
                newLog.setDate(date);
                return newLog;
            });
    }
    
    /**
     * Add a workout entry to today's log
     */
    public WorkoutLog addWorkout(String profileId, WorkoutEntry workout) {
        WorkoutLog log = getTodayLog(profileId);
        
        // Check if workout already exists, update if so
        if (log.getWorkouts().stream().anyMatch(w -> w.getId().equals(workout.getId()))) {
            log.updateWorkout(workout.getId(), workout);
        } else {
            log.addWorkout(workout);
        }
        
        return workoutLogRepository.save(log);
    }
    
    /**
     * Update an existing workout entry
     */
    public WorkoutLog updateWorkout(String profileId, String workoutId, WorkoutEntry updatedWorkout) {
        WorkoutLog log = getTodayLog(profileId);
        updatedWorkout.setId(workoutId);
        
        if (!log.updateWorkout(workoutId, updatedWorkout)) {
            throw new RuntimeException("Workout not found");
        }
        
        return workoutLogRepository.save(log);
    }
    
    /**
     * Remove a workout entry from today's log
     */
    public WorkoutLog removeWorkout(String profileId, String workoutId) {
        WorkoutLog log = getTodayLog(profileId);
        log.removeWorkout(workoutId);
        return workoutLogRepository.save(log);
    }
    
    /**
     * Get all workout logs for a profile
     */
    public List<WorkoutLog> getAllLogs(String profileId) {
        return workoutLogRepository.findByProfileId(profileId);
    }
    
    /**
     * Get workout logs for a date range
     */
    public List<WorkoutLog> getLogsBetweenDates(String profileId, LocalDate startDate, LocalDate endDate) {
        return workoutLogRepository.findByProfileIdAndDateBetween(profileId, startDate, endDate);
    }
}
