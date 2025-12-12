package com.abhi.FitnessTracker.Repository;

import com.abhi.FitnessTracker.Model.WorkoutLog;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface WorkoutLogRepository extends MongoRepository<WorkoutLog, String> {
    Optional<WorkoutLog> findByProfileIdAndDate(String profileId, LocalDate date);
    List<WorkoutLog> findByProfileId(String profileId);
    List<WorkoutLog> findByProfileIdAndDateBetween(String profileId, LocalDate startDate, LocalDate endDate);
}
