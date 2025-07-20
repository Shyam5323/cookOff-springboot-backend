package com.uni.cookoff.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uni.cookoff.dto.request.JudgeSubmission;
import com.uni.cookoff.dto.request.JudgeToken;
import com.uni.cookoff.dto.request.SubmissionRequest;
import com.uni.cookoff.dto.response.JudgeCallback;
import com.uni.cookoff.dto.response.JudgeResponse;
import com.uni.cookoff.dto.response.RunCodeResponse;
import com.uni.cookoff.dto.response.SubmissionResponse;
import com.uni.cookoff.models.Question;
import com.uni.cookoff.models.Testcase;
import com.uni.cookoff.models.Submission;
import com.uni.cookoff.models.SubmissionResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CodeExecutionService {

    private final RestTemplate restTemplate;
    private final TestcaseService testcaseService;
    private final SubmissionService submissionService;
    private final SubmissionResultService submissionResultService;

    @Value("${judge0.uri}")
    private String judge0Uri;

    @Value("${judge0.token}")
    private String judge0Token;

    @Value("${callback.url}")
    private String callbackUrl;

    private final ExecutorService executorService = Executors.newFixedThreadPool(10);

    public RunCodeResponse runCode(SubmissionRequest request) {
        List<Testcase> testCases = testcaseService.findByQuestionId(request.getQuestionId());

        if (testCases.isEmpty()) {
            throw new RuntimeException("No test cases found for question");
        }

        List<JudgeResponse> results = new ArrayList<>();
        int testCasesPassed = 0;

        for (Testcase testCase : testCases) {
            JudgeResponse result = executeTestCase(request, testCase);
            results.add(result);

            if ("Accepted".equals(result.getStatus().getDescription())) {
                testCasesPassed++;
            }
        }

        return RunCodeResponse.builder()
                .result(results)
                .testCasesPassed(testCasesPassed)
                .build();
    }

    public SubmissionResponse submitCode(SubmissionRequest request, String userId) {
        List<Testcase> testCases = testcaseService.findByQuestionId(request.getQuestionId());

        if (testCases.isEmpty()) {
            throw new RuntimeException("No test cases found for question");
        }

        Submission submission = Submission.builder()
                .id(userId)
                .question(Question.builder().id(request.getQuestionId()).build())
                .languageId(request.getLanguageId())
                .description(request.getSourceCode())
                .status("PENDING")
                .build();

        submission = submissionService.saveSubmission(submission);

        Submission finalSubmission = submission;
        CompletableFuture.runAsync(() -> {
            submitToJudge0(finalSubmission, testCases);
        }, executorService);

        return SubmissionResponse.builder()
                .submissionId(submission.getId())
                .build();
    }

    public void processCallback(JudgeCallback callback) {
        try {
            String submissionId = callback.getSubmissionId();
            String testCaseId = callback.getTestCaseId();

            SubmissionResult result = SubmissionResult.builder()
                    .submission(Submission.builder().id(submissionId).build()) // Set the Submission object
                    .testcase(Testcase.builder().id(testCaseId).build()) // Set the Testcase object
                    .runtime(Double.parseDouble(callback.getTime())) // Use double directly
                    .memory(callback.getMemory())
                    .status(mapStatus(callback.getStatus().getId()))
                    .description(callback.getStatus().getDescription())
                    .build();

            submissionResultService.saveSubmissionResult(result);

            updateSubmissionStatus(submissionId);

        } catch (Exception e) {
            log.error("Error processing callback: {}", e.getMessage(), e);
        }
    }

    private JudgeResponse executeTestCase(SubmissionRequest request, Testcase testCase) {
        // Use a known language ID - try 38 for Python 3
        Integer languageId = request.getLanguageId();

        System.out.println(request);

        JudgeSubmission submission = JudgeSubmission.builder()
                .languageId(languageId)
                .sourceCode((request.getSourceCode()))
                .input((testCase.getInput()))
                .output((testCase.getExpectedOutput()))
                .runtime(BigDecimal.valueOf(Math.min(testCase.getRuntime(), 20.0)))
                .build();

        return sendToJudge0(submission);
    }
    public String testWithBase64() {
        try {
            String url = judge0Uri + "/submissions?base64_encoded=false&wait=false";

            String sourceCode = "print('Hello World')";
            String stdin = "";

            Map<String, Object> payload = new HashMap<>();
            payload.put("language_id", 71);
            payload.put("source_code", sourceCode);
            payload.put("stdin", stdin);

            HttpHeaders headers = createHeaders();
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

            ObjectMapper mapper = new ObjectMapper();
            log.info("Base64 payload: {}", mapper.writeValueAsString(payload));



            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

            return "Success: " + response.getBody();

        } catch (HttpClientErrorException e) {
            log.error("HTTP Error: {}", e.getResponseBodyAsString());
            return "Error: " + e.getResponseBodyAsString();
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage(), e);
            return "Error: " + e.getMessage();
        }
    }
   private JudgeResponse sendToJudge0(JudgeSubmission submission) {
       try {
           String url = judge0Uri + "/submissions?base64_encoded=false&wait=true";

        HttpHeaders headers = createHeaders();
           headers.setContentType(MediaType.APPLICATION_JSON);

           ObjectMapper mapper = new ObjectMapper();
           String rawJson = mapper.writeValueAsString(submission);
           System.out.println("Raw JSON: " + rawJson);

           HttpEntity<String> entity = new HttpEntity<>(rawJson, headers);
           ResponseEntity<JudgeResponse> response = restTemplate.postForEntity(url, entity, JudgeResponse.class);

           if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
               return response.getBody();
           } else {
               throw new RuntimeException("Judge0 failed: " + response.getStatusCode() + ", body: " + mapper.writeValueAsString(response.getBody()));
           }


       } catch (HttpClientErrorException e) {
           log.error("Judge0 API Error: Status={}, Response={}", e.getStatusCode(), e.getResponseBodyAsString());
           throw new RuntimeException("Judge0 API Error: " + e.getResponseBodyAsString(), e);
       } catch (Exception e) {
           log.error("Error in sendToJudge0: {}", e.getMessage(), e);
           throw new RuntimeException("Error in sendToJudge0", e);
       }
   }

    private void submitToJudge0(Submission submission, List<Testcase> testCases) {

        List<JudgeSubmission> submissions = new ArrayList<>();
        List<String> testCaseIds = new ArrayList<>();
        for (Testcase testCase : testCases) {
            JudgeSubmission judgeSubmission = JudgeSubmission.builder()
                    .languageId(submission.getLanguageId())
                    .sourceCode(submission.getDescription())
                    .input((testCase.getInput()))
                    .output((testCase.getExpectedOutput()))
                    .runtime(BigDecimal.valueOf(testCase.getRuntime()))
                    .callback(callbackUrl)
                    .build();
            submissions.add(judgeSubmission);
            testCaseIds.add(testCase.getId());
        }
        try {
            String url = judge0Uri + "/submissions?base64_encoded=false&wait=false";
            HttpHeaders headers = createHeaders();
            HttpEntity<List<JudgeSubmission>> entity = new HttpEntity<>(submissions, headers);
            ResponseEntity<JudgeToken[]> response = restTemplate.postForEntity(url, entity, JudgeToken[].class);

            if (response.getStatusCode() == HttpStatus.CREATED && response.getBody() != null) {
                storeTokens(submission.getId(), response.getBody(), testCaseIds);
            }

        } catch (Exception e) {
            log.error("Error submitting to Judge0: {}", e.getMessage(), e);
            updateSubmissionStatus(submission.getId());
        }
    }

    private void storeTokens(String submissionId, JudgeToken[] tokens, List<String> testCaseIds) {
        for (int i = 0; i < tokens.length && i < testCaseIds.size(); i++) {
            log.info("Stored token {} for submission {} test case {}",
                    tokens[i].getToken(), submissionId, testCaseIds.get(i));
        }
    }

    private void updateSubmissionStatus(String submissionId) {
        List<SubmissionResult> results = submissionResultService.findBySubmissionId(submissionId);

        if (!results.isEmpty()) {
            long passed = results.stream()
                    .filter(r -> "success".equals(r.getStatus()))
                    .count();

            long failed = results.size() - passed;

            Submission submission = submissionService.findById(submissionId).orElse(null);
            if (submission != null) {
                submission.setTestcasesPassed((int) passed); // Corrected method name
                submission.setTestcasesFailed((int) failed); // Corrected method name
                submission.setStatus("COMPLETED");
                submissionService.saveSubmission(submission);
            }
        }
    }




    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        if (judge0Uri.contains("rapidapi")) {
            headers.set("x-rapidapi-host", "judge0-ce.p.rapidapi.com");
            headers.set("x-rapidapi-key", judge0Token);
        } else {
            headers.set("Authorization", "Bearer " + judge0Token);
        }

        return headers;
    }
    private String mapStatus(String statusId) {
        return switch (statusId) {
            case "1" -> "In Queue";
            case "2" -> "Processing";
            case "3" -> "success";
            case "4" -> "wrong answer";
            case "5" -> "Time Limit Exceeded";
            case "6" -> "Compilation error";
            case "7", "8", "9", "10", "11", "12" -> "Runtime error";
            case "13" -> "Internal Error";
            case "14" -> "Exec Format Error";
            default -> "Unknown";
        };
    }
}