package com.uni.cookoff.controllers.admin;

import com.uni.cookoff.dto.request.CreateQuestionRequest;
import com.uni.cookoff.models.Question;
import com.uni.cookoff.services.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/admin/questions")
@RequiredArgsConstructor
public class QuestionController {
    private final QuestionService questionService;

    @PostMapping
    public ResponseEntity<Question> createQuestion(@RequestBody CreateQuestionRequest request) {
        Question question = Question.builder()
                .id(UUID.randomUUID().toString())
                .title(request.getTitle())
                .description(request.getDescription())
                .inputFormat(request.getInputFormat())
                .points(request.getPoints())
                .round(request.getRound())
                .constraints(request.getConstraints())
                .outputFormat(request.getOutputFormat())
                .sampleTestInput(request.getSampleTestInput())
                .sampleTestOutput(request.getSampleTestOutput())
                .explanation(request.getExplanation())
                .build();
        Question saved = questionService.saveQuestion(question);
        return ResponseEntity.ok(saved);
    }

    @PostMapping("/batch")
    public ResponseEntity<?> createQuestions(@RequestBody java.util.List<CreateQuestionRequest> requests) {
        java.util.List<Question> savedQuestions = new java.util.ArrayList<>();
        for (CreateQuestionRequest request : requests) {
            Question question = Question.builder()
                    .id(java.util.UUID.randomUUID().toString())
                    .title(request.getTitle())
                    .description(request.getDescription())
                    .inputFormat(request.getInputFormat())
                    .points(request.getPoints())
                    .round(request.getRound())
                    .constraints(request.getConstraints())
                    .outputFormat(request.getOutputFormat())
                    .sampleTestInput(request.getSampleTestInput())
                    .sampleTestOutput(request.getSampleTestOutput())
                    .explanation(request.getExplanation())
                    .build();
            savedQuestions.add(questionService.saveQuestion(question));
        }
        return ResponseEntity.ok(savedQuestions);
    }
}
