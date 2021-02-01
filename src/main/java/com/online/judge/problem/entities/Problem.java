package com.online.judge.problem.entities;

import com.online.judge.output.entities.Output;
import com.online.judge.test.entities.Test;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Document(collection = "PROBLEMS")
public class Problem {

    @Id
    private String problemId;
    private String problemName;
    private String problemDesc;
    private String setterName;
    private double timeLimit;
    private int memoryLimit;
    private String contestId;
    private List<String> constraints;
    private String testId;
    private Boolean visibility;
    private Test testCases;
    private Output output;
}
