package com.online.judge.test.entities;

import com.online.judge.verdict.Verdict;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TestCaseResponse {
    int testCaseNo;
    Verdict verdict;
}
