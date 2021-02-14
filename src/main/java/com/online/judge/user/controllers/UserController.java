package com.online.judge.user.controllers;

import com.online.judge.submission.entities.Submission;
import com.online.judge.submission.repositories.SubmissionRepository;
import com.online.judge.user.models.UserSubmissionDetails;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/user")
public class UserController {

    @Autowired
    private SubmissionRepository submissionRepository;

    @Autowired
    private ModelMapper modelMapper;

    @PostConstruct
    void init() {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    }

    @RequestMapping(value = "/{userName}/submissions", method = RequestMethod.GET)
    private ResponseEntity<List<UserSubmissionDetails>> getAllSubmissionsOfUser(@PathVariable String userName) {
        List<Submission> submissionList = submissionRepository.findByUserName(userName);
        List<UserSubmissionDetails> userSubmissionDetailsList = new ArrayList<UserSubmissionDetails>();
        for (Submission submission : submissionList) {
            UserSubmissionDetails userSubmissionDetails = modelMapper.map(submission, UserSubmissionDetails.class);
            userSubmissionDetailsList.add(userSubmissionDetails);
        }
        return ResponseEntity.status(HttpStatus.OK).body(userSubmissionDetailsList);
    }
}
