package com.abhi.FitnessTracker.Model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * User document representing an authenticated user with multiple profiles.
 */
@Data
@Document(collection = "users")
public class User {
    @Id
    private String id;
    
    @Indexed(unique = true)
    private String email;
    
    private String passwordHash;
    
    private boolean isAdmin = false; // Admin flag for managing foods/exercises
    
    private List<Profile> profiles = new ArrayList<>();
    
    private LocalDateTime createdAt = LocalDateTime.now();
    
    /**
     * Add a new profile to this user
     */
    public void addProfile(Profile profile) {
        if (this.profiles == null) {
            this.profiles = new ArrayList<>();
        }
        this.profiles.add(profile);
    }
    
    /**
     * Find a profile by ID
     */
    public Profile getProfileById(String profileId) {
        if (this.profiles == null) return null;
        return this.profiles.stream()
            .filter(p -> p.getId().equals(profileId))
            .findFirst()
            .orElse(null);
    }
    
    /**
     * Update an existing profile
     */
    public boolean updateProfile(String profileId, Profile updatedProfile) {
        for (int i = 0; i < profiles.size(); i++) {
            if (profiles.get(i).getId().equals(profileId)) {
                profiles.set(i, updatedProfile);
                return true;
            }
        }
        return false;
    }
}
