package com.abhi.FitnessTracker.Controller;

import com.abhi.FitnessTracker.Model.Exercise;
import com.abhi.FitnessTracker.Repository.ExerciseRepository;
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
class ExerciseControllerTest {

    @Mock
    private ExerciseRepository exerciseRepository;

    @InjectMocks
    private ExerciseController exerciseController;

    private Exercise systemExercise;
    private Exercise userExercise;

    @BeforeEach
    void setUp() {
        systemExercise = new Exercise();
        systemExercise.setId("ex-1");
        systemExercise.setName("Push Ups");
        systemExercise.setCategory("home");
        systemExercise.setCreatedByUserId(null); // system exercise

        userExercise = new Exercise();
        userExercise.setId("ex-2");
        userExercise.setName("Custom Exercise");
        userExercise.setCategory("home");
        userExercise.setCreatedByUserId("user-1");
    }

    @Test
    void getAllExercises_withUserId_returnsSystemAndUserExercises() {
        Exercise otherUserExercise = new Exercise();
        otherUserExercise.setId("ex-3");
        otherUserExercise.setCreatedByUserId("other-user");

        when(exerciseRepository.findAll()).thenReturn(List.of(systemExercise, userExercise, otherUserExercise));

        ResponseEntity<List<Exercise>> response = exerciseController.getAllExercises("user-1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size()); // system + user's own, not other user's
    }

    @Test
    void getAllExercises_noUserId_returnsSystemOnly() {
        when(exerciseRepository.findAll()).thenReturn(List.of(systemExercise, userExercise));

        ResponseEntity<List<Exercise>> response = exerciseController.getAllExercises(null);

        assertEquals(1, response.getBody().size());
        assertNull(response.getBody().get(0).getCreatedByUserId());
    }

    @Test
    void getByCategory_withUserId_filtersCorrectly() {
        when(exerciseRepository.findByCategory("home")).thenReturn(List.of(systemExercise, userExercise));

        ResponseEntity<List<Exercise>> response = exerciseController.getByCategory("home", "user-1");

        assertEquals(2, response.getBody().size());
    }

    @Test
    void getByCategory_noUserId_returnsSystemOnly() {
        when(exerciseRepository.findByCategory("home")).thenReturn(List.of(systemExercise, userExercise));

        ResponseEntity<List<Exercise>> response = exerciseController.getByCategory("home", null);

        assertEquals(1, response.getBody().size());
    }

    @Test
    void searchExercises_filtersAndReturns() {
        when(exerciseRepository.findAll()).thenReturn(List.of(systemExercise, userExercise));

        ResponseEntity<List<Exercise>> response = exerciseController.searchExercises("push", "user-1");

        assertEquals(1, response.getBody().size());
        assertEquals("Push Ups", response.getBody().get(0).getName());
    }

    @Test
    void createExercise_success() {
        when(exerciseRepository.save(systemExercise)).thenReturn(systemExercise);

        ResponseEntity<?> response = exerciseController.createExercise(systemExercise);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void createExercise_failure_returnsBadRequest() {
        when(exerciseRepository.save(any())).thenThrow(new RuntimeException("DB error"));

        ResponseEntity<?> response = exerciseController.createExercise(systemExercise);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void updateExercise_existing_returnsUpdated() {
        when(exerciseRepository.existsById("ex-1")).thenReturn(true);
        when(exerciseRepository.save(any())).thenReturn(systemExercise);

        ResponseEntity<?> response = exerciseController.updateExercise("ex-1", systemExercise);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void updateExercise_notFound_returns404() {
        when(exerciseRepository.existsById("missing")).thenReturn(false);

        ResponseEntity<?> response = exerciseController.updateExercise("missing", systemExercise);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void deleteExercise_existing_returnsOk() {
        when(exerciseRepository.existsById("ex-1")).thenReturn(true);

        ResponseEntity<?> response = exerciseController.deleteExercise("ex-1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(exerciseRepository).deleteById("ex-1");
    }

    @Test
    void deleteExercise_notFound_returns404() {
        when(exerciseRepository.existsById("missing")).thenReturn(false);

        ResponseEntity<?> response = exerciseController.deleteExercise("missing");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void getCategories_returnsListOfCategories() {
        ResponseEntity<List<String>> response = exerciseController.getCategories();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(5, response.getBody().size());
        assertTrue(response.getBody().contains("home"));
        assertTrue(response.getBody().contains("gym"));
    }
}
