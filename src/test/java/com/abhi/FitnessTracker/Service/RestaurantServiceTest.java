package com.abhi.FitnessTracker.Service;

import com.abhi.FitnessTracker.Model.Restaurant;
import com.abhi.FitnessTracker.Repository.RestaurantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RestaurantServiceTest {

    @Mock
    private RestaurantRepository restaurantRepository;

    @InjectMocks
    private RestaurantService restaurantService;

    private Restaurant testRestaurant;

    @BeforeEach
    void setUp() {
        testRestaurant = new Restaurant();
        testRestaurant.setId("rest-1");
        testRestaurant.setName("Pizza Place");
    }

    @Test
    void getAllRestaurants_returnsList() {
        when(restaurantRepository.findAll()).thenReturn(List.of(testRestaurant));

        List<Restaurant> result = restaurantService.getAllRestaurants();

        assertEquals(1, result.size());
        assertEquals("Pizza Place", result.get(0).getName());
    }

    @Test
    void getRestaurantById_found() {
        when(restaurantRepository.findById("rest-1")).thenReturn(Optional.of(testRestaurant));

        Restaurant result = restaurantService.getRestaurantById("rest-1");

        assertNotNull(result);
        assertEquals("Pizza Place", result.getName());
    }

    @Test
    void getRestaurantById_notFound_returnsNull() {
        when(restaurantRepository.findById("missing")).thenReturn(Optional.empty());

        Restaurant result = restaurantService.getRestaurantById("missing");

        assertNull(result);
    }

    @Test
    void saveRestaurant_savesAndReturns() {
        when(restaurantRepository.save(testRestaurant)).thenReturn(testRestaurant);

        Restaurant result = restaurantService.saveRestaurant(testRestaurant);

        assertEquals("rest-1", result.getId());
        verify(restaurantRepository).save(testRestaurant);
    }

    @Test
    void deleteRestaurant_callsDeleteById() {
        restaurantService.deleteRestaurant("rest-1");

        verify(restaurantRepository).deleteById("rest-1");
    }
}
