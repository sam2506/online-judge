package com.online.judge.submission.entities;

import com.online.judge.language.Language;
import com.online.judge.verdict.Verdict;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
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
    @Indexed
    private String problemId;
    @Indexed
    private String userName;
    private String code;
    private Language languageId;
    private String contestId;
    private Verdict verdict;
    private Date timestamp;
}
