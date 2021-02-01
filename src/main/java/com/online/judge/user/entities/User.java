package com.online.judge.user.entities;

import com.online.judge.contest.entities.Contest;
import com.online.judge.problem.entities.Problem;
import com.online.judge.submission.entities.Submission;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Document(collection = "USERS")
public class User {

    @Id
    private String userName;
    private List<Contest> createdContests;
    private List<Problem> createdProblems;
    private List<Submission> submissionList;
    private List<Contest> registeredContestsList;
}
