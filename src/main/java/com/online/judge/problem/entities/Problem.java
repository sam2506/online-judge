package com.online.judge.problem.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Document(collection = "PROBLEMS")
public class Problem {

    @Id
    private String problemId;
    @NotNull
    private String problemName;
    @NotNull
    private String problemDesc;
    private String setterName;
    @NotNull
    private double timeLimit;
    @NotNull
    private int memoryLimit;
    @NotNull
    private List<String> constraints;
    @NotNull
    private Boolean visibility;
}
