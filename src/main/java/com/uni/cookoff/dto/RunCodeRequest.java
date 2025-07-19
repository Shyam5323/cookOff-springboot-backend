package com.uni.cookoff.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RunCodeRequest {

    @NotNull
    @NotBlank
    private String sourceCode;

    @NotNull
    private Integer languageId;

    @NotNull
    @NotBlank
    private String questionId;
}
