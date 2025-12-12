package com.abhi.FitnessTracker.Service;

import com.abhi.FitnessTracker.Model.FoodItem;
import com.abhi.FitnessTracker.Model.FoodLog;
import com.abhi.FitnessTracker.Repository.FoodLogRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * Service for managing daily food logs.
 */
@Service
public class FoodLogService {
    
    private final FoodLogRepository foodLogRepository;
    
    public FoodLogService(FoodLogRepository foodLogRepository) {
        this.foodLogRepository = foodLogRepository;
    }
    
    /**
     * Get today's food log for a profile, create if doesn't exist
     */
    public FoodLog getTodayLog(String profileId) {
        return getLogByDate(profileId, LocalDate.now());
    }
    
    /**
     * Get food log for a specific date
     */
    public FoodLog getLogByDate(String profileId, LocalDate date) {
        return foodLogRepository.findByProfileIdAndDate(profileId, date)
            .orElseGet(() -> {
                FoodLog newLog = new FoodLog();
                newLog.setProfileId(profileId);
                newLog.setDate(date);
                return newLog;
            });
    }
    
    /**
     * Add a food item to today's log
     */
    public FoodLog addFoodItem(String profileId, FoodItem item) {
        FoodLog log = getTodayLog(profileId);
        
        // Generate ID if not provided
        if (item.getId() == null) {
            item.setId(System.currentTimeMillis());
        }
        
        log.addItem(item);
        return foodLogRepository.save(log);
    }
    
    /**
     * Remove a food item from today's log
     */
    public FoodLog removeFoodItem(String profileId, Long itemId) {
        FoodLog log = getTodayLog(profileId);
        log.removeItem(itemId);
        return foodLogRepository.save(log);
    }
    
    /**
     * Get all food logs for a profile
     */
    public List<FoodLog> getAllLogs(String profileId) {
        return foodLogRepository.findByProfileId(profileId);
    }
    
    /**
     * Get food logs for a date range
     */
    public List<FoodLog> getLogsBetweenDates(String profileId, LocalDate startDate, LocalDate endDate) {
        return foodLogRepository.findByProfileIdAndDateBetween(profileId, startDate, endDate);
    }
}
