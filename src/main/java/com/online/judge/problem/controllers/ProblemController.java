package com.online.judge.problem.controllers;

import com.online.judge.problem.entities.Problem;
import com.online.judge.problem.repositories.ProblemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/problems")
public class ProblemController {

    @Autowired
    private ProblemRepository problemRepository;

    @RequestMapping(value = "", method = RequestMethod.GET)
    private List<Problem> getAllProblems() {
        return problemRepository.findAll();
    }

    @RequestMapping(value = "/{problemId}", method = RequestMethod.GET)
    private Problem getProblem(@PathVariable String problemId) {
        return problemRepository.findByProblemId(problemId);
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    private String createProblem(@RequestBody Problem problem) {
        problemRepository.save(problem);
        return "success";
    }

    @RequestMapping(value = "/{problemId}", method = RequestMethod.DELETE)
    private String deleteProblem(@PathVariable String problemId) {
        problemRepository.deleteById(problemId);
        return "success";
    }
}
