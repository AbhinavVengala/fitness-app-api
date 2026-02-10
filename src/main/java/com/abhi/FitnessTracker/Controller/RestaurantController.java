package com.abhi.FitnessTracker.Controller;

import com.abhi.FitnessTracker.Model.Restaurant;
import com.abhi.FitnessTracker.Service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/restaurants")
public class RestaurantController {

    @Autowired
    private RestaurantService restaurantService;

    @GetMapping
    public ResponseEntity<List<Restaurant>> getAllRestaurants() {
        return ResponseEntity.ok(restaurantService.getAllRestaurants());
    }

    @PostMapping
    public ResponseEntity<Restaurant> createRestaurant(@RequestBody Restaurant restaurant) {
        return ResponseEntity.ok(restaurantService.saveRestaurant(restaurant));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Restaurant> updateRestaurant(@PathVariable String id, @RequestBody Restaurant restaurant) {
        Restaurant existing = restaurantService.getRestaurantById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        restaurant.setId(id);
        return ResponseEntity.ok(restaurantService.saveRestaurant(restaurant));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRestaurant(@PathVariable String id) {
        if (restaurantService.getRestaurantById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        restaurantService.deleteRestaurant(id);
        return ResponseEntity.ok().build();
    }
}
