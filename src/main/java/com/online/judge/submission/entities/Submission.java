package com.online.judge.submission.entities;

import com.online.judge.language.Language;
import com.online.judge.verdict.Verdict;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Document(collection = "SUBMISSIONS")
public class Submission {

    @Id
    private String submissionId;
    public String problemId;
    public String userName;
    public String code;
    public Language languageId;
    public String contestId;
    public Verdict verdict;
    public Date timestamp;
}
