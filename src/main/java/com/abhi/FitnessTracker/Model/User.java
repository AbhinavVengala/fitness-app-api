package com.abhi.FitnessTracker.Model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "users")
public class User {
    @Id
    private String id;
    private String name;
    private String email;
    private String passwordHash;
    private String gender;
    private int age;
    private double heightCm;
    private double weightKg;
    private LocalDateTime createdAt = LocalDateTime.now();
}

