package com.abhi.FitnessTracker.Repository;

import com.abhi.FitnessTracker.Model.Food;
import org.springframework.data.mongodb.repository.MongoRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;

public interface FoodRepository extends MongoRepository<Food, String> {
    
    // Find all system foods (paginated)
    Page<Food> findByCreatedByUserIdIsNull(Pageable pageable);

    // Find system + user foods (paginated)
    @Query("{'$or': [{'createdByUserId': ?0}, {'createdByUserId': null}]}")
    Page<Food> findSystemAndUserFoods(String userId, Pageable pageable);

    // Search by name (system only)
    Page<Food> findByNameContainingIgnoreCaseAndCreatedByUserIdIsNull(String name, Pageable pageable);

    // Search by name (system + user)
    @Query("{'name': {$regex: ?0, $options: 'i'}, '$or': [{'createdByUserId': ?1}, {'createdByUserId': null}]}")
    Page<Food> searchByNameAndUser(String name, String userId, Pageable pageable);

    // Find by category (system only)
    Page<Food> findByCategoryAndCreatedByUserIdIsNull(String category, Pageable pageable);

    // Find by category (system + user)
    @Query("{'category': ?0, '$or': [{'createdByUserId': ?1}, {'createdByUserId': null}]}")
    Page<Food> findByCategoryAndUser(String category, String userId, Pageable pageable);

    boolean existsByName(String name);
}
