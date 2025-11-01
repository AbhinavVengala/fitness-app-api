package com.abhi.FitnessTracker.Controller;

import com.abhi.FitnessTracker.Model.Workout;
import com.abhi.FitnessTracker.Service.WorkoutService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/workouts")
public class WorkoutController {
    private final WorkoutService service;

    public WorkoutController(WorkoutService service) {
        this.service = service;
    }

    @PostMapping
    public Workout addWorkout(@RequestBody Workout workout) {
        return service.save(workout);
    }

    @GetMapping("/{userId}")
    public List<Workout> getWorkouts(@PathVariable String userId) {
        return service.findByUser(userId);
    }

    @GetMapping("/{userId}/{date}")
    public List<Workout> getWorkoutsByDate(@PathVariable String userId, @PathVariable String date) {
        return service.findByDate(userId, LocalDate.parse(date));
    }
}

