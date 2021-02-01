package com.online.judge.compiler;

import com.online.judge.output.entities.Output;
import com.online.judge.submission.entities.SubmissionRequest;
import com.online.judge.test.entities.Test;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CompileRequest {
    SubmissionRequest submissionRequest;
    Test test;
    Output output;
}
