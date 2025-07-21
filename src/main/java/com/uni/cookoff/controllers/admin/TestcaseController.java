package com.uni.cookoff.controllers.admin;

import com.uni.cookoff.dto.request.CreateTestcaseRequest;
import com.uni.cookoff.models.Question;
import com.uni.cookoff.models.Testcase;
import com.uni.cookoff.services.QuestionService;
import com.uni.cookoff.services.TestcaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/admin/testcases")
@RequiredArgsConstructor
public class TestcaseController {
    private final TestcaseService testcaseService;
    private final QuestionService questionService;

    @PostMapping
    public ResponseEntity<?> createTestcase(@RequestBody CreateTestcaseRequest request) {
        Optional<Question> questionOpt = questionService.findById(request.getQuestionId());
        if (questionOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Question not found");
        }
        Testcase testcase = Testcase.builder()
                .id(UUID.randomUUID().toString())
                .expectedOutput(request.getExpectedOutput())
                .memory(request.getMemory())
                .input(request.getInput())
                .hidden(request.isHidden())
                .runtime(request.getRuntime())
                .question(questionOpt.get())
                .build();
        Testcase saved = testcaseService.saveTestcase(testcase);
        return ResponseEntity.ok(saved);
    }

    @PostMapping("/batch")
    public ResponseEntity<?> createTestcases(@RequestBody java.util.List<CreateTestcaseRequest> requests) {
        java.util.List<Testcase> savedTestcases = new java.util.ArrayList<>();
        for (CreateTestcaseRequest request : requests) {
            java.util.Optional<Question> questionOpt = questionService.findById(request.getQuestionId());
            if (questionOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Question not found for one or more testcases");
            }
            Testcase testcase = Testcase.builder()
                    .id(java.util.UUID.randomUUID().toString())
                    .expectedOutput(request.getExpectedOutput())
                    .memory(request.getMemory())
                    .input(request.getInput())
                    .hidden(request.isHidden())
                    .runtime(request.getRuntime())
                    .question(questionOpt.get())
                    .build();
            savedTestcases.add(testcaseService.saveTestcase(testcase));
        }
        return ResponseEntity.ok(savedTestcases);
    }
} 