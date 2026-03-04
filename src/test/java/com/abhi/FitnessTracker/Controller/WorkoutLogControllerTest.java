package com.abhi.FitnessTracker.Controller;

import com.abhi.FitnessTracker.Model.WorkoutEntry;
import com.abhi.FitnessTracker.Model.WorkoutLog;
import com.abhi.FitnessTracker.Service.WorkoutLogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorkoutLogControllerTest {

    @Mock
    private WorkoutLogService workoutLogService;

    @InjectMocks
    private WorkoutLogController workoutLogController;

    private WorkoutLog testLog;

    @BeforeEach
    void setUp() {
        testLog = new WorkoutLog();
        testLog.setId("wlog-1");
        testLog.setProfileId("profile-1");
        testLog.setDate(LocalDate.now());
    }

    @Test
    void getTodayLog_returnsLog() {
        when(workoutLogService.getTodayLog("profile-1")).thenReturn(testLog);

        ResponseEntity<WorkoutLog> response = workoutLogController.getTodayLog("profile-1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getLogByDate_returnsLog() {
        when(workoutLogService.getLogByDate("profile-1", LocalDate.of(2026, 3, 1))).thenReturn(testLog);

        ResponseEntity<WorkoutLog> response = workoutLogController.getLogByDate("profile-1", "2026-03-01");

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void addWorkout_returnsUpdatedLog() {
        WorkoutEntry entry = new WorkoutEntry();
        entry.setId("w1");
        when(workoutLogService.addWorkout("profile-1", entry)).thenReturn(testLog);

        ResponseEntity<WorkoutLog> response = workoutLogController.addWorkout("profile-1", entry);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void updateWorkout_success_returnsOk() {
        WorkoutEntry entry = new WorkoutEntry();
        when(workoutLogService.updateWorkout("profile-1", "w1", entry)).thenReturn(testLog);

        ResponseEntity<?> response = workoutLogController.updateWorkout("profile-1", "w1", entry);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void updateWorkout_notFound_returnsBadRequest() {
        WorkoutEntry entry = new WorkoutEntry();
        when(workoutLogService.updateWorkout("profile-1", "w999", entry))
                .thenThrow(new RuntimeException("Workout not found"));

        ResponseEntity<?> response = workoutLogController.updateWorkout("profile-1", "w999", entry);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void removeWorkout_returnsUpdatedLog() {
        when(workoutLogService.removeWorkout("profile-1", "w1")).thenReturn(testLog);

        ResponseEntity<WorkoutLog> response = workoutLogController.removeWorkout("profile-1", "w1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getAllLogs_returnsList() {
        when(workoutLogService.getAllLogs("profile-1")).thenReturn(List.of(testLog));

        ResponseEntity<List<WorkoutLog>> response = workoutLogController.getAllLogs("profile-1");

        assertEquals(1, response.getBody().size());
    }

    @Test
    void getLogsBetweenDates_returnsList() {
        when(workoutLogService.getLogsBetweenDates("profile-1",
                LocalDate.of(2026, 3, 1), LocalDate.of(2026, 3, 7)))
                .thenReturn(List.of(testLog));

        ResponseEntity<List<WorkoutLog>> response = workoutLogController
                .getLogsBetweenDates("profile-1", "2026-03-01", "2026-03-07");

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getCaloriesBurned_returnsTotalMap() {
        WorkoutEntry e = new WorkoutEntry();
        e.setId("w1");
        e.setCaloriesBurned(250);
        testLog.addWorkout(e);
        when(workoutLogService.getTodayLog("profile-1")).thenReturn(testLog);

        ResponseEntity<Map<String, Double>> response = workoutLogController.getCaloriesBurned("profile-1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(250.0, response.getBody().get("caloriesBurned"));
    }
}
