package com.abhi.FitnessTracker.Service;

import com.abhi.FitnessTracker.Model.Meal;
import com.abhi.FitnessTracker.Repository.MealRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class MealService {
    private final MealRepository repo;

    public MealService(MealRepository repo) {
        this.repo = repo;
    }

    public Meal save(Meal meal) { return repo.save(meal); }

    public List<Meal> findByUser(String userId) { return repo.findByUserId(userId); }

    public List<Meal> findByDate(String userId, LocalDate date) {
        return repo.findByUserIdAndDate(userId, date);
    }
}
