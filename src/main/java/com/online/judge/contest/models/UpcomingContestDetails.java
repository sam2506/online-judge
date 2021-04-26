package com.online.judge.contest.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UpcomingContestDetails {

    private String contestId;
    private String contestName;
    private Date startTime;
}
