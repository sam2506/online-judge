package com.online.judge.contest.controllers;

import com.online.judge.common.exceptions.ForbiddenException;
import com.online.judge.common.exceptions.NotFoundException;
import com.online.judge.contest.entities.Contest;
import com.online.judge.contest.models.ContestDetails;
import com.online.judge.contest.models.UpcomingContestDetails;
import com.online.judge.contest.repositories.ContestRepository;
import com.online.judge.leaderboard.Leaderboard;
import com.online.judge.problem.controllers.JudgeRequest;
import com.online.judge.problem.entities.Problem;
import com.online.judge.problem.models.ProblemDetails;
import com.online.judge.problem.repositories.ProblemRepository;
import com.online.judge.rabbitmq.SubmissionHandler;
import com.online.judge.submission.models.SubmissionRequest;
import com.online.judge.submission.entities.Submission;
import com.online.judge.submission.models.SubmissionSuccessResponse;
import com.online.judge.submission.repositories.SubmissionRepository;
import com.online.judge.user.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;

import javax.annotation.PostConstruct;
import javax.validation.Valid;
import java.security.Principal;
import java.util.*;

@RestController
@RequestMapping(value = "/contests")
public class ContestController {

    @Autowired
    private ContestRepository contestRepository;

    @Autowired
    private ProblemRepository problemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubmissionRepository submissionRepository;

    @Autowired
    private SubmissionHandler submissionHandler;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private ModelMapper modelMapper;

