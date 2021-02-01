package com.online.judge.contest.entities;

import com.online.judge.problem.entities.Problem;
import com.online.judge.submission.entities.Submission;
import com.online.judge.user.entities.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Document(collection = "CONTESTS")
public class Contest {

    @Id
    private String contestId;
    private String contestName;
    private Date startTime;
    private Date endTime;
    private List<User> moderators;
    private List<User> registeredUsers;
    private List<Problem> problemList;
    private List<Submission> submissionList;
}
