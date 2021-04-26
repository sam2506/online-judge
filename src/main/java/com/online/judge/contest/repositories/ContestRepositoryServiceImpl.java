package com.online.judge.contest.repositories;

import com.online.judge.contest.entities.Contest;
import com.online.judge.contest.models.UpcomingContestDetails;
import com.online.judge.submission.entities.Submission;
import com.online.judge.user.models.UserContestStats;
import com.online.judge.verdict.Verdict;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ContestRepositoryServiceImpl implements ContestRepositoryService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public void updateLeaderboardOfContest(String contestId, String userName,
                                           String problemId, Long totalTimeOfSubmissionInSec) {
        Query query = new Query();
        query.addCriteria(Criteria.where("contestId").is(contestId)
                .and("leaderboard.userContestStatsList.userName").is(userName));
        Update update = new Update();
        update.addToSet("leaderboard.userContestStatsList.$.solvedProblemIdList", problemId);
        update.inc("leaderboard.userContestStatsList.$.totalTimeInSecs", totalTimeOfSubmissionInSec);
        if(mongoTemplate.findAndModify(query, update, Contest.class) == null) {
           Query query1 = new Query();
           query1.addCriteria(Criteria.where("contestId").is(contestId));
           UserContestStats userContestStats;
           List<String> solvedProblemList = new ArrayList<String>();
           solvedProblemList.add(problemId);
           userContestStats = new
                   UserContestStats(userName, contestId, totalTimeOfSubmissionInSec, solvedProblemList);
           Update update1 = new Update();
           update1.addToSet("leaderboard.userContestStatsList", userContestStats);
           mongoTemplate.findAndModify(query1, update1, Contest.class);
        }
    }

    @Override
    public void addSubmission(String contestId, Submission submission) {
        Query query = new Query();
        query.addCriteria(Criteria.where("contestId").is(contestId));
        Update update = new Update();
        update.addToSet("submissionList", submission);
        mongoTemplate.findAndModify(query, update, Contest.class);
    }

    @Override
    public Boolean checkIfUserAlreadySubmittedTheProblem(String contestId, String problemId, String userName) {
        Query query = new Query();
        query.addCriteria(Criteria.where("contestId").is(contestId)
                .and("leaderboard.userContestStatsList.userName").is(userName)
                .and("leaderboard.userContestStatsList.$.solvedProblemIdList").in(problemId));
        return mongoTemplate.find(query, Contest.class).size() != 0;
    }

    @Override
    public Date findStartTimeOfContest(String contestId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("contestId").is(contestId));
        query.fields().include("startTime").exclude(contestId);
        Contest contest = mongoTemplate.findOne(query, Contest.class);
        if(contest != null)
            return contest.getStartTime();
        else
            return null;
    }

    @Override
    public void updateVerdictOfSubmission(String contestId, String submissionId, Verdict verdict) {
        Query query = new Query();
        query.addCriteria(Criteria.where("contestId").is(contestId)
                .and("submissionList.submissionId").is(submissionId));
        Update update = new Update();
        update.set("submissionList.$.verdict", verdict);
    }

    @Override
    public List<UpcomingContestDetails> getContestsWithinAWeek(Date currentTime) {
        Date timeAfterSevenDays = new Date(currentTime.getTime() + 7*24*60*60*1000);
        Query query = new Query();
        query.addCriteria(Criteria.where("startTime").gte(currentTime).lt(timeAfterSevenDays));
        query.fields().include("contestName").include("startTime");
        List<Contest> upcomingContests = mongoTemplate.find(query, Contest.class);
        List<UpcomingContestDetails> upcomingContestDetails = new ArrayList<>();
        for(Contest contest : upcomingContests) {
            upcomingContestDetails.add(modelMapper.map(contest, UpcomingContestDetails.class));
        }
        return upcomingContestDetails;
    }
}
