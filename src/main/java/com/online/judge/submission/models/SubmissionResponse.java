package com.online.judge.submission.models;

import com.online.judge.verdict.Verdict;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SubmissionResponse {

    private String submissionId;
    private String userName;
    private String contestId;
    private Verdict verdict;
}
