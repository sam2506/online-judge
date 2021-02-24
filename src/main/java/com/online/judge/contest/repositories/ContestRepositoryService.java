package com.online.judge.contest.repositories;

import com.online.judge.submission.entities.Submission;

public interface ContestRepositoryService {
    void updateLeaderboardOfContest(String contestId, String userName, String problemId, Long totalTimeOfSubmissionInSec);
    void addSubmission(String contestId, Submission submission);
    Boolean checkIfUserAlreadySubmittedTheProblem(String contestId, String problemId, String userName);
}
