package com.abhi.FitnessTracker.Repository;

import com.abhi.FitnessTracker.Model.Workout;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.List;

public interface WorkoutRepository extends MongoRepository<Workout, String> {
    List<Workout> findByUserId(String userId);
    List<Workout> findByUserIdAndDate(String userId, LocalDate date);
}

