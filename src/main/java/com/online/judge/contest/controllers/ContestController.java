package com.online.judge.contest.controllers;

import com.online.judge.contest.entities.Contest;
import com.online.judge.contest.repositories.ContestRepository;
import com.online.judge.problem.entities.Problem;
import com.online.judge.problem.repositories.ProblemRepository;
import com.online.judge.user.entities.User;
import com.online.judge.user.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/contests")
public class ContestController {

    @Autowired
    private ContestRepository contestRepository;

    @Autowired
    private ProblemRepository problemRepository;

    @Autowired
    private UserRepository userRepository;

    @RequestMapping(value = "", method = RequestMethod.GET)
    private List<Contest> getAllContests() {
        return contestRepository.findAll();
    }

    @RequestMapping(value = "/{contestId}", method = RequestMethod.GET)
    private Optional<Contest> getContest(@PathVariable String contestId, @RequestBody String userName) {
        Date currentTime = new Date();
        Optional<Contest> contest = contestRepository.findById(contestId);
        Optional<User> user = userRepository.findById(userName);
        if(contest.isPresent()) {
            if(user.isPresent()) {
                if(contest.get().getModerators().contains(user.get())) {
                    return contest;
                }
            }
            Date contestStartTime = contest.get().getStartTime();
            if (currentTime.before(contestStartTime)) {
                return Optional.empty();
            } else {
                return contest;
            }
        } else {
            return Optional.empty();
        }
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    private String createContest(@RequestBody Contest contest) {
        contestRepository.save(contest);
        return "success";
    }

    @RequestMapping(value = "/{contestId}/{problemId}", method = RequestMethod.PUT)
    private String addProblem(@PathVariable String contestId, @PathVariable String problemId,
                              @RequestBody String userName) {
        Optional<User> user = userRepository.findById(userName);
        Optional<Problem> problem = problemRepository.findById(problemId);
        Optional<Contest> contest = contestRepository.findById(contestId);
        if(contest.isPresent() && problem.isPresent() && user.isPresent()) {
            if(contest.get().getModerators().contains(user.get())) {
                contest.get().getProblemList().add(problem.get());
                contestRepository.save(contest.get());
                return "success";
            }
            else
                return "User is not a moderator";
        } else {
            return "The problem does not exist";
        }
    }

    @RequestMapping(value = "/{contestId}/{problemId}}", method = RequestMethod.DELETE)
    private String deleteProblem(@PathVariable String contestId, @PathVariable String problemId,
                                 @RequestBody String userName) {
        Optional<Problem> problem = problemRepository.findById(problemId);
        Optional<Contest> contest = contestRepository.findById(contestId);
        Optional<User> user = userRepository.findById(userName);
        if(contest.isPresent() && problem.isPresent() && user.isPresent()) {
            if(contest.get().getModerators().contains(user.get())) {
                contest.get().getProblemList().remove(problem.get());
                contestRepository.save(contest.get());
                return "success";
            } else {
                return "User is not a moderator";
            }
        } else {
            return "The problem does not exist";
        }
    }
}
