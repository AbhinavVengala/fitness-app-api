package com.abhi.FitnessTracker.Service;

import com.abhi.FitnessTracker.Model.FoodItem;
import com.abhi.FitnessTracker.Model.FoodLog;
import com.abhi.FitnessTracker.Repository.FoodLogRepository;
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
class FoodLogServiceTest {

    @Mock
    private FoodLogRepository foodLogRepository;

    @InjectMocks
    private FoodLogService foodLogService;

    private FoodLog existingLog;
    private final String PROFILE_ID = "profile-1";

    @BeforeEach
    void setUp() {
        existingLog = new FoodLog();
        existingLog.setId("log-1");
        existingLog.setProfileId(PROFILE_ID);
        existingLog.setDate(LocalDate.now());
    }

    // ========== getLogByDate ==========

    @Test
    void getLogByDate_existing_returnsLog() {
        when(foodLogRepository.findByProfileIdAndDate(PROFILE_ID, LocalDate.now()))
                .thenReturn(Optional.of(existingLog));

        FoodLog result = foodLogService.getLogByDate(PROFILE_ID, LocalDate.now());

        assertEquals("log-1", result.getId());
    }

    @Test
    void getLogByDate_notFound_createsNewUnsaved() {
        when(foodLogRepository.findByProfileIdAndDate(PROFILE_ID, LocalDate.now()))
                .thenReturn(Optional.empty());

        FoodLog result = foodLogService.getLogByDate(PROFILE_ID, LocalDate.now());

        assertNotNull(result);
        assertEquals(PROFILE_ID, result.getProfileId());
        assertEquals(LocalDate.now(), result.getDate());
        assertNull(result.getId()); // Not yet saved
    }

    // ========== addFoodItem ==========

    @Test
    void addFoodItem_generatesIdAndAdds() {
        when(foodLogRepository.findByProfileIdAndDate(eq(PROFILE_ID), any(LocalDate.class)))
                .thenReturn(Optional.of(existingLog));
        when(foodLogRepository.save(any(FoodLog.class))).thenAnswer(inv -> inv.getArgument(0));

        FoodItem item = new FoodItem();
        item.setName("Banana");
        item.setCalories(105);

        FoodLog result = foodLogService.addFoodItem(PROFILE_ID, item);

        assertNotNull(item.getId()); // ID was generated
        assertEquals(1, result.getItems().size());
        assertEquals("Banana", result.getItems().get(0).getName());
        verify(foodLogRepository).save(existingLog);
    }

    @Test
    void addFoodItem_keepsExistingId() {
        when(foodLogRepository.findByProfileIdAndDate(eq(PROFILE_ID), any(LocalDate.class)))
                .thenReturn(Optional.of(existingLog));
        when(foodLogRepository.save(any(FoodLog.class))).thenAnswer(inv -> inv.getArgument(0));

        FoodItem item = new FoodItem();
        item.setId(42L);
        item.setName("Apple");

        foodLogService.addFoodItem(PROFILE_ID, item);

        assertEquals(42L, item.getId());
    }

    // ========== removeFoodItem ==========

    @Test
    void removeFoodItem_removesFromLog() {
        FoodItem item = new FoodItem();
        item.setId(1L);
        existingLog.addItem(item);

        when(foodLogRepository.findByProfileIdAndDate(eq(PROFILE_ID), any(LocalDate.class)))
                .thenReturn(Optional.of(existingLog));
        when(foodLogRepository.save(any(FoodLog.class))).thenAnswer(inv -> inv.getArgument(0));

        FoodLog result = foodLogService.removeFoodItem(PROFILE_ID, 1L);

        assertEquals(0, result.getItems().size());
        verify(foodLogRepository).save(existingLog);
    }

    // ========== getAllLogs ==========

    @Test
    void getAllLogs_returnsAll() {
        when(foodLogRepository.findByProfileId(PROFILE_ID)).thenReturn(List.of(existingLog));

        List<FoodLog> result = foodLogService.getAllLogs(PROFILE_ID);

        assertEquals(1, result.size());
    }

    // ========== getLogsBetweenDates ==========

    @Test
    void getLogsBetweenDates_filtersCorrectly() {
        FoodLog log1 = new FoodLog();
        log1.setDate(LocalDate.of(2026, 3, 1));

        FoodLog log2 = new FoodLog();
        log2.setDate(LocalDate.of(2026, 3, 5));

        FoodLog log3 = new FoodLog();
        log3.setDate(LocalDate.of(2026, 3, 10));

        when(foodLogRepository.findByProfileId(PROFILE_ID))
                .thenReturn(List.of(log1, log2, log3));

        List<FoodLog> result = foodLogService.getLogsBetweenDates(
                PROFILE_ID, LocalDate.of(2026, 3, 2), LocalDate.of(2026, 3, 8));

        assertEquals(1, result.size());
        assertEquals(LocalDate.of(2026, 3, 5), result.get(0).getDate());
    }

    @Test
    void getLogsBetweenDates_includesBoundaryDates() {
        FoodLog log = new FoodLog();
        log.setDate(LocalDate.of(2026, 3, 1));

        when(foodLogRepository.findByProfileId(PROFILE_ID))
                .thenReturn(List.of(log));

        List<FoodLog> result = foodLogService.getLogsBetweenDates(
                PROFILE_ID, LocalDate.of(2026, 3, 1), LocalDate.of(2026, 3, 1));

        assertEquals(1, result.size());
    }

    @Test
    void getLogsBetweenDates_skipsNullDates() {
        FoodLog logWithNullDate = new FoodLog();
        logWithNullDate.setDate(null);

        FoodLog validLog = new FoodLog();
        validLog.setDate(LocalDate.of(2026, 3, 5));

        when(foodLogRepository.findByProfileId(PROFILE_ID))
                .thenReturn(List.of(logWithNullDate, validLog));

        List<FoodLog> result = foodLogService.getLogsBetweenDates(
                PROFILE_ID, LocalDate.of(2026, 3, 1), LocalDate.of(2026, 3, 10));

        assertEquals(1, result.size());
    }
}
