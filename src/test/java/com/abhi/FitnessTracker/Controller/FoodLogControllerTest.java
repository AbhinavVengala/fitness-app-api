package com.abhi.FitnessTracker.Controller;

import com.abhi.FitnessTracker.Model.FoodItem;
import com.abhi.FitnessTracker.Model.FoodLog;
import com.abhi.FitnessTracker.Service.FoodLogService;
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
class FoodLogControllerTest {

    @Mock
    private FoodLogService foodLogService;

    @InjectMocks
    private FoodLogController foodLogController;

    private FoodLog testLog;

    @BeforeEach
    void setUp() {
        testLog = new FoodLog();
        testLog.setId("log-1");
        testLog.setProfileId("profile-1");
        testLog.setDate(LocalDate.now());
    }

    @Test
    void getTodayLog_returnsLog() {
        when(foodLogService.getTodayLog("profile-1")).thenReturn(testLog);

        ResponseEntity<FoodLog> response = foodLogController.getTodayLog("profile-1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("log-1", response.getBody().getId());
    }

    @Test
    void getLogByDate_returnsLog() {
        when(foodLogService.getLogByDate("profile-1", LocalDate.of(2026, 3, 1))).thenReturn(testLog);

        ResponseEntity<FoodLog> response = foodLogController.getLogByDate("profile-1", "2026-03-01");

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void addFoodItem_returnsUpdatedLog() {
        FoodItem item = new FoodItem();
        item.setName("Banana");
        when(foodLogService.addFoodItem("profile-1", item)).thenReturn(testLog);

        ResponseEntity<FoodLog> response = foodLogController.addFoodItem("profile-1", item);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void removeFoodItem_returnsUpdatedLog() {
        when(foodLogService.removeFoodItem("profile-1", 42L)).thenReturn(testLog);

        ResponseEntity<FoodLog> response = foodLogController.removeFoodItem("profile-1", 42L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getAllLogs_returnsList() {
        when(foodLogService.getAllLogs("profile-1")).thenReturn(List.of(testLog));

        ResponseEntity<List<FoodLog>> response = foodLogController.getAllLogs("profile-1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void getLogsBetweenDates_returnsList() {
        when(foodLogService.getLogsBetweenDates("profile-1",
                LocalDate.of(2026, 3, 1), LocalDate.of(2026, 3, 7)))
                .thenReturn(List.of(testLog));

        ResponseEntity<List<FoodLog>> response = foodLogController
                .getLogsBetweenDates("profile-1", "2026-03-01", "2026-03-07");

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getDailyTotals_returnsTotalsMap() {
        FoodItem item = new FoodItem();
        item.setCalories(500);
        item.setProtein(30);
        item.setCarbs(50);
        item.setFats(15);
        testLog.addItem(item);
        when(foodLogService.getTodayLog("profile-1")).thenReturn(testLog);

        ResponseEntity<Map<String, Double>> response = foodLogController.getDailyTotals("profile-1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(500.0, response.getBody().get("calories"));
        assertEquals(30.0, response.getBody().get("protein"));
    }
}
