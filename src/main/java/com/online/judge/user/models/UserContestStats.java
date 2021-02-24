package com.online.judge.user.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserContestStats {

    private String userName;
    private String contestId;
    private Long totalTimeInSecs;
    private List<String> solvedProblemIdList;
}
