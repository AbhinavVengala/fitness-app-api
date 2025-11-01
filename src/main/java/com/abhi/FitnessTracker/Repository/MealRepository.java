package com.abhi.FitnessTracker.Repository;

import com.abhi.FitnessTracker.Model.Meal;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.List;

public interface MealRepository extends MongoRepository<Meal, String> {
    List<Meal> findByUserId(String userId);
    List<Meal> findByUserIdAndDate(String userId, LocalDate date);
}
