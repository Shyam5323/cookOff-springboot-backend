package com.uni.cookoff.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JudgeResponse {
    private String testCaseId;
    private String stdOut;
    private String expectedOutput;
    private String input;
    private String time;
    private Integer memory;
    private String stdErr;
    private String token;
    private String message;
    private JudgeStatus status;
    private String compilerOutput;
}