package com.abhi.FitnessTracker.Controller;

import com.abhi.FitnessTracker.Model.Restaurant;
import com.abhi.FitnessTracker.Service.RestaurantService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RestaurantControllerTest {

    @Mock
    private RestaurantService restaurantService;

    @InjectMocks
    private RestaurantController restaurantController;

    private Restaurant testRestaurant;

    @BeforeEach
    void setUp() {
        testRestaurant = new Restaurant();
        testRestaurant.setId("rest-1");
        testRestaurant.setName("Pizza Place");
    }

    @Test
    void getAllRestaurants_returnsList() {
        when(restaurantService.getAllRestaurants()).thenReturn(List.of(testRestaurant));

        ResponseEntity<List<Restaurant>> response = restaurantController.getAllRestaurants();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void createRestaurant_returnsCreated() {
        when(restaurantService.saveRestaurant(testRestaurant)).thenReturn(testRestaurant);

        ResponseEntity<Restaurant> response = restaurantController.createRestaurant(testRestaurant);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Pizza Place", response.getBody().getName());
    }

    @Test
    void updateRestaurant_existing_returnsUpdated() {
        when(restaurantService.getRestaurantById("rest-1")).thenReturn(testRestaurant);
        when(restaurantService.saveRestaurant(any())).thenReturn(testRestaurant);

        ResponseEntity<Restaurant> response = restaurantController.updateRestaurant("rest-1", testRestaurant);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void updateRestaurant_notFound_returns404() {
        when(restaurantService.getRestaurantById("missing")).thenReturn(null);

        ResponseEntity<Restaurant> response = restaurantController.updateRestaurant("missing", testRestaurant);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void deleteRestaurant_existing_returnsOk() {
        when(restaurantService.getRestaurantById("rest-1")).thenReturn(testRestaurant);

        ResponseEntity<Void> response = restaurantController.deleteRestaurant("rest-1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(restaurantService).deleteRestaurant("rest-1");
    }

    @Test
    void deleteRestaurant_notFound_returns404() {
        when(restaurantService.getRestaurantById("missing")).thenReturn(null);

        ResponseEntity<Void> response = restaurantController.deleteRestaurant("missing");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
