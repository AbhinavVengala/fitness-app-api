package com.abhi.FitnessTracker.Controller;

import com.abhi.FitnessTracker.Model.Meal;
import com.abhi.FitnessTracker.Service.MealService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/meals")
public class MealController {
    private final MealService service;

    public MealController(MealService service) {
        this.service = service;
    }

    @PostMapping
    public Meal addMeal(@RequestBody Meal meal) {
        return service.save(meal);
    }

    @GetMapping("/{userId}")
    public List<Meal> getMeals(@PathVariable String userId) {
        return service.findByUser(userId);
    }

    @GetMapping("/{userId}/{date}")
    public List<Meal> getMealsByDate(@PathVariable String userId, @PathVariable String date) {
        return service.findByDate(userId, LocalDate.parse(date));
    }
}

