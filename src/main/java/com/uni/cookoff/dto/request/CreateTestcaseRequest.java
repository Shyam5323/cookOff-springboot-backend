package com.uni.cookoff.dto.request;

import lombok.Data;

@Data
public class CreateTestcaseRequest {
    private String expectedOutput;
    private double memory;
    private String input;
    private boolean hidden;
    private double runtime;
    private String questionId;
} 