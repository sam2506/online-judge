package com.online.judge.problem.repositories;

import com.online.judge.problem.entities.Problem;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ProblemRepository extends MongoRepository<Problem, String> {
    public Problem findByProblemId(String problemId);
    public List<Problem> findByVisibility(Boolean visibility);
}
