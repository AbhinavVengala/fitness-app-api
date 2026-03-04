package com.abhi.FitnessTracker.Model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FoodLogTest {

    private FoodLog foodLog;

    @BeforeEach
    void setUp() {
        foodLog = new FoodLog();
        foodLog.setProfileId("profile-1");
    }

    @Test
    void addItem_addsToList() {
        FoodItem item = new FoodItem();
        item.setId(1L);
        item.setName("Chicken Breast");
        item.setCalories(165);
        item.setProtein(31);

        foodLog.addItem(item);

        assertEquals(1, foodLog.getItems().size());
        assertEquals("Chicken Breast", foodLog.getItems().get(0).getName());
    }

    @Test
    void addItem_nullItemsList_initializesAndAdds() {
        foodLog.setItems(null);

        FoodItem item = new FoodItem();
        item.setId(1L);
        foodLog.addItem(item);

        assertNotNull(foodLog.getItems());
        assertEquals(1, foodLog.getItems().size());
    }

    @Test
    void removeItem_existingItem_returnsTrue() {
        FoodItem item = new FoodItem();
        item.setId(42L);
        foodLog.addItem(item);

        assertTrue(foodLog.removeItem(42L));
        assertEquals(0, foodLog.getItems().size());
    }

    @Test
    void removeItem_nonExistingItem_returnsFalse() {
        FoodItem item = new FoodItem();
        item.setId(1L);
        foodLog.addItem(item);

        assertFalse(foodLog.removeItem(999L));
        assertEquals(1, foodLog.getItems().size());
    }

    @Test
    void removeItem_nullItems_returnsFalse() {
        foodLog.setItems(null);
        assertFalse(foodLog.removeItem(1L));
    }

    @Test
    void getTotalCalories_sumsCorrectly() {
        FoodItem item1 = new FoodItem();
        item1.setCalories(200);
        FoodItem item2 = new FoodItem();
        item2.setCalories(350);

        foodLog.addItem(item1);
        foodLog.addItem(item2);

        assertEquals(550.0, foodLog.getTotalCalories());
    }

    @Test
    void getTotalCalories_emptyList_returnsZero() {
        assertEquals(0.0, foodLog.getTotalCalories());
    }

    @Test
    void getTotalCalories_nullItems_returnsZero() {
        foodLog.setItems(null);
        assertEquals(0.0, foodLog.getTotalCalories());
    }

    @Test
    void getTotalProtein_sumsCorrectly() {
        FoodItem item1 = new FoodItem();
        item1.setProtein(30);
        FoodItem item2 = new FoodItem();
        item2.setProtein(20);

        foodLog.addItem(item1);
        foodLog.addItem(item2);

        assertEquals(50.0, foodLog.getTotalProtein());
    }

    @Test
    void getTotalCarbs_sumsCorrectly() {
        FoodItem item1 = new FoodItem();
        item1.setCarbs(40);
        FoodItem item2 = new FoodItem();
        item2.setCarbs(60);

        foodLog.addItem(item1);
        foodLog.addItem(item2);

        assertEquals(100.0, foodLog.getTotalCarbs());
    }

    @Test
    void getTotalFats_sumsCorrectly() {
        FoodItem item1 = new FoodItem();
        item1.setFats(10);
        FoodItem item2 = new FoodItem();
        item2.setFats(15.5);

        foodLog.addItem(item1);
        foodLog.addItem(item2);

        assertEquals(25.5, foodLog.getTotalFats());
    }
}
