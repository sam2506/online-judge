package com.online.judge.problem.controllers;

import com.online.judge.amazons3.AmazonS3Service;
import com.online.judge.common.exceptions.ForbiddenException;
import com.online.judge.common.exceptions.NotFoundException;
import com.online.judge.problem.entities.Problem;
import com.online.judge.problem.models.ProblemDetails;
import com.online.judge.problem.repositories.ProblemRepository;
import com.online.judge.submission.entities.Submission;
import com.online.judge.submission.controllers.SubmissionRequest;
import com.online.judge.submission.repositories.SubmissionRepository;
import com.online.judge.test.TestCaseResponse;
import com.online.judge.verdict.Verdict;
import lombok.Setter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.validation.Valid;
import java.security.Principal;
import java.util.*;

@RestController
@RequestMapping(value = "/problems")
@Setter
@RabbitListener(queues = "completed_test_cases_queue")
public class ProblemController {

    @Value("${judge.api.url}")
    private String JUDGE_URL;

    @Autowired
    private ProblemRepository problemRepository;

    @Autowired
    private SubmissionRepository submissionRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private AmazonS3Service amazonS3Service;

    @PostConstruct
    void init() {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    private ResponseEntity<List<ProblemDetails>> getAllProblems() {
        List<Problem> problemList = problemRepository.findByVisibility(true);
        List<ProblemDetails> problemDetailsList = new ArrayList<ProblemDetails>();
        for(Problem problem : problemList){
            ProblemDetails problemDetails = modelMapper.map(problem, ProblemDetails.class);
            problemDetailsList.add(problemDetails);
        }
        return ResponseEntity.status(HttpStatus.OK).body(problemDetailsList);
    }

    @RequestMapping(value = "/{problemId}", method = RequestMethod.GET)
    private ResponseEntity<ProblemDetails> getProblem(Principal principal, @PathVariable String problemId) {

        String loggedInUserName = principal != null ? principal.getName() : null;
        Optional<Problem> problem = problemRepository.findById(problemId);
        if (problem.isPresent()) {
            if (problem.get().getVisibility() ||
                    problem.get().getSetterName().equals(loggedInUserName)) {
                ProblemDetails problemDetails = modelMapper.map(problem.get(), ProblemDetails.class);
                return ResponseEntity.status(HttpStatus.OK).body(problemDetails);
            } else {
                throw new ForbiddenException("User do not have permissions for the page");
            }
        } else {
            throw new NotFoundException("ProblemId:" + problemId + " not found");
        }
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    private ResponseEntity<String> createProblem(Principal principal, @Valid @RequestBody Problem problem) {
        String loggedInUserName = principal != null ? principal.getName() : null;
        problem.setSetterName(loggedInUserName);
        problemRepository.save(problem);
        return ResponseEntity.status(HttpStatus.CREATED).body("success");
    }

    @RequestMapping(value = "/{problemId}/edit", method = RequestMethod.PUT)
    private ResponseEntity<String> editProblem(Principal principal, @PathVariable String problemId, @Valid @RequestBody Problem editedProblem) {
        String loggedInUserName = principal != null ? principal.getName() : null;
        Optional<Problem> originalProblem = problemRepository.findById(problemId);
        if(originalProblem.isPresent()) {
            if(originalProblem.get().getSetterName().equals(loggedInUserName)){
                editedProblem.setSetterName(loggedInUserName);
                editedProblem.setProblemId(problemId);
                problemRepository.save(editedProblem);
                return ResponseEntity.status(HttpStatus.OK).body("success");
            } else {
                throw new ForbiddenException("User not permitted to edit problem");
            }
        } else {
            throw new NotFoundException("ProblemId:" + problemId + " not found");
        }
    }

    @RequestMapping(value = "/{problemId}", method = RequestMethod.DELETE)
    private ResponseEntity<String> deleteProblem(Principal principal, @PathVariable String problemId) {

        String loggedInUserName = principal != null ? principal.getName() : null;
        Optional<Problem> problem = problemRepository.findById(problemId);
        if (problem.isPresent()) {
            if(problem.get().getSetterName().equals(loggedInUserName)) {
                problemRepository.deleteById(problemId);
                return ResponseEntity.status(HttpStatus.OK).body("success");
            } else {
                throw new ForbiddenException("User not permitted to delete problem");
            }
        } else {
            throw new NotFoundException("ProblemId:" + problemId + " not found");
        }
    }

    private Submission getSubmissionFromSubmissionRequest(
            SubmissionRequest submissionRequest, String verdict, Date timeOfSubmission) {

        Submission submission = modelMapper.map(submissionRequest, Submission.class);
        submission.setTimestamp(timeOfSubmission);
        submission.setVerdict(Verdict.valueOf(verdict));
        return submission;
    }

    @RequestMapping(value = "/{problemId}/uploadTestCases", method = RequestMethod.POST)
    private ResponseEntity<String> uploadFile(Principal principal, @PathVariable String problemId,
                                              @RequestPart(value = "file") MultipartFile file) {
        String loggedInUserName = principal != null ? principal.getName() : null;
        Optional<Problem> problem = problemRepository.findById(problemId);
        if (problem.isPresent()) {
            if(problem.get().getSetterName().equals(loggedInUserName)) {
                amazonS3Service.uploadFile(file, problem.get().getProblemId());
                return ResponseEntity.status(HttpStatus.OK).body("successfully uploaded tests");
            } else {
                throw new ForbiddenException("user is not the setter of the problem");
            }
        } else {
            throw new NotFoundException("ProblemId:" + problemId + " not found");
        }
    }

    private JudgeRequest createJudgeRequest(SubmissionRequest submissionRequest, Problem problem) {
        JudgeRequest judgeRequest = new JudgeRequest();
        judgeRequest.setSubmissionRequest(submissionRequest);
        judgeRequest.setTimeLimit(problem.getTimeLimit());
        judgeRequest.setMemoryLimit(problem.getMemoryLimit());
        return judgeRequest;
    }

    private String sendJudgeRequest(JudgeRequest judgeRequest) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<JudgeRequest> entity =
                new HttpEntity<JudgeRequest> (judgeRequest, headers);
        return restTemplate.postForObject(JUDGE_URL, entity, String.class);
    }

    @RequestMapping(value = "/{problemId}/submit", method = RequestMethod.POST)
    private ResponseEntity<String> submitProblem(Principal principal, @PathVariable String problemId,
                                  @Valid @RequestBody SubmissionRequest submissionRequest) {
        Date timeOfSubmission = new Date();
        submissionRequest.setUserName(principal.getName());
        submissionRequest.setProblemId(problemId);
        Optional<Problem> problem = problemRepository.findById(problemId);
        if(problem.isPresent()) {
            JudgeRequest judgeRequest = createJudgeRequest(submissionRequest, problem.get());
            try {
                String verdict = sendJudgeRequest(judgeRequest);
                Submission submission = getSubmissionFromSubmissionRequest(
                        submissionRequest, verdict, timeOfSubmission);
                submissionRepository.save(submission);
                return ResponseEntity.status(HttpStatus.OK).body(verdict);
            } catch (RestClientException e) {
                e.printStackTrace();
                throw new RestClientException(e.toString());
            }
        } else {
            throw new NotFoundException("ProblemID: " + problemId + " does not exist");
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
