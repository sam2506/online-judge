package com.online.judge.problem.controllers;

import com.online.judge.submission.models.SubmissionRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class JudgeRequest {
    SubmissionRequest submissionRequest;
    double timeLimit;
    int memoryLimit;
}
