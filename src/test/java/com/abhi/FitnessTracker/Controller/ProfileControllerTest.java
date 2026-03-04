package com.abhi.FitnessTracker.Controller;

import com.abhi.FitnessTracker.Model.Goals;
import com.abhi.FitnessTracker.Model.Profile;
import com.abhi.FitnessTracker.Service.ProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfileControllerTest {

    @Mock
    private ProfileService profileService;

    @InjectMocks
    private ProfileController profileController;

    private Profile testProfile;
    private final String USER_ID = "user-1";

    @BeforeEach
    void setUp() {
        testProfile = new Profile();
        testProfile.setId("p1");
        testProfile.setName("John");
    }

    @Test
    void getProfiles_returnsProfilesList() {
        when(profileService.getProfiles(USER_ID)).thenReturn(List.of(testProfile));

        ResponseEntity<List<Profile>> response = profileController.getProfiles(USER_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void getProfiles_userNotFound_returnsNotFound() {
        when(profileService.getProfiles("missing")).thenThrow(new RuntimeException("User not found"));

        ResponseEntity<List<Profile>> response = profileController.getProfiles("missing");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void getProfile_found_returnsProfile() {
        when(profileService.getProfile(USER_ID, "p1")).thenReturn(testProfile);

        ResponseEntity<?> response = profileController.getProfile(USER_ID, "p1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getProfile_notFound_returnsBadRequest() {
        when(profileService.getProfile(USER_ID, "missing"))
                .thenThrow(new RuntimeException("Profile not found"));

        ResponseEntity<?> response = profileController.getProfile(USER_ID, "missing");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void createProfile_success() {
        when(profileService.addProfile(USER_ID, testProfile)).thenReturn(testProfile);

        ResponseEntity<?> response = profileController.createProfile(USER_ID, testProfile);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void createProfile_failure_returnsBadRequest() {
        when(profileService.addProfile(eq(USER_ID), any()))
                .thenThrow(new RuntimeException("User not found"));

        ResponseEntity<?> response = profileController.createProfile(USER_ID, testProfile);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void updateProfile_success() {
        when(profileService.updateProfile(USER_ID, "p1", testProfile)).thenReturn(testProfile);

        ResponseEntity<?> response = profileController.updateProfile(USER_ID, "p1", testProfile);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void updateProfile_failure_returnsBadRequest() {
        when(profileService.updateProfile(eq(USER_ID), eq("p999"), any()))
                .thenThrow(new RuntimeException("Profile not found"));

        ResponseEntity<?> response = profileController.updateProfile(USER_ID, "p999", testProfile);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void updateGoals_success() {
        Goals goals = new Goals(2000, 100, 200, 65, 8);
        when(profileService.updateGoals(USER_ID, "p1", goals)).thenReturn(testProfile);

        ResponseEntity<?> response = profileController.updateGoals(USER_ID, "p1", goals);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void updateGoals_failure_returnsBadRequest() {
        when(profileService.updateGoals(eq(USER_ID), eq("missing"), any()))
                .thenThrow(new RuntimeException("Profile not found"));

        ResponseEntity<?> response = profileController.updateGoals(USER_ID, "missing", new Goals());

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void updateWaterIntake_success() {
        when(profileService.updateWaterIntake(USER_ID, "p1", 5)).thenReturn(testProfile);

        var request = new ProfileController.WaterRequest(5);
        ResponseEntity<?> response = profileController.updateWaterIntake(USER_ID, "p1", request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void addWaterIntake_success() {
        when(profileService.addWaterIntake(USER_ID, "p1", 2)).thenReturn(testProfile);

        var request = new ProfileController.WaterRequest(2);
        ResponseEntity<?> response = profileController.addWaterIntake(USER_ID, "p1", request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void addWaterIntake_failure_returnsBadRequest() {
        when(profileService.addWaterIntake(eq(USER_ID), eq("missing"), anyInt()))
                .thenThrow(new RuntimeException("Profile not found"));

        var request = new ProfileController.WaterRequest(1);
        ResponseEntity<?> response = profileController.addWaterIntake(USER_ID, "missing", request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}
