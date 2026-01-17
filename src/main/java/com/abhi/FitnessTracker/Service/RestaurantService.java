package com.abhi.FitnessTracker.Service;

import com.abhi.FitnessTracker.Model.Restaurant;
import com.abhi.FitnessTracker.Repository.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RestaurantService {

    @Autowired
    private RestaurantRepository restaurantRepository;

    public List<Restaurant> getAllRestaurants() {
        return restaurantRepository.findAll();
    }
}
