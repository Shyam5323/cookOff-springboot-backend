package com.uni.cookoff.controllers;

import com.uni.cookoff.models.Question;
import com.uni.cookoff.models.User;
import com.uni.cookoff.services.QuestionService;
import com.uni.cookoff.services.UserService;
import com.uni.cookoff.services.ContestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/questions")
@RequiredArgsConstructor
public class PublicQuestionController {

    private final QuestionService questionService;
    private final UserService userService;
    private final ContestService contestService;

    @GetMapping
    public ResponseEntity<List<Question>> getAllQuestions(Authentication authentication) {
        try {
            String userEmail = authentication.getName();
            Optional<User> userOpt = userService.findByEmail(userEmail);

            if (userOpt.isEmpty()) {
                return ResponseEntity.status(401).build();
            }

            User user = userOpt.get();
            // Users can only see questions for their current round or lower
            List<Question> questions = questionService.findByRoundLessThanEqual(user.getRoundQualified());
            return ResponseEntity.ok(questions);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/round/{roundNumber}")
    public ResponseEntity<List<Question>> getQuestionsByRound(
            @PathVariable int roundNumber,
            Authentication authentication) {
        try {
            String userEmail = authentication.getName();
            Optional<User> userOpt = userService.findByEmail(userEmail);

            if (userOpt.isEmpty()) {
                return ResponseEntity.status(401).build();
            }

            User user = userOpt.get();
            // Users can only access questions for rounds they've qualified for
            if (roundNumber > user.getRoundQualified()) {
                return ResponseEntity.status(403).build();
            }

            List<Question> questions = questionService.findByRound(roundNumber);
            return ResponseEntity.ok(questions);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Question> getQuestionById(
            @PathVariable String id,
            Authentication authentication) {
        try {
            String userEmail = authentication.getName();
            Optional<User> userOpt = userService.findByEmail(userEmail);

            if (userOpt.isEmpty()) {
                return ResponseEntity.status(401).build();
            }

            User user = userOpt.get();
            Optional<Question> questionOpt = questionService.findById(id);

            if (questionOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Question question = questionOpt.get();
            // Check if user can access this question based on their round qualification
            if (question.getRound() > user.getRoundQualified()) {
                return ResponseEntity.status(403).build();
            }

            return ResponseEntity.ok(question);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
