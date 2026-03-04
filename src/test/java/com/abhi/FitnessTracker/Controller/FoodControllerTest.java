package com.abhi.FitnessTracker.Controller;

import com.abhi.FitnessTracker.Model.Food;
import com.abhi.FitnessTracker.Model.User;
import com.abhi.FitnessTracker.Repository.UserRepository;
import com.abhi.FitnessTracker.Service.FoodService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FoodControllerTest {

    @Mock
    private FoodService foodService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private FoodController foodController;

    private Food sampleFood;

    @BeforeEach
    void setUp() {
        sampleFood = new Food();
        sampleFood.setId("food-1");
        sampleFood.setName("Apple");
        sampleFood.setCalories(95);
    }

    @Test
    void getAllFoods_returnsPage() {
        Page<Food> page = new PageImpl<>(List.of(sampleFood));
        when(foodService.getAll(isNull(), any())).thenReturn(page);

        ResponseEntity<Page<Food>> response = foodController.getAllFoods(null, 0, 20);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().getContent().size());
    }

    @Test
    void searchFoods_returnsResults() {
        Page<Food> page = new PageImpl<>(List.of(sampleFood));
        when(foodService.searchByName(eq("Apple"), isNull(), any())).thenReturn(page);

        ResponseEntity<Page<Food>> response = foodController.searchFoods("Apple", null, 0, 20);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getFoodsByCategory_returnsResults() {
        Page<Food> page = new PageImpl<>(List.of(sampleFood));
        when(foodService.getByCategory(eq("fruits"), isNull(), any())).thenReturn(page);

        ResponseEntity<Page<Food>> response = foodController.getFoodsByCategory("fruits", null, 0, 20);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void addFood_returnsCreatedFood() {
        when(foodService.addFood(sampleFood)).thenReturn(sampleFood);

        ResponseEntity<Food> response = foodController.addFood(sampleFood);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Apple", response.getBody().getName());
    }

    @Test
    void updateFood_existing_returnsUpdated() {
        when(foodService.updateFood("food-1", sampleFood)).thenReturn(sampleFood);

        ResponseEntity<?> response = foodController.updateFood("food-1", sampleFood);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void updateFood_notFound_returns404() {
        when(foodService.updateFood("missing", sampleFood)).thenReturn(null);

        ResponseEntity<?> response = foodController.updateFood("missing", sampleFood);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void deleteFood_success() {
        User adminUser = new User();
        adminUser.setId("admin-1");
        adminUser.setAdmin(true);

        when(authentication.getName()).thenReturn("admin@test.com");
        when(userRepository.findByEmail("admin@test.com")).thenReturn(Optional.of(adminUser));
        when(foodService.deleteFood("food-1", "admin-1", true)).thenReturn(true);

        ResponseEntity<?> response = foodController.deleteFood("food-1", authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void deleteFood_notFound_returns404() {
        User user = new User();
        user.setId("user-1");
        user.setAdmin(false);

        when(authentication.getName()).thenReturn("user@test.com");
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));
        when(foodService.deleteFood("missing", "user-1", false)).thenReturn(false);

        ResponseEntity<?> response = foodController.deleteFood("missing", authentication);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void deleteFood_error_returnsBadRequest() {
        when(authentication.getName()).thenReturn("user@test.com");
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.empty());

        ResponseEntity<?> response = foodController.deleteFood("food-1", authentication);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void getCategories_returnsList() {
        ResponseEntity<List<String>> response = foodController.getCategories();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().size() > 0);
    }

    @Test
    void addFoods_bulk_returnsList() {
        List<Food> foods = List.of(sampleFood);
        when(foodService.addFoods(foods)).thenReturn(foods);

        ResponseEntity<List<Food>> response = foodController.addFoods(foods);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void getFoodByBarcode_found() {
        when(foodService.getFoodByBarcode("123456")).thenReturn(sampleFood);

        ResponseEntity<Food> response = foodController.getFoodByBarcode("123456");

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getFoodByBarcode_notFound() {
        when(foodService.getFoodByBarcode("999999")).thenReturn(null);

        ResponseEntity<Food> response = foodController.getFoodByBarcode("999999");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
