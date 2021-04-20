package com.online.judge.rabbitmq;

import com.online.judge.compiler.CompilationResponse;
import com.online.judge.contest.repositories.ContestRepository;
import com.online.judge.problem.controllers.JudgeRequest;
import com.online.judge.submission.entities.Submission;
import com.online.judge.submission.models.SubmissionResponse;
import com.online.judge.submission.repositories.SubmissionRepository;
import com.online.judge.test.TestCaseResponse;
import com.online.judge.verdict.Verdict;
import lombok.Setter;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@ConfigurationProperties(prefix = "submission")
@RabbitListener(queues = "completed_test_cases_queue")
@Setter
public class SubmissionHandler {

    private String exchange;
    private String routingKey;

    @Autowired
    private ContestRepository contestRepository;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private SubmissionRepository submissionRepository;

    @Autowired
    private RabbitTemplate template;

    @Autowired
    private StringRedisTemplate redisTemplate;

    public void pushSubmissionToSubmissionQueue(JudgeRequest judgeRequest) {
        template.convertAndSend(exchange, routingKey, judgeRequest);
    }

    @RabbitHandler
    public void testCompleteListener(TestCaseResponse testCaseResponse) {
        int testCaseNo = testCaseResponse.getTestCaseNo();
        String verdict = testCaseResponse.getVerdict().toString();
        String userName = testCaseResponse.getUserName();
        String submissionId = testCaseResponse.getSubmissionId();
        System.out.println("Test Case " + testCaseNo + " " + verdict);
        simpMessagingTemplate.convertAndSendToUser(userName, "/queue/" + submissionId + "/testCaseResponses", testCaseResponse);
    }
    @RabbitHandler
    public void compilationListener(CompilationResponse compilationResponse) {
        String userName = compilationResponse.getUserName();
        String submissionId = compilationResponse.getSubmissionId();
        simpMessagingTemplate.convertAndSendToUser(userName, "/queue/" + submissionId + "/compilationResponse", compilationResponse);
    }

    @RabbitHandler
    public void submissionResponseListener(SubmissionResponse submissionResponse) {
        String userName = submissionResponse.getUserName();
        String submissionId = submissionResponse.getSubmissionId();
        String contestId = submissionResponse.getContestId();
        Verdict verdict = submissionResponse.getVerdict();
        Submission submission = submissionRepository.findBySubmissionId(submissionId);
        submission.setVerdict(verdict);
        submissionRepository.save(submission);
        if(contestId != null) {
            Date startTimeOfContest = contestRepository.findStartTimeOfContest(contestId);
            Long totalTimeOfSubmissionInSec = (submission.getTimestamp().getTime() - startTimeOfContest.getTime()) / 1000;
            if(verdict == Verdict.AC) {
                if(!contestRepository.checkIfUserAlreadySubmittedTheProblem(contestId, submission.getProblemId(), userName)) {
                    updateLeaderBoard(contestId, userName, totalTimeOfSubmissionInSec);
                    contestRepository.updateLeaderboardOfContest(contestId, userName, submission.getProblemId(), totalTimeOfSubmissionInSec);
                }
            }
            if(verdict == Verdict.WA) {
                updateLeaderBoard(contestId, userName, 0L);
            }
            contestRepository.updateVerdictOfSubmission(contestId, submissionId, verdict);
        }
        simpMessagingTemplate.convertAndSendToUser(userName, "/queue/" + submissionId + "/submissionResponse", submissionResponse);
    }

    private void updateLeaderBoard(String contestId, String userName, Long totalTimeOfSubmission) {
        final double POINTS_ON_WA_SUBMISSION = 0;
        if(redisTemplate.opsForZSet().score(contestId, userName) == null) {
            redisTemplate.opsForZSet().add(contestId, userName, POINTS_ON_WA_SUBMISSION);
        }
        if(totalTimeOfSubmission != 0) {
            final long POINTS_ON_AC_SUBMISSION = 100000000;
            final long MAX_TOTAL_TIME_OF_SUBMISSION = 1000000;
            redisTemplate.opsForZSet().incrementScore(contestId, userName, -1.0 * POINTS_ON_AC_SUBMISSION + MAX_TOTAL_TIME_OF_SUBMISSION - totalTimeOfSubmission);
        }
    }
}
