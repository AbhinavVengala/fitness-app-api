package com.abhi.FitnessTracker.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;

/**
 * Embedded document representing a user profile.
 * A user can have multiple profiles (e.g., for different family members).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Profile {
    private String id;
    private String name;
    private int age;
    private double weight;
    private double height;
    private String gender;
    private String fitnessGoal;
    private String experienceLevel;
    private Goals goals;
    private int waterIntake;
    private String lastWaterDate;
    private Map<String, Integer> waterIntakeHistory;
}
