package com.uni.cookoff.dto.response;


import com.fasterxml.jackson.annotation.JsonProperty;
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

    @JsonProperty("stdout")
    private String stdOut;

    private String expectedOutput;


    private String input;

    @JsonProperty("time")
    private String time;
    @JsonProperty("memory")
    private Integer memory;
    @JsonProperty("stderr")
    private String stdErr;
    @JsonProperty("token")
    private String token;
    @JsonProperty("message")
    private String message;
    @JsonProperty("status")
    private JudgeStatus status;
    @JsonProperty("compiler_output")
    private String compilerOutput;
}