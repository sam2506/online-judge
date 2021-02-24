package com.online.judge.contest.repositories;

import com.online.judge.contest.entities.Contest;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ContestRepository extends MongoRepository<Contest, String>, ContestRepositoryService {
    public Contest findByContestId(String contestId);
}
