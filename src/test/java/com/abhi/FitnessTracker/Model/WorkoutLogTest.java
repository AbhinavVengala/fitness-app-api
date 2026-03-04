package com.abhi.FitnessTracker.Model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WorkoutLogTest {

    private WorkoutLog workoutLog;

    @BeforeEach
    void setUp() {
        workoutLog = new WorkoutLog();
        workoutLog.setProfileId("profile-1");
    }

    @Test
    void addWorkout_addsToList() {
        WorkoutEntry entry = new WorkoutEntry();
        entry.setId("w1");
        entry.setName("Push Ups");
        entry.setCaloriesBurned(50);

        workoutLog.addWorkout(entry);

        assertEquals(1, workoutLog.getWorkouts().size());
        assertEquals("Push Ups", workoutLog.getWorkouts().get(0).getName());
    }

    @Test
    void addWorkout_nullWorkoutsList_initializesAndAdds() {
        workoutLog.setWorkouts(null);

        WorkoutEntry entry = new WorkoutEntry();
        entry.setId("w1");
        workoutLog.addWorkout(entry);

        assertNotNull(workoutLog.getWorkouts());
        assertEquals(1, workoutLog.getWorkouts().size());
    }

    @Test
    void updateWorkout_existingWorkout_returnsTrue() {
        WorkoutEntry entry = new WorkoutEntry();
        entry.setId("w1");
        entry.setName("Push Ups");
        entry.setCaloriesBurned(50);
        workoutLog.addWorkout(entry);

        WorkoutEntry updated = new WorkoutEntry();
        updated.setId("w1");
        updated.setName("Push Ups");
        updated.setCaloriesBurned(100);

        assertTrue(workoutLog.updateWorkout("w1", updated));
        assertEquals(100, workoutLog.getWorkouts().get(0).getCaloriesBurned());
    }

    @Test
    void updateWorkout_nonExistingWorkout_returnsFalse() {
        WorkoutEntry entry = new WorkoutEntry();
        entry.setId("w1");
        workoutLog.addWorkout(entry);

        WorkoutEntry updated = new WorkoutEntry();
        updated.setId("w999");

        assertFalse(workoutLog.updateWorkout("w999", updated));
    }

    @Test
    void updateWorkout_nullWorkouts_returnsFalse() {
        workoutLog.setWorkouts(null);

        WorkoutEntry updated = new WorkoutEntry();
        updated.setId("w1");

        assertFalse(workoutLog.updateWorkout("w1", updated));
    }

    @Test
    void removeWorkout_existingWorkout_returnsTrue() {
        WorkoutEntry entry = new WorkoutEntry();
        entry.setId("w1");
        workoutLog.addWorkout(entry);

        assertTrue(workoutLog.removeWorkout("w1"));
        assertEquals(0, workoutLog.getWorkouts().size());
    }

    @Test
    void removeWorkout_nonExistingWorkout_returnsFalse() {
        WorkoutEntry entry = new WorkoutEntry();
        entry.setId("w1");
        workoutLog.addWorkout(entry);

        assertFalse(workoutLog.removeWorkout("w999"));
        assertEquals(1, workoutLog.getWorkouts().size());
    }

    @Test
    void removeWorkout_nullWorkouts_returnsFalse() {
        workoutLog.setWorkouts(null);
        assertFalse(workoutLog.removeWorkout("w1"));
    }

    @Test
    void getTotalCaloriesBurned_sumsCorrectly() {
        WorkoutEntry e1 = new WorkoutEntry();
        e1.setId("w1");
        e1.setCaloriesBurned(150);

        WorkoutEntry e2 = new WorkoutEntry();
        e2.setId("w2");
        e2.setCaloriesBurned(200.5);

        workoutLog.addWorkout(e1);
        workoutLog.addWorkout(e2);

        assertEquals(350.5, workoutLog.getTotalCaloriesBurned());
    }

    @Test
    void getTotalCaloriesBurned_emptyList_returnsZero() {
        assertEquals(0.0, workoutLog.getTotalCaloriesBurned());
    }

    @Test
    void getTotalCaloriesBurned_nullWorkouts_returnsZero() {
        workoutLog.setWorkouts(null);
        assertEquals(0.0, workoutLog.getTotalCaloriesBurned());
    }
}
