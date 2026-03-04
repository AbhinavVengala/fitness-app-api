package com.abhi.FitnessTracker.Service;

import com.abhi.FitnessTracker.Model.Food;
import com.abhi.FitnessTracker.Repository.FoodRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FoodServiceTest {

    @Mock
    private FoodRepository foodRepository;

    @InjectMocks
    private FoodService foodService;

    private Food sampleFood;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        sampleFood = new Food();
        sampleFood.setId("food-1");
        sampleFood.setName("Chicken Breast");
        sampleFood.setCalories(165);
        sampleFood.setProtein(31);
        sampleFood.setCarbs(0);
        sampleFood.setFats(3.6);
        sampleFood.setCategory("protein");

        pageable = PageRequest.of(0, 20);
    }

    // ========== searchByName ==========

    @Test
    void searchByName_withQueryAndUserId_delegatesToRepo() {
        Page<Food> page = new PageImpl<>(List.of(sampleFood));
        when(foodRepository.searchByNameAndUser("Chicken", "user-1", pageable)).thenReturn(page);

        Page<Food> result = foodService.searchByName("Chicken", "user-1", pageable);

        assertEquals(1, result.getContent().size());
        assertEquals("Chicken Breast", result.getContent().get(0).getName());
    }

    @Test
    void searchByName_withQueryNoUserId_searchesSystemOnly() {
        Page<Food> page = new PageImpl<>(List.of(sampleFood));
        when(foodRepository.findByNameContainingIgnoreCaseAndCreatedByUserIdIsNull("Chicken", pageable)).thenReturn(page);

        Page<Food> result = foodService.searchByName("Chicken", null, pageable);

        assertEquals(1, result.getContent().size());
    }

    @Test
    void searchByName_emptyQuery_returnsEmptyPage() {
        Page<Food> result = foodService.searchByName("", "user-1", pageable);

        assertTrue(result.isEmpty());
        verifyNoInteractions(foodRepository);
    }

    @Test
    void searchByName_nullQuery_returnsEmptyPage() {
        Page<Food> result = foodService.searchByName(null, "user-1", pageable);

        assertTrue(result.isEmpty());
    }

    // ========== getAll ==========

    @Test
    void getAll_withUserId_returnsSystemAndUserFoods() {
        Page<Food> page = new PageImpl<>(List.of(sampleFood));
        when(foodRepository.findSystemAndUserFoods("user-1", pageable)).thenReturn(page);

        Page<Food> result = foodService.getAll("user-1", pageable);

        assertEquals(1, result.getContent().size());
    }

    @Test
    void getAll_noUserId_returnsSystemFoodsOnly() {
        Page<Food> page = new PageImpl<>(List.of(sampleFood));
        when(foodRepository.findByCreatedByUserIdIsNull(pageable)).thenReturn(page);

        Page<Food> result = foodService.getAll(null, pageable);

        assertEquals(1, result.getContent().size());
    }

    // ========== getByCategory ==========

    @Test
    void getByCategory_withUserId() {
        Page<Food> page = new PageImpl<>(List.of(sampleFood));
        when(foodRepository.findByCategoryAndUser("protein", "user-1", pageable)).thenReturn(page);

        Page<Food> result = foodService.getByCategory("protein", "user-1", pageable);

        assertEquals(1, result.getContent().size());
    }

    @Test
    void getByCategory_noUserId() {
        Page<Food> page = new PageImpl<>(List.of(sampleFood));
        when(foodRepository.findByCategoryAndCreatedByUserIdIsNull("protein", pageable)).thenReturn(page);

        Page<Food> result = foodService.getByCategory("protein", null, pageable);

        assertEquals(1, result.getContent().size());
    }

    // ========== addFood / addFoods ==========

    @Test
    void addFood_savesAndReturns() {
        when(foodRepository.save(sampleFood)).thenReturn(sampleFood);

        Food result = foodService.addFood(sampleFood);

        assertEquals("food-1", result.getId());
        verify(foodRepository).save(sampleFood);
    }

    @Test
    void addFoods_savesAllAndReturns() {
        List<Food> foods = List.of(sampleFood);
        when(foodRepository.saveAll(foods)).thenReturn(foods);

        List<Food> result = foodService.addFoods(foods);

        assertEquals(1, result.size());
    }

    // ========== updateFood ==========

    @Test
    void updateFood_existingFood_updatesAndReturns() {
        when(foodRepository.findById("food-1")).thenReturn(Optional.of(sampleFood));
        when(foodRepository.save(any(Food.class))).thenAnswer(inv -> inv.getArgument(0));

        Food updated = new Food();
        updated.setName("Updated Chicken");
        updated.setCalories(170);

        Food result = foodService.updateFood("food-1", updated);

        assertNotNull(result);
        assertEquals("food-1", result.getId());
        verify(foodRepository).save(updated);
    }

    @Test
    void updateFood_notFound_returnsNull() {
        when(foodRepository.findById("missing")).thenReturn(Optional.empty());

        Food result = foodService.updateFood("missing", sampleFood);

        assertNull(result);
        verify(foodRepository, never()).save(any());
    }

    // ========== deleteFood ==========

    @Test
    void deleteFood_asAdmin_deletesAny() {
        when(foodRepository.findById("food-1")).thenReturn(Optional.of(sampleFood));

        boolean result = foodService.deleteFood("food-1", "admin-user", true);

        assertTrue(result);
        verify(foodRepository).deleteById("food-1");
    }

    @Test
    void deleteFood_asOwner_deletesOwn() {
        Food userFood = new Food();
        userFood.setId("food-2");
        userFood.setCreatedByUserId("user-1");
        when(foodRepository.findById("food-2")).thenReturn(Optional.of(userFood));

        boolean result = foodService.deleteFood("food-2", "user-1", false);

        assertTrue(result);
        verify(foodRepository).deleteById("food-2");
    }

    @Test
    void deleteFood_systemFood_regularUser_throwsException() {
        // System food has null createdByUserId
        when(foodRepository.findById("food-1")).thenReturn(Optional.of(sampleFood));

        assertThrows(RuntimeException.class,
                () -> foodService.deleteFood("food-1", "user-1", false));
    }

    @Test
    void deleteFood_otherUsersFood_throwsException() {
        Food otherUserFood = new Food();
        otherUserFood.setId("food-3");
        otherUserFood.setCreatedByUserId("other-user");
        when(foodRepository.findById("food-3")).thenReturn(Optional.of(otherUserFood));

        assertThrows(RuntimeException.class,
                () -> foodService.deleteFood("food-3", "user-1", false));
    }

    @Test
    void deleteFood_notFound_returnsFalse() {
        when(foodRepository.findById("missing")).thenReturn(Optional.empty());

        boolean result = foodService.deleteFood("missing", "user-1", false);

        assertFalse(result);
    }
}
