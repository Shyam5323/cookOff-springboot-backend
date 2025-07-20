package com.uni.cookoff.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // Only include non-null fields
public class JudgeSubmission {

    @JsonProperty("language_id")
    private Integer languageId;

    @JsonProperty("source_code")
    private String sourceCode;

    @JsonProperty("stdin")
    private String input;

    @JsonProperty("expected_output")
    private String output;

    @JsonProperty("cpu_time_limit")
    private BigDecimal runtime;

    @JsonProperty("callback_url")
    private String callback;

    @JsonProperty("memory_limit")
    private Integer memoryLimit; // in KB
}