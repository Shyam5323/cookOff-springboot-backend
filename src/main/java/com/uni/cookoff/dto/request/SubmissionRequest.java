package com.uni.cookoff.dto.request;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmissionRequest {

    @NotBlank(message = "Source code is required")
    private String sourceCode;

    @NotBlank(message = "Question ID is required")
    private String questionId;

    @NotNull(message = "Language ID is required")
    private Integer languageId;
}