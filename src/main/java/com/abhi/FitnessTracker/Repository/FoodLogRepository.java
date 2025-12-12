package com.abhi.FitnessTracker.Repository;

import com.abhi.FitnessTracker.Model.FoodLog;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface FoodLogRepository extends MongoRepository<FoodLog, String> {
    Optional<FoodLog> findByProfileIdAndDate(String profileId, LocalDate date);
    List<FoodLog> findByProfileId(String profileId);
    List<FoodLog> findByProfileIdAndDateBetween(String profileId, LocalDate startDate, LocalDate endDate);
}
