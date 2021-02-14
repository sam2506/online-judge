package com.online.judge.submission.repositories;

import com.online.judge.submission.entities.Submission;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface SubmissionRepository extends MongoRepository<Submission, String> {
    public List<Submission> findByUserName(String userName);
}
