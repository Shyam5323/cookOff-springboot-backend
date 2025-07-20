package com.uni.cookoff.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JudgeCallback {
    private String submissionId;
    private String testCaseId;
    private String time;
    private Integer memory;
    private JudgeStatus status;
}