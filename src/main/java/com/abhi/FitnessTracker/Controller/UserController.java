package com.abhi.FitnessTracker.Controller;

import com.abhi.FitnessTracker.Model.User;
import com.abhi.FitnessTracker.Service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @PostMapping
    public User register(@RequestBody User user) {
        return service.save(user);
    }

    @GetMapping
    public List<User> allUsers() {
        return service.getAll();
    }

    @GetMapping("/{email}")
    public User findByEmail(@PathVariable String email) {
        return service.findByEmail(email);
    }
}

