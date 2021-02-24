package com.online.judge.problem.models;

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
public class ProblemDetails {

    private String problemId;
    private String problemName;
    private String problemDesc;
    private String setterName;
    private double timeLimit;
    private int memoryLimit;
    private List<String> constraints;
}
