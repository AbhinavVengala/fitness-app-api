package com.abhi.FitnessTracker.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Embedded document representing daily fitness goals for a profile.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Goals {
    private int calories;
    private int protein;
    private int carbs;
    private int fats;
    private int water;
}
