package com.abhi.FitnessTracker.Service;

import com.abhi.FitnessTracker.Model.Goals;
import com.abhi.FitnessTracker.Model.Profile;
import com.abhi.FitnessTracker.Model.User;
import com.abhi.FitnessTracker.Repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfileServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ProfileService profileService;

    private User testUser;
    private Profile testProfile;

    @BeforeEach
    void setUp() {
        testProfile = new Profile();
        testProfile.setId("p1");
        testProfile.setName("John");
        testProfile.setAge(25);
        testProfile.setWeight(75);
        testProfile.setHeight(180);
        testProfile.setGoals(new Goals(2000, 100, 200, 65, 8));
        testProfile.setWaterIntake(0);
        testProfile.setLastWaterDate(LocalDate.now().toString());
        testProfile.setWaterIntakeHistory(new HashMap<>());

        testUser = new User();
        testUser.setId("user-1");
        testUser.setEmail("test@example.com");
        testUser.setProfiles(new ArrayList<>(List.of(testProfile)));
    }

    // ========== getProfiles ==========

    @Test
    void getProfiles_returnsProfilesList() {
        when(userRepository.findById("user-1")).thenReturn(Optional.of(testUser));

        List<Profile> result = profileService.getProfiles("user-1");

        assertEquals(1, result.size());
        assertEquals("John", result.get(0).getName());
    }

    @Test
    void getProfiles_userNotFound_throwsException() {
        when(userRepository.findById("missing")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> profileService.getProfiles("missing"));
    }

    // ========== addProfile ==========

    @Test
    void addProfile_setsDefaultGoalsAndWater() {
        when(userRepository.findById("user-1")).thenReturn(Optional.of(testUser));

        Profile newProfile = new Profile();
        newProfile.setName("Jane");
        newProfile.setAge(30);

        Profile result = profileService.addProfile("user-1", newProfile);

        assertNotNull(result.getId()); // UUID generated
        assertNotNull(result.getGoals());
        assertEquals(0, result.getWaterIntake());
        assertEquals(LocalDate.now().toString(), result.getLastWaterDate());
        verify(userRepository).save(testUser);
    }

    @Test
    void addProfile_keepExistingIdIfProvided() {
        when(userRepository.findById("user-1")).thenReturn(Optional.of(testUser));

        Profile newProfile = new Profile();
        newProfile.setId("custom-id");
        newProfile.setName("Custom");

        Profile result = profileService.addProfile("user-1", newProfile);

        assertEquals("custom-id", result.getId());
    }

    @Test
    void addProfile_keepExistingGoalsIfProvided() {
        when(userRepository.findById("user-1")).thenReturn(Optional.of(testUser));

        Goals customGoals = new Goals(3000, 150, 300, 100, 10);
        Profile newProfile = new Profile();
        newProfile.setName("Custom Goals");
        newProfile.setGoals(customGoals);

        Profile result = profileService.addProfile("user-1", newProfile);

        assertEquals(3000, result.getGoals().getCalories());
    }

    // ========== updateProfile ==========

    @Test
    void updateProfile_found_updatesSuccessfully() {
        when(userRepository.findById("user-1")).thenReturn(Optional.of(testUser));

        Profile updated = new Profile();
        updated.setName("Updated John");
        updated.setAge(26);

        Profile result = profileService.updateProfile("user-1", "p1", updated);

        assertEquals("Updated John", result.getName());
        assertEquals("p1", result.getId());
        verify(userRepository).save(testUser);
    }

    @Test
    void updateProfile_notFound_throwsException() {
        when(userRepository.findById("user-1")).thenReturn(Optional.of(testUser));

        assertThrows(RuntimeException.class,
                () -> profileService.updateProfile("user-1", "nonexistent", new Profile()));
    }

    // ========== updateGoals ==========

    @Test
    void updateGoals_updatesProfileGoals() {
        when(userRepository.findById("user-1")).thenReturn(Optional.of(testUser));

        Goals newGoals = new Goals(2500, 120, 250, 80, 10);

        Profile result = profileService.updateGoals("user-1", "p1", newGoals);

        assertEquals(2500, result.getGoals().getCalories());
        assertEquals(120, result.getGoals().getProtein());
        verify(userRepository).save(testUser);
    }

    @Test
    void updateGoals_profileNotFound_throwsException() {
        when(userRepository.findById("user-1")).thenReturn(Optional.of(testUser));

        assertThrows(RuntimeException.class,
                () -> profileService.updateGoals("user-1", "nonexistent", new Goals()));
    }

    // ========== addWaterIntake ==========

    @Test
    void addWaterIntake_addsToExisting() {
        testProfile.setWaterIntake(3);
        when(userRepository.findById("user-1")).thenReturn(Optional.of(testUser));

        Profile result = profileService.addWaterIntake("user-1", "p1", 2);

        assertEquals(5, result.getWaterIntake());
        verify(userRepository).save(testUser);
    }

    @Test
    void addWaterIntake_profileNotFound_throwsException() {
        when(userRepository.findById("user-1")).thenReturn(Optional.of(testUser));

        assertThrows(RuntimeException.class,
                () -> profileService.addWaterIntake("user-1", "nonexistent", 1));
    }

    // ========== updateWaterIntake ==========

    @Test
    void updateWaterIntake_setsValue() {
        when(userRepository.findById("user-1")).thenReturn(Optional.of(testUser));

        Profile result = profileService.updateWaterIntake("user-1", "p1", 5);

        assertEquals(5, result.getWaterIntake());
        assertEquals(LocalDate.now().toString(), result.getLastWaterDate());
        verify(userRepository).save(testUser);
    }

    // ========== getProfile ==========

    @Test
    void getProfile_found_returnsProfile() {
        when(userRepository.findById("user-1")).thenReturn(Optional.of(testUser));

        Profile result = profileService.getProfile("user-1", "p1");

        assertEquals("John", result.getName());
    }

    @Test
    void getProfile_notFound_throwsException() {
        when(userRepository.findById("user-1")).thenReturn(Optional.of(testUser));

        assertThrows(RuntimeException.class,
                () -> profileService.getProfile("user-1", "nonexistent"));
    }

    @Test
    void getProfile_userNotFound_throwsException() {
        when(userRepository.findById("missing")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> profileService.getProfile("missing", "p1"));
    }
}
