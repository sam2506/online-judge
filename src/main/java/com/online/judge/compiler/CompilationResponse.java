package com.online.judge.compiler;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CompilationResponse {
    boolean isCompilationSuccessful;
    int noOfTestCases;
    String submissionId;
    String userName;
}
