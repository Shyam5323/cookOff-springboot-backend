package com.uni.cookoff.controllers;

import com.uni.cookoff.dto.request.SubmissionRequest;
import com.uni.cookoff.dto.response.JudgeCallback;
import com.uni.cookoff.dto.response.RunCodeResponse;
import com.uni.cookoff.dto.response.SubmissionResponse;
import com.uni.cookoff.services.CodeExecutionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
@Slf4j
public class CodeExecutionController {

    private final CodeExecutionService codeExecutionService;


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
    public ResponseEntity<RunCodeResponse> runCode(@Valid @RequestBody SubmissionRequest request) {
        try {
            RunCodeResponse response = codeExecutionService.runCode(request);
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
            System.out.println(userId);
            System.out.println(userId.substring(17,53));

            SubmissionResponse response = codeExecutionService.submitCode(request, userId.substring(17,53));
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
