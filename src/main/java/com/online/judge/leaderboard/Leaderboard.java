package com.online.judge.leaderboard;

import com.online.judge.user.models.UserContestStats;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Leaderboard {

    @Id
    String leaderboardId;
    String contestId;
    List<UserContestStats> userContestStatsList;
}
