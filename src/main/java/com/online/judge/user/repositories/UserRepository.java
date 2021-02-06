package com.online.judge.user.repositories;

import com.online.judge.user.entities.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {
}
