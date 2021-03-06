package com.online.judge.contest.models;

import com.online.judge.language.Language;
import com.online.judge.verdict.Verdict;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ContestSubmissionDetails {

    private String submissionId;
    private String problemId;
    private String userName;
    private Language languageId;
    private Verdict verdict;
    private Date timestamp;
}
