package com.abhi.FitnessTracker.Repository;

import com.abhi.FitnessTracker.Model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {
    User findByEmail(String email);
}

