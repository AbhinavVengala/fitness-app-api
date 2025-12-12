package com.abhi.FitnessTracker.Repository;

import com.abhi.FitnessTracker.Model.Exercise;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ExerciseRepository extends MongoRepository<Exercise, String> {
    List<Exercise> findByCategory(String category);
    Optional<Exercise> findByName(String name);
}
