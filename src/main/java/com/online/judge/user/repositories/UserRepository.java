package com.online.judge.user.repositories;

import com.online.judge.user.entities.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    public Optional<User> findByEmailId(String email);
}
