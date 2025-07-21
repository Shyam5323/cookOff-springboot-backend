package com.uni.cookoff.controllers.admin;

import com.uni.cookoff.models.User;
import com.uni.cookoff.models.Submission;
import com.uni.cookoff.services.UserService;
import com.uni.cookoff.repositories.SubmissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import org.springframework.data.redis.core.StringRedisTemplate;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final SubmissionRepository submissionRepository;
    private final StringRedisTemplate redisTemplate;

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.findActiveUsers());
    }

    @GetMapping("/leaderboard")
    public ResponseEntity<List<User>> getLeaderboard() {
        return ResponseEntity.ok(userService.findTopUsersByScore());
    }

    @PostMapping("/upgrade-round")
    public ResponseEntity<?> upgradeUsersToRound(@RequestBody UpgradeUsersRequest request) {
        if (request.getUserIds() == null || request.getUserIds().isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid user_ids format");
        }
        for (String userId : request.getUserIds()) {
            Optional<User> userOpt = userService.findByUserId(userId);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                user.setRoundQualified(request.getRound());
                userService.saveUser(user);
            }
        }
        return ResponseEntity.ok(Map.of("message", "Users upgraded successfully"));
    }

    @PostMapping("/ban")
    public ResponseEntity<?> banUser(@RequestBody Map<String, String> request) {
        String userId = request.get("user_id");
        if (userId == null) {
            return ResponseEntity.badRequest().body("user_id must be a string");
        }
        Optional<User> userOpt = userService.findByUserId(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found");
        }
        User user = userOpt.get();
        user.setBanned(true);
        userService.saveUser(user);
        return ResponseEntity.ok(Map.of("message", "User banned successfully"));
    }

    @PostMapping("/unban")
    public ResponseEntity<?> unbanUser(@RequestBody Map<String, String> request) {
        String userId = request.get("user_id");
        if (userId == null) {
            return ResponseEntity.badRequest().body("user_id must be a string");
        }
        Optional<User> userOpt = userService.findByUserId(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found");
        }
        User user = userOpt.get();
        user.setBanned(false);
        userService.saveUser(user);
        return ResponseEntity.ok(Map.of("message", "User unbanned successfully"));
    }

    @GetMapping("/{userId}/submissions")
    public ResponseEntity<?> getSubmissionByUser(@PathVariable String userId) {
        List<Submission> submissions = submissionRepository.findByUserId(userId);
        return ResponseEntity.ok(Map.of("data", submissions));
    }

    public static class UpgradeUsersRequest {
        private List<String> userIds;
        private int round;
        public List<String> getUserIds() { return userIds; }
        public void setUserIds(List<String> userIds) { this.userIds = userIds; }
        public int getRound() { return round; }
        public void setRound(int round) { this.round = round; }
    }

    public static class RoundRequest {
        private int round_id;
        public int getRound_id() { return round_id; }
        public void setRound_id(int round_id) { this.round_id = round_id; }
    }

    @PostMapping("/enable-round")
    public ResponseEntity<?> enableRound(@RequestBody RoundRequest reqBody) {
        String redisKey = "round:enabled";
        String roundIDStr = String.valueOf(reqBody.getRound_id());
        redisTemplate.opsForValue().set(redisKey, roundIDStr);
        return ResponseEntity.ok(Map.of("message", "Round enabled successfully"));
    }
}
