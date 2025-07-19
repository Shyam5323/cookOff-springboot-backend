package com.uni.cookoff.controllers;


import com.uni.cookoff.dto.*;
import com.uni.cookoff.services.CodeExecutionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class CodeExecutionController {

    private final CodeExecutionService codeExecutionService;

    /**
     * Run code against test cases (for testing without saving submission)
     */
    // Add this to your CodeExecutionController.java

    // Add this to your CodeExecutionController.java

    @GetMapping("/test-judge0")
    public ResponseEntity<String> testJudge0() {
        try {
            codeExecutionService.testJudge0Connection();
            return ResponseEntity.ok("Check logs for available languages");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/test-simple")
    public ResponseEntity<String> testSimpleSubmission() {
        try {
            String result = codeExecutionService.testSimpleSubmission();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/test-no-base64")
    public ResponseEntity<String> testNoBase64() {
        try {
            String result = codeExecutionService.testWithoutBase64();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/test-with-base64")
    public ResponseEntity<String> testWithBase64() {
        try {
            String result = codeExecutionService.testWithBase64();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
    @PostMapping("/runcode")
    public ResponseEntity<RunCodeResponse> runCode(
            @Valid @RequestBody RunCodeRequest request,
            Authentication authentication) {

        try {
            String questionId = (request.getQuestionId());
            RunCodeResponse response = codeExecutionService.runCode(request, questionId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error running code: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Submit code for evaluation (creates submission record)
     */
    @PostMapping("/submit")
    public ResponseEntity<SubmissionResponse> submitCode(
            @Valid @RequestBody SubmissionRequest request,
            Authentication authentication) {

        try {
            String userId = (authentication.getName());
            String questionId = (request.getQuestionId());

            SubmissionResponse response = codeExecutionService.submitCode(request, userId, questionId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error submitting code: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Process callback from Judge0
     */
    @PutMapping("/callback")
    public ResponseEntity<Void> processCallback(@RequestBody JudgeCallback callback) {
        try {
            codeExecutionService.processCallback(callback);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error processing callback: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }
}
