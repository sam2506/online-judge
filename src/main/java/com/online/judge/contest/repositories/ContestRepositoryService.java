package com.online.judge.contest.repositories;

import com.online.judge.contest.models.UpcomingContestDetails;
import com.online.judge.submission.entities.Submission;
import com.online.judge.verdict.Verdict;

import java.util.Date;
import java.util.List;

public interface ContestRepositoryService {
    void updateLeaderboardOfContest(String contestId, String userName, String problemId, Long totalTimeOfSubmissionInSec);
    void addSubmission(String contestId, Submission submission);
    Boolean checkIfUserAlreadySubmittedTheProblem(String contestId, String problemId, String userName);
    Date findStartTimeOfContest(String contestId);
    void updateVerdictOfSubmission(String contestId, String submissionId, Verdict verdict);
    List<UpcomingContestDetails> getContestsWithinAWeek(Date currentTime);
}
