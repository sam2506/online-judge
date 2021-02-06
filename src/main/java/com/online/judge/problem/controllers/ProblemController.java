package com.online.judge.problem.controllers;

import com.online.judge.compiler.CompileRequest;
import com.online.judge.problem.entities.Problem;
import com.online.judge.problem.repositories.ProblemRepository;
import com.online.judge.submission.entities.SubmissionRequest;
import com.online.judge.test.entities.TestCaseResponse;
import lombok.Setter;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/problems")
@ConfigurationProperties(prefix = "pending.testcases")
@Setter
@RabbitListener(queues = "completed_test_cases_queue")
public class ProblemController {

    private String exchange;
    private String routingKey;

    @Autowired
    private ProblemRepository problemRepository;

    @Autowired
    private RabbitTemplate template;

    @RequestMapping(value = "", method = RequestMethod.GET)
    private List<Problem> getAllProblems() {
        return problemRepository.findByVisibility(true);
    }

    @RequestMapping(value = "/{problemId}", method = RequestMethod.GET)
    private Optional<Problem> getProblem(@PathVariable String problemId, @RequestBody String userName) {

        Optional<Problem> problem = problemRepository.findById(problemId);
        if((problem.isPresent() && problem.get().getVisibility()) ||
                (problem.isPresent()) && problem.get().getSetterName().equals(userName))
            return problem;
        else
            return Optional.empty();
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    private String createProblem(@RequestBody Problem problem) {
        problemRepository.save(problem);
        return "success";
    }

    @RequestMapping(value = "/{problemId}", method = RequestMethod.DELETE)
    private String deleteProblem(@PathVariable String problemId, @RequestBody String userName) {

        Optional<Problem> problem = problemRepository.findById(problemId);
        if(problem.isPresent() && problem.get().getSetterName().equals(userName)) {
            problemRepository.deleteById(problemId);
            return "success";
        } else {
            return "user not permitted to delete problem or problem does not exist";
        }
    }

    @RequestMapping(value = "/{problemId}/submit", method = RequestMethod.POST)
    private String submitProblem(@PathVariable String problemId,
                                  @RequestBody SubmissionRequest submissionRequest) {
        Optional<Problem> problem = problemRepository.findById(problemId);
        if(problem.isPresent()) {
            CompileRequest compileRequest = new CompileRequest();
            compileRequest.setSubmissionRequest(submissionRequest);
            compileRequest.setTimeLimit(problem.get().getTimeLimit());
            compileRequest.setMemoryLimit(problem.get().getMemoryLimit());
            template.convertAndSend(exchange, routingKey, compileRequest);
            return "submission queued";
        } else {
            return "problem does not exist";
        }
    }

    @RabbitHandler
    public void testCompleteListener(TestCaseResponse testCaseResponse) {
        int testCaseNo = testCaseResponse.getTestCaseNo();
        String verdict = testCaseResponse.getVerdict().toString();
        System.out.println("Test Case " + testCaseNo + " " + verdict);
    }
    @RabbitHandler
    public void compilationErrorListener(String message) {
        System.out.println(message);
    }

}
