package com.uni.cookoff.controllers;

import com.uni.cookoff.models.User;
import com.uni.cookoff.models.Submission;
import com.uni.cookoff.models.Contest;
import com.uni.cookoff.services.UserService;
import com.uni.cookoff.services.SubmissionService;
import com.uni.cookoff.services.ContestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final UserService userService;
    private final SubmissionService submissionService;
    private final ContestService contestService;

    @GetMapping("/profile")
    public ResponseEntity<UserProfile> getUserProfile(Authentication authentication) {
        try {
            String userEmail = authentication.getName();
            Optional<User> userOpt = userService.findByEmail(userEmail);

            if (userOpt.isEmpty()) {
                return ResponseEntity.status(401).build();
            }

            User user = userOpt.get();
            List<Submission> userSubmissions = submissionService.findByUserId(user.getId());
            List<Contest> activeContests = contestService.getActiveContests();

            // Calculate statistics
            long totalSubmissions = userSubmissions.size();
            long acceptedSubmissions = userSubmissions.stream()
                    .filter(s -> "ACCEPTED".equals(s.getStatus()))
                    .count();

            double acceptanceRate = totalSubmissions > 0 ?
                (double) acceptedSubmissions / totalSubmissions * 100 : 0.0;

            UserProfile profile = new UserProfile(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRegNo(),
                user.getScore(),
                user.getRoundQualified(),
                totalSubmissions,
                acceptedSubmissions,
                acceptanceRate,
                activeContests.size()
            );

            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/submissions")
    public ResponseEntity<List<Submission>> getUserSubmissions(Authentication authentication) {
        try {
            String userEmail = authentication.getName();
            Optional<User> userOpt = userService.findByEmail(userEmail);

            if (userOpt.isEmpty()) {
                return ResponseEntity.status(401).build();
            }

            User user = userOpt.get();
            List<Submission> submissions = submissionService.findByUserId(user.getId());
            return ResponseEntity.ok(submissions);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/submissions/recent")
    public ResponseEntity<List<Submission>> getRecentSubmissions(
            Authentication authentication,
            @RequestParam(defaultValue = "10") int limit) {
        try {
            String userEmail = authentication.getName();
            Optional<User> userOpt = userService.findByEmail(userEmail);

            if (userOpt.isEmpty()) {
                return ResponseEntity.status(401).build();
            }

            User user = userOpt.get();
            List<Submission> submissions = submissionService.findRecentByUserId(user.getId(), limit);
            return ResponseEntity.ok(submissions);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/stats")
    public ResponseEntity<UserStats> getUserStats(Authentication authentication) {
        try {
            String userEmail = authentication.getName();
            Optional<User> userOpt = userService.findByEmail(userEmail);

            if (userOpt.isEmpty()) {
                return ResponseEntity.status(401).build();
            }

            User user = userOpt.get();
            List<Submission> userSubmissions = submissionService.findByUserId(user.getId());

            // Calculate detailed statistics
            long totalSubmissions = userSubmissions.size();
            long acceptedSubmissions = userSubmissions.stream()
                    .filter(s -> "ACCEPTED".equals(s.getStatus()))
                    .count();
            long wrongAnswerSubmissions = userSubmissions.stream()
                    .filter(s -> "WRONG_ANSWER".equals(s.getStatus()))
                    .count();
            long timeoutSubmissions = userSubmissions.stream()
                    .filter(s -> "TIME_LIMIT_EXCEEDED".equals(s.getStatus()))
                    .count();
            long runtimeErrorSubmissions = userSubmissions.stream()
                    .filter(s -> "RUNTIME_ERROR".equals(s.getStatus()))
                    .count();

            UserStats stats = new UserStats(
                totalSubmissions,
                acceptedSubmissions,
                wrongAnswerSubmissions,
                timeoutSubmissions,
                runtimeErrorSubmissions,
                user.getScore(),
                user.getRoundQualified()
            );

            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Inner classes for response DTOs
    public static class UserProfile {
        public String id;
        public String name;
        public String email;
        public String regNo;
        public double score;
        public int roundQualified;
        public long totalSubmissions;
        public long acceptedSubmissions;
        public double acceptanceRate;
        public int activeContests;

        public UserProfile(String id, String name, String email, String regNo, double score,
                          int roundQualified, long totalSubmissions, long acceptedSubmissions,
                          double acceptanceRate, int activeContests) {
            this.id = id;
            this.name = name;
            this.email = email;
            this.regNo = regNo;
            this.score = score;
            this.roundQualified = roundQualified;
            this.totalSubmissions = totalSubmissions;
            this.acceptedSubmissions = acceptedSubmissions;
            this.acceptanceRate = acceptanceRate;
            this.activeContests = activeContests;
        }
    }

    public static class UserStats {
        public long totalSubmissions;
        public long acceptedSubmissions;
        public long wrongAnswerSubmissions;
        public long timeoutSubmissions;
        public long runtimeErrorSubmissions;
        public double score;
        public int roundQualified;

        public UserStats(long totalSubmissions, long acceptedSubmissions, long wrongAnswerSubmissions,
                        long timeoutSubmissions, long runtimeErrorSubmissions, double score, int roundQualified) {
            this.totalSubmissions = totalSubmissions;
            this.acceptedSubmissions = acceptedSubmissions;
            this.wrongAnswerSubmissions = wrongAnswerSubmissions;
            this.timeoutSubmissions = timeoutSubmissions;
            this.runtimeErrorSubmissions = runtimeErrorSubmissions;
            this.score = score;
            this.roundQualified = roundQualified;
        }
    }
}
