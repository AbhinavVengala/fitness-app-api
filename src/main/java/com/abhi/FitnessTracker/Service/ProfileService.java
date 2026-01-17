package com.abhi.FitnessTracker.Service;

import com.abhi.FitnessTracker.Model.Goals;
import com.abhi.FitnessTracker.Model.Profile;
import com.abhi.FitnessTracker.Model.User;
import com.abhi.FitnessTracker.Repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Service for profile management within a user account.
 */
@Service
public class ProfileService {
    
    private final UserRepository userRepository;
    
    public ProfileService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Helper to verify if water intake needs reset.
     * Returns true if profile was modified.
     */
    private boolean checkAndResetWater(Profile profile) {
        String today = LocalDate.now().toString();
        
        if (profile.getWaterIntakeHistory() == null) {
            profile.setWaterIntakeHistory(new java.util.HashMap<>());
        }
        
        if (profile.getLastWaterDate() == null || !profile.getLastWaterDate().equals(today)) {
            if (profile.getLastWaterDate() != null && profile.getWaterIntake() > 0) {
                profile.getWaterIntakeHistory().put(profile.getLastWaterDate(), profile.getWaterIntake());
            }
            
            profile.setWaterIntake(0);
            profile.setLastWaterDate(today);
            return true;
        }
        return false;
    }

    /**
     * Add water intake for a profile (Atomic update)
     */
    public Profile addWaterIntake(String userId, String profileId, int amount) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        Profile profile = user.getProfileById(profileId);
        if (profile == null) {
            throw new RuntimeException("Profile not found");
        }
        
        checkAndResetWater(profile);
        
        profile.setWaterIntake(profile.getWaterIntake() + amount);
        userRepository.save(user);
        
        return profile;
    }
    
    /**
     * Get all profiles for a user
     */
    public List<Profile> getProfiles(String userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<Profile> profiles = user.getProfiles();
        boolean changed = false;
        
        for (Profile p : profiles) {
            if (checkAndResetWater(p)) {
                changed = true;
            }
        }
        
        if (changed) {
            userRepository.save(user);
        }
        
        return profiles;
    }
    
    /**
     * Add a new profile to a user
     */
    public Profile addProfile(String userId, Profile profile) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (profile.getId() == null || profile.getId().isEmpty()) {
            profile.setId(UUID.randomUUID().toString());
        }
        
        if (profile.getGoals() == null) {
            profile.setGoals(new Goals(2000, 100, 200, 65, 8));
        }
        
        profile.setWaterIntake(0);
        profile.setLastWaterDate(LocalDate.now().toString());
        
        user.addProfile(profile);
        userRepository.save(user);
        
        return profile;
    }
    
    /**
     * Update an existing profile
     */
    public Profile updateProfile(String userId, String profileId, Profile updatedProfile) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        updatedProfile.setId(profileId);
        
        if (!user.updateProfile(profileId, updatedProfile)) {
            throw new RuntimeException("Profile not found");
        }
        
        userRepository.save(user);
        return updatedProfile;
    }
    
    /**
     * Update goals for a profile
     */
    public Profile updateGoals(String userId, String profileId, Goals goals) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        Profile profile = user.getProfileById(profileId);
        if (profile == null) {
            throw new RuntimeException("Profile not found");
        }
        
        profile.setGoals(goals);
        userRepository.save(user);
        
        return profile;
    }
    
    /**
     * Update water intake for a profile
     */
    public Profile updateWaterIntake(String userId, String profileId, int waterIntake) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        Profile profile = user.getProfileById(profileId);
        if (profile == null) {
            throw new RuntimeException("Profile not found");
        }
        
        profile.setWaterIntake(waterIntake);
        profile.setLastWaterDate(LocalDate.now().toString());
        userRepository.save(user);
        
        return profile;
    }
    
    /**
     * Get a specific profile
     */
    public Profile getProfile(String userId, String profileId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        Profile profile = user.getProfileById(profileId);
        if (profile == null) {
            throw new RuntimeException("Profile not found");
        }
        
        if (checkAndResetWater(profile)) {
            userRepository.save(user);
        }
        
        return profile;
    }
}
