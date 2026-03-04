package com.abhi.FitnessTracker.Service;

import com.abhi.FitnessTracker.Model.WorkoutEntry;
import com.abhi.FitnessTracker.Model.WorkoutLog;
import com.abhi.FitnessTracker.Repository.WorkoutLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorkoutLogServiceTest {

    @Mock
    private WorkoutLogRepository workoutLogRepository;

    @InjectMocks
    private WorkoutLogService workoutLogService;

    private WorkoutLog existingLog;
    private final String PROFILE_ID = "profile-1";

    @BeforeEach
    void setUp() {
        existingLog = new WorkoutLog();
        existingLog.setId("wlog-1");
        existingLog.setProfileId(PROFILE_ID);
        existingLog.setDate(LocalDate.now());
    }

    // ========== getLogByDate ==========

    @Test
    void getLogByDate_existing_returnsLog() {
        when(workoutLogRepository.findByProfileIdAndDate(PROFILE_ID, LocalDate.now()))
                .thenReturn(Optional.of(existingLog));

        WorkoutLog result = workoutLogService.getLogByDate(PROFILE_ID, LocalDate.now());

        assertEquals("wlog-1", result.getId());
    }

    @Test
    void getLogByDate_notFound_createsNewUnsaved() {
        when(workoutLogRepository.findByProfileIdAndDate(PROFILE_ID, LocalDate.now()))
                .thenReturn(Optional.empty());

        WorkoutLog result = workoutLogService.getLogByDate(PROFILE_ID, LocalDate.now());

        assertNotNull(result);
        assertEquals(PROFILE_ID, result.getProfileId());
        assertNull(result.getId());
    }

    // ========== addWorkout ==========

    @Test
    void addWorkout_newWorkout_addsToLog() {
        when(workoutLogRepository.findByProfileIdAndDate(eq(PROFILE_ID), any(LocalDate.class)))
                .thenReturn(Optional.of(existingLog));
        when(workoutLogRepository.save(any(WorkoutLog.class))).thenAnswer(inv -> inv.getArgument(0));

        WorkoutEntry entry = new WorkoutEntry();
        entry.setId("w1");
        entry.setName("Push Ups");
        entry.setCaloriesBurned(50);

        WorkoutLog result = workoutLogService.addWorkout(PROFILE_ID, entry);

        assertEquals(1, result.getWorkouts().size());
        assertEquals("Push Ups", result.getWorkouts().get(0).getName());
        verify(workoutLogRepository).save(existingLog);
    }

    @Test
    void addWorkout_existingWorkoutId_updatesInstead() {
        WorkoutEntry existing = new WorkoutEntry();
        existing.setId("w1");
        existing.setName("Push Ups");
        existing.setCaloriesBurned(50);
        existingLog.addWorkout(existing);

        when(workoutLogRepository.findByProfileIdAndDate(eq(PROFILE_ID), any(LocalDate.class)))
                .thenReturn(Optional.of(existingLog));
        when(workoutLogRepository.save(any(WorkoutLog.class))).thenAnswer(inv -> inv.getArgument(0));

        WorkoutEntry updated = new WorkoutEntry();
        updated.setId("w1");
        updated.setName("Push Ups");
        updated.setCaloriesBurned(100);

        WorkoutLog result = workoutLogService.addWorkout(PROFILE_ID, updated);

        assertEquals(1, result.getWorkouts().size());
        assertEquals(100, result.getWorkouts().get(0).getCaloriesBurned());
    }

    // ========== updateWorkout ==========

    @Test
    void updateWorkout_found_updatesSuccessfully() {
        WorkoutEntry existing = new WorkoutEntry();
        existing.setId("w1");
        existing.setCaloriesBurned(50);
        existingLog.addWorkout(existing);

        when(workoutLogRepository.findByProfileIdAndDate(eq(PROFILE_ID), any(LocalDate.class)))
                .thenReturn(Optional.of(existingLog));
        when(workoutLogRepository.save(any(WorkoutLog.class))).thenAnswer(inv -> inv.getArgument(0));

        WorkoutEntry updated = new WorkoutEntry();
        updated.setCaloriesBurned(200);

        WorkoutLog result = workoutLogService.updateWorkout(PROFILE_ID, "w1", updated);

        assertEquals(200, result.getWorkouts().get(0).getCaloriesBurned());
        assertEquals("w1", result.getWorkouts().get(0).getId());
    }

    @Test
    void updateWorkout_notFound_throwsException() {
        when(workoutLogRepository.findByProfileIdAndDate(eq(PROFILE_ID), any(LocalDate.class)))
                .thenReturn(Optional.of(existingLog));

        WorkoutEntry updated = new WorkoutEntry();

        assertThrows(RuntimeException.class,
                () -> workoutLogService.updateWorkout(PROFILE_ID, "nonexistent", updated));
    }

    // ========== removeWorkout ==========

    @Test
    void removeWorkout_removesFromLog() {
        WorkoutEntry existing = new WorkoutEntry();
        existing.setId("w1");
        existingLog.addWorkout(existing);

        when(workoutLogRepository.findByProfileIdAndDate(eq(PROFILE_ID), any(LocalDate.class)))
                .thenReturn(Optional.of(existingLog));
        when(workoutLogRepository.save(any(WorkoutLog.class))).thenAnswer(inv -> inv.getArgument(0));

        WorkoutLog result = workoutLogService.removeWorkout(PROFILE_ID, "w1");

        assertEquals(0, result.getWorkouts().size());
        verify(workoutLogRepository).save(existingLog);
    }

    // ========== getAllLogs ==========

    @Test
    void getAllLogs_returnsAll() {
        when(workoutLogRepository.findByProfileId(PROFILE_ID)).thenReturn(List.of(existingLog));

        List<WorkoutLog> result = workoutLogService.getAllLogs(PROFILE_ID);

        assertEquals(1, result.size());
    }

    // ========== getLogsBetweenDates ==========

    @Test
    void getLogsBetweenDates_filtersCorrectly() {
        WorkoutLog log1 = new WorkoutLog();
        log1.setDate(LocalDate.of(2026, 3, 1));

        WorkoutLog log2 = new WorkoutLog();
        log2.setDate(LocalDate.of(2026, 3, 5));

        WorkoutLog log3 = new WorkoutLog();
        log3.setDate(LocalDate.of(2026, 3, 10));

        when(workoutLogRepository.findByProfileId(PROFILE_ID))
                .thenReturn(List.of(log1, log2, log3));

        List<WorkoutLog> result = workoutLogService.getLogsBetweenDates(
                PROFILE_ID, LocalDate.of(2026, 3, 2), LocalDate.of(2026, 3, 8));

        assertEquals(1, result.size());
        assertEquals(LocalDate.of(2026, 3, 5), result.get(0).getDate());
    }

    @Test
    void getLogsBetweenDates_skipsNullDates() {
        WorkoutLog logNull = new WorkoutLog();
        logNull.setDate(null);

        WorkoutLog logValid = new WorkoutLog();
        logValid.setDate(LocalDate.of(2026, 3, 5));

        when(workoutLogRepository.findByProfileId(PROFILE_ID))
                .thenReturn(List.of(logNull, logValid));

        List<WorkoutLog> result = workoutLogService.getLogsBetweenDates(
                PROFILE_ID, LocalDate.of(2026, 3, 1), LocalDate.of(2026, 3, 10));

        assertEquals(1, result.size());
    }
}