    @PostConstruct
    void init() {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    private ResponseEntity<List<ContestDetails>> getAllContests() {
        List<Contest> contestList = contestRepository.findAll();
        List<ContestDetails> contestDetailsList = new ArrayList<ContestDetails>();
        for(Contest contest : contestList) {
            ContestDetails contestDetails = modelMapper.map(contest, ContestDetails.class);
            contestDetails.setProblemDetailsList(Collections.emptyList());
            contestDetails.setSubmissionList((Collections.emptyList()));
            contestDetailsList.add(contestDetails);
        }
        return ResponseEntity.status(HttpStatus.OK).body(contestDetailsList);
    }

    @RequestMapping(value = "/upcomingContest", method = RequestMethod.GET)
    private ResponseEntity<List<UpcomingContestDetails>> getUpcomingContests() {
        Date currentTime = new Date();
        List<UpcomingContestDetails> upcomingContestDetails = contestRepository.getContestsWithinAWeek(currentTime);
        return ResponseEntity.status(HttpStatus.OK).body(upcomingContestDetails);
    }

    @RequestMapping(value = "/{contestId}", method = RequestMethod.GET)
    private ResponseEntity<ContestDetails> getContest(Principal principal, @PathVariable String contestId) {
        Date currentTime = new Date();
        String loggedInUserName = principal != null ? principal.getName() : null;
        Optional<Contest> contest = contestRepository.findById(contestId);
        if(contest.isPresent()) {
            ContestDetails contestDetails = modelMapper.map(contest.get(), ContestDetails.class);
            List<ProblemDetails> problemDetailsList = new ArrayList<ProblemDetails>();
            for(String problemId : contest.get().getProblemIdList()) {
                Problem problem = problemRepository.findByProblemId(problemId);
                ProblemDetails problemDetails = modelMapper.map(problem, ProblemDetails.class);
                problemDetailsList.add(problemDetails);
            }
            contestDetails.setProblemDetailsList(problemDetailsList);
            if(contest.get().getModerators().contains(loggedInUserName)) {
                return ResponseEntity.status(HttpStatus.OK).body(contestDetails);
            }
            Date contestStartTime = contest.get().getStartTime();
            if (currentTime.before(contestStartTime)) {
                contestDetails.setProblemDetailsList(null);
                contestDetails.setSubmissionList(null);
            }
            return ResponseEntity.status(HttpStatus.OK).body(contestDetails);
        } else {
            throw new NotFoundException("ContestId:" + contestId + " not found");
        }
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    private ResponseEntity<String> createContest(Principal principal, @Valid @RequestBody Contest contest) {
        String loggedInUserName = principal != null ? principal.getName() : null;
        for(String problemId : contest.getProblemIdList()) {
            Optional<Problem> problem = problemRepository.findById(problemId);
            if(problem.isPresent()) {
                if (!problem.get().getVisibility() && !problem.get().getSetterName().equals(loggedInUserName)) {
                    throw new ForbiddenException("User not permitted to use this problem");
                }
            } else {
                throw new NotFoundException("ProblemId:" + problemId + " not found");
            }
        }
        if(!contest.getModerators().contains(loggedInUserName)) {
            contest.getModerators().add(loggedInUserName);
        }
        contest.setRegisteredUsers(Collections.emptyList());
        contest.setSubmissionList(Collections.emptyList());
        contest.setLeaderboard(new Leaderboard(UUID.randomUUID().toString(), contest.getContestId(), Collections.emptyList()));
        contestRepository.save(contest);
        return ResponseEntity.status(HttpStatus.CREATED).body("success");
    }

    @RequestMapping(value = "/{contestId}/problem/{problemId}", method = RequestMethod.GET)
    private ResponseEntity<ProblemDetails> getProblemOfContest(@PathVariable String contestId, @PathVariable String problemId) {
        Optional<Contest> contest = contestRepository.findById(contestId);
        if(contest.isPresent()) {
            if(contest.get().getProblemIdList().contains(problemId)) {
                Problem problem = problemRepository.findByProblemId(problemId);
                ProblemDetails problemDetails = modelMapper.map(problem, ProblemDetails.class);
                return ResponseEntity.status(HttpStatus.OK).body(problemDetails);
            } else {
                throw new NotFoundException("ProblemId:" + problemId + " not found");
            }
        } else {
            throw new NotFoundException("ContestId:" + contestId + " not found");
        }
    }

    private Submission getSubmissionFromSubmissionRequest(
            SubmissionRequest submissionRequest, Date timeOfSubmission) {

        Submission submission = modelMapper.map(submissionRequest, Submission.class);
        submission.setTimestamp(timeOfSubmission);
        return submission;
    }

    @RequestMapping(value = "/{contestId}/leaderboard", method = RequestMethod.GET)
    private ResponseEntity<Long> getRankOfUser(@PathVariable String contestId, @RequestParam("userName") String userName) {
        Double scoreOfUser = redisTemplate.opsForZSet().score(contestId, userName);
        if(scoreOfUser == null) {
            throw new NotFoundException("user has not submitted any problem in the contest");
        }
        Long rankOfUser = redisTemplate.opsForZSet().count(contestId, Double.NEGATIVE_INFINITY, scoreOfUser - 1);
        return ResponseEntity.status(HttpStatus.OK).body(rankOfUser);
    }

    private JudgeRequest createJudgeRequest(SubmissionRequest submissionRequest, Problem problem) {
        JudgeRequest judgeRequest = new JudgeRequest();
        judgeRequest.setSubmissionRequest(submissionRequest);
        judgeRequest.setTimeLimit(problem.getTimeLimit());
        judgeRequest.setMemoryLimit(problem.getMemoryLimit());
        return judgeRequest;
    }

    @RequestMapping(value = "/{contestId}/problem/{problemId}/submit", method = RequestMethod.POST)
    @Transactional
    private ResponseEntity<SubmissionSuccessResponse> submitProblem(Principal principal, @PathVariable String contestId, @PathVariable String problemId,
                                                 @Valid @RequestBody SubmissionRequest submissionRequest) {
        Date timeOfSubmission = new Date();
        submissionRequest.setUserName(principal.getName());
        submissionRequest.setProblemId(problemId);
        Optional<Contest> contest = contestRepository.findById(contestId);
        if(contest.isPresent()) {
            if(contest.get().getStartTime().after(timeOfSubmission)) {
                throw new ForbiddenException("contest not started yet");
            }
            if(contest.get().getEndTime().before(timeOfSubmission)) {
                throw new ForbiddenException("contest has ended");
            }
            if(contest.get().getProblemIdList().contains(problemId)) {
                Problem problem = problemRepository.findByProblemId(problemId);
                JudgeRequest judgeRequest = createJudgeRequest(submissionRequest, problem);
                Submission submission = getSubmissionFromSubmissionRequest(
                        submissionRequest, timeOfSubmission);
                submission.setContestId(contestId);
                submissionHandler.pushSubmissionToSubmissionQueue(judgeRequest);
                contestRepository.addSubmission(contestId, submission);
                submissionRepository.save(submission);
                return ResponseEntity.status(HttpStatus.OK).body(new SubmissionSuccessResponse(
                        submissionRequest.getSubmissionId(), "solution submitted successfully"));
            } else {
                throw new NotFoundException("ProblemId:" + problemId + " not found");
            }
        } else {
            throw new NotFoundException("ContestId:" + contestId + " not found");
        }
    }

    @RequestMapping(value = "/{contestId}/problem/{problemId}", method = RequestMethod.POST)
    private ResponseEntity<String> addProblem(Principal principal, @PathVariable String contestId, @PathVariable String problemId) {
        String loggedInUserName = principal != null ? principal.getName() : null;
        Optional<Problem> problem = problemRepository.findById(problemId);
        Optional<Contest> contest = contestRepository.findById(contestId);
        if(contest.isPresent()) {
            if(problem.isPresent()) {
                if(contest.get().getModerators().contains(loggedInUserName)) {
                    if(problem.get().getVisibility() || problem.get().getSetterName().equals(loggedInUserName)) {
                        contest.get().getProblemIdList().add(problemId);
                        contestRepository.save(contest.get());
                        return ResponseEntity.status(HttpStatus.OK).body("success");
                    } else {
                        throw new ForbiddenException("User not permitted to use this problem");
                    }
                } else {
                    throw new ForbiddenException("User is not a moderator of the contest");
                }
            } else {
                throw new NotFoundException("ProblemId:" + problemId + " not found");
            }
        } else {
            throw new NotFoundException("ContestId:" + contestId + " not found");
        }
    }

    @RequestMapping(value = "/{contestId}/problem/{problemId}", method = RequestMethod.DELETE)
    private ResponseEntity<String> deleteProblem(Principal principal, @PathVariable String contestId, @PathVariable String problemId,
                                 @RequestBody String userName) {

        String loggedInUserName = principal != null ? principal.getName() : null;
        Optional<Contest> contest = contestRepository.findById(contestId);
        if(contest.isPresent()) {
            if(contest.get().getProblemIdList().contains(problemId)) {
                if(contest.get().getModerators().contains(loggedInUserName)) {
                    contest.get().getProblemIdList().remove(problemId);
                    contestRepository.save(contest.get());
                    return ResponseEntity.status(HttpStatus.OK).body("success");
                } else {
                    throw new ForbiddenException("User is not a moderator of the contest");
                }
            } else {
                throw new NotFoundException("ProblemId:" + problemId + " not found");
            }
        } else {
            throw new NotFoundException("ContestId:" + contestId + " not found");
        }
    }
}
