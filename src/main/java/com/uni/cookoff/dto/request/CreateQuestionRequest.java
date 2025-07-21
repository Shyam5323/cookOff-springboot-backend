package com.uni.cookoff.dto.request;

import lombok.Data;

@Data
public class CreateQuestionRequest {
    private String title;
    private String description;
    private String inputFormat;
    private int points;
    private int round;
    private String constraints;
    private String outputFormat;
    private String sampleTestInput;
    private String sampleTestOutput;
    private String explanation;
} 