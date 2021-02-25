package com.online.judge.user.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.Indexed;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserContestStats {

    @Indexed
    private String userName;
    private String contestId;
    private Long totalTimeInSecs;
    private List<String> solvedProblemIdList;
}
