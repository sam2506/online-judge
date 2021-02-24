package com.online.judge.submission.controllers;

import com.online.judge.common.exceptions.ForbiddenException;
import com.online.judge.common.exceptions.NotFoundException;
import com.online.judge.contest.entities.Contest;
import com.online.judge.contest.repositories.ContestRepository;
import com.online.judge.submission.entities.Submission;
import com.online.judge.submission.repositories.SubmissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Date;
import java.util.Optional;

@RestController
public class SubmissionController {

    @Autowired
    private SubmissionRepository submissionRepository;

    @Autowired
    private ContestRepository contestRepository;

    @RequestMapping(value = "/viewSubmission/{submissionId}", method = RequestMethod.GET)
    public ResponseEntity<Submission> getSubmission(Principal principal, @PathVariable String submissionId) {

        String loggedInUserName = principal != null ? principal.getName() : null;
        Optional<Submission> submission = submissionRepository.findById(submissionId);
        if(submission.isPresent()) {
            if(submission.get().getContestId() == null) {
                return ResponseEntity.status(HttpStatus.OK).body(submission.get());
            } else {
                Contest contest = contestRepository.findByContestId(submission.get().getContestId());
                Date currentTime = new Date();
                if(contest.getEndTime().before(currentTime) ||
                        submission.get().getUserName().equals(loggedInUserName)) {
                    return ResponseEntity.status(HttpStatus.OK).body(submission.get());
                } else {
                    throw new ForbiddenException("User do not have permissions for the page");
                }
            }
        } else {
            throw new NotFoundException("SubmissionId:" + submissionId + " not found");
        }
    }
}
