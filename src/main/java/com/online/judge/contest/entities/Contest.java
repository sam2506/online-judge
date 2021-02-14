package com.online.judge.contest.entities;

import com.online.judge.submission.entities.Submission;
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
    private List<String> moderators;
    private List<String> registeredUsers;
    private List<String> problemIdList;
    private List<Submission> submissionList;
}
