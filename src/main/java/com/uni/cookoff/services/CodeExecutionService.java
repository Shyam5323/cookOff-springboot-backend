package com.uni.cookoff.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uni.cookoff.dto.*;
import com.uni.cookoff.models.Question;
import com.uni.cookoff.models.Testcase;
import com.uni.cookoff.models.Submission;
import com.uni.cookoff.models.SubmissionResult;
import com.uni.cookoff.repositories.TestcaseRepository;
import com.uni.cookoff.repositories.SubmissionRepository;
import com.uni.cookoff.repositories.SubmissionResultRepository;
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
    private final TestcaseRepository testCaseRepository;
    private final SubmissionRepository submissionRepository;
    private final SubmissionResultRepository submissionResultRepository;

    @Value("${judge0.uri}")
    private String judge0Uri;

    @Value("${judge0.token}")
    private String judge0Token;

    @Value("${callback.url}")
    private String callbackUrl;

    private final ExecutorService executorService = Executors.newFixedThreadPool(10);

    public RunCodeResponse runCode(RunCodeRequest request, String questionId) {
        List<Testcase> testCases = testCaseRepository.findByQuestionId(questionId);

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

    public SubmissionResponse submitCode(SubmissionRequest request, String userId, String questionId) {
        List<Testcase> testCases = testCaseRepository.findByQuestionId(questionId);

        if (testCases.isEmpty()) {
            throw new RuntimeException("No test cases found for question");
        }

        Submission submission = Submission.builder()
                .id(userId)
                .question(Question.builder().id(questionId).build())
                .languageId(request.getLanguageId())
                .description(request.getSourceCode())
                .status("PENDING")
                .build();

        submission = submissionRepository.save(submission);

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

            submissionResultRepository.save(result);

            updateSubmissionStatus(submissionId);

        } catch (Exception e) {
            log.error("Error processing callback: {}", e.getMessage(), e);
        }
    }

    private JudgeResponse executeTestCase(RunCodeRequest request, Testcase testCase) {
        // Use a known language ID - try 38 for Python 3
        Integer languageId = request.getLanguageId() == 71 ? 38 : request.getLanguageId();

        JudgeSubmission submission = JudgeSubmission.builder()
                .languageId(languageId)  // Use corrected language ID
                .sourceCode(base64Encode(request.getSourceCode()))
                .input(base64Encode(testCase.getInput()))
                .output(base64Encode(testCase.getExpectedOutput()))
                .runtime(BigDecimal.valueOf(Math.min(testCase.getRuntime(), 5.0))) // Reduce to 5.0 seconds
                .build();

        return sendToJudge0(submission, true);
    }
    public String testWithBase64() {
        try {
            String url = judge0Uri + "/submissions?base64_encoded=true&wait=true";

            String sourceCode = Base64.getEncoder().encodeToString("print('Hello World')".getBytes());
            String stdin = Base64.getEncoder().encodeToString("".getBytes());

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
    private JudgeResponse sendToJudge0(JudgeSubmission submission, boolean isSingle) {
        try {
            String url = judge0Uri + "/submissions?base64_encoded=true&wait=true";
            HttpHeaders headers = createHeaders();

            // Debug the actual JSON being sent
            ObjectMapper mapper = new ObjectMapper();
            String jsonPayload = mapper.writeValueAsString(submission);
            log.info("JSON being sent to Judge0: {}", jsonPayload);

            HttpEntity<JudgeSubmission> entity = new HttpEntity<>(submission, headers);

            ResponseEntity<JudgeResponse> response = restTemplate.postForEntity(url, entity, JudgeResponse.class);

            if (response.getStatusCode() == HttpStatus.CREATED && response.getBody() != null) {
                return response.getBody();
            } else {
                throw new RuntimeException("Failed to send submission to Judge0");
            }
        } catch (HttpClientErrorException e) {
            log.error("Judge0 API Error: Status={}, Response={}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Judge0 API Error: " + e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            log.error("Error in sendToJudge0: {}", e.getMessage(), e);
            throw new RuntimeException("Error in sendToJudge0", e);
        }
    }

    // Add a method to test connectivity and get available languages
    public void testJudge0Connection() {
        try {
            String url = judge0Uri + "/languages";
            HttpHeaders headers = createHeaders();
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            log.info("Available languages: {}", response.getBody());
        } catch (Exception e) {
            log.error("Failed to get languages from Judge0: {}", e.getMessage(), e);
        }
    }

    private void submitToJudge0(Submission submission, List<Testcase> testCases) {
        List<JudgeSubmission> submissions = new ArrayList<>();
        List<String> testCaseIds = new ArrayList<>();

        for (Testcase testCase : testCases) {
            System.out.println("Hello : " +  testCase);
            JudgeSubmission judgeSubmission = JudgeSubmission.builder()
                    .languageId(submission.getLanguageId())
                    .sourceCode(base64Encode(submission.getDescription())) // Use getDescription() instead
                    .input(base64Encode(testCase.getInput()))
                    .output(base64Encode(testCase.getExpectedOutput()))
                    .runtime(BigDecimal.valueOf(testCase.getRuntime()))
                    .callback(callbackUrl)
                    .build();


            submissions.add(judgeSubmission);
            testCaseIds.add(testCase.getId());
        }


        try {
            String url = judge0Uri + "/submissions?base64_encoded=true&wait=false";
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
        List<SubmissionResult> results = submissionResultRepository.findBySubmissionId(submissionId);

        if (!results.isEmpty()) {
            long passed = results.stream()
                    .filter(r -> "success".equals(r.getStatus()))
                    .count();

            long failed = results.size() - passed;

            Submission submission = submissionRepository.findById(submissionId).orElse(null);
            if (submission != null) {
                submission.setTestcasesPassed((int) passed); // Corrected method name
                submission.setTestcasesFailed((int) failed); // Corrected method name
                submission.setStatus("COMPLETED");
                submissionRepository.save(submission);
            }
        }
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Check if you're using RapidAPI or direct Judge0
        if (judge0Uri.contains("rapidapi")) {
            headers.set("x-rapidapi-host", "judge0-ce.p.rapidapi.com");
            headers.set("x-rapidapi-key", judge0Token);
        } else {
            // For direct Judge0 API, you might need different headers
            headers.set("Authorization", "Bearer " + judge0Token);
        }

        return headers;
    }

    private String base64Encode(String data) {
        return Base64.getEncoder().encodeToString(data.getBytes());
    }

    private String base64Decode(String encoded) {
        try {
            return new String(Base64.getDecoder().decode(encoded));
        } catch (Exception e) {
            return encoded;
        }
    }

    private int getRuntimeMultiplier(int languageId) {
        return switch (languageId) {
            case 50, 54, 60, 73, 63 -> 1;
            case 51, 62 -> 2;
            case 68 -> 3;
            case 71 -> 5;
            default -> throw new IllegalArgumentException("Invalid language ID: " + languageId);
        };
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
// Add these methods to your CodeExecutionService.java



    public String testSimpleSubmission() {
        try {
            // Test with the simplest possible payload - no base64 encoding
            String url = judge0Uri + "/submissions?base64_encoded=false&wait=true";

            // Create the simplest possible JSON manually
            String jsonPayload = "{\n" +
                    "  \"language_id\": 38,\n" +
                    "  \"source_code\": \"print('Hello World')\",\n" +
                    "  \"stdin\": \"\"\n" +
                    "}";

            HttpHeaders headers = createHeaders();
            HttpEntity<String> entity = new HttpEntity<>(jsonPayload, headers);

            log.info("Testing with simple payload: {}", jsonPayload);
            log.info("Headers: {}", headers);
            log.info("URL: {}", url);

            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

            log.info("Response status: {}", response.getStatusCode());
            log.info("Response body: {}", response.getBody());

            return "Success: " + response.getBody();

        } catch (HttpClientErrorException e) {
            log.error("HTTP Error: Status={}, Response={}", e.getStatusCode(), e.getResponseBodyAsString());
            return "HTTP Error: " + e.getResponseBodyAsString();
        } catch (Exception e) {
            log.error("Error in testSimpleSubmission: {}", e.getMessage(), e);
            return "Error: " + e.getMessage();
        }
    }



}