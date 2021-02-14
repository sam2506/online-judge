package com.online.judge.contest.models;

import com.online.judge.problem.models.ProblemDetails;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ContestDetails {

    private String contestId;
    private String contestName;
    private Date startTime;
    private Date endTime;
    private List<String> moderators;
    private List<String> registeredUsers;
    private List<ProblemDetails> problemDetailsList;
    private List<ContestSubmissionDetails> submissionList;
}
