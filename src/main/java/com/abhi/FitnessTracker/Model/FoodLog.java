package com.abhi.FitnessTracker.Model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * FoodLog document representing a daily food log for a profile.
 * Each document contains all food items logged for a specific date.
 */
@Data
@Document(collection = "food_logs")
@CompoundIndex(name = "profile_date_idx", def = "{'profileId': 1, 'date': 1}", unique = true)
public class FoodLog {
    @Id
    private String id;
    
    private String profileId;
    
    private LocalDate date;
    
    private List<FoodItem> items = new ArrayList<>();
    
    /**
     * Add a food item to the log
     */
    public void addItem(FoodItem item) {
        if (this.items == null) {
            this.items = new ArrayList<>();
        }
        this.items.add(item);
    }
    
    /**
     * Remove a food item by ID
     */
    public boolean removeItem(Long itemId) {
        if (this.items == null) return false;
        return this.items.removeIf(item -> item.getId().equals(itemId));
    }
    
    /**
     * Calculate total calories for the day
     */
    public double getTotalCalories() {
        if (this.items == null) return 0;
        return this.items.stream().mapToDouble(FoodItem::getCalories).sum();
    }
    
    /**
     * Calculate total protein for the day
     */
    public double getTotalProtein() {
        if (this.items == null) return 0;
        return this.items.stream().mapToDouble(FoodItem::getProtein).sum();
    }
    
    /**
     * Calculate total carbs for the day
     */
    public double getTotalCarbs() {
        if (this.items == null) return 0;
        return this.items.stream().mapToDouble(FoodItem::getCarbs).sum();
    }
    
    /**
     * Calculate total fats for the day
     */
    public double getTotalFats() {
        if (this.items == null) return 0;
        return this.items.stream().mapToDouble(FoodItem::getFats).sum();
    }
}
