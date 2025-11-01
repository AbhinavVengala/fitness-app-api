package com.abhi.FitnessTracker.Service;

import com.abhi.FitnessTracker.Model.Workout;
import com.abhi.FitnessTracker.Repository.WorkoutRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class WorkoutService {
    private final WorkoutRepository repo;

    public WorkoutService(WorkoutRepository repo) {
        this.repo = repo;
    }

    public Workout save(Workout workout) { return repo.save(workout); }

    public List<Workout> findByUser(String userId) { return repo.findByUserId(userId); }

    public List<Workout> findByDate(String userId, LocalDate date) {
        return repo.findByUserIdAndDate(userId, date);
    }
}
