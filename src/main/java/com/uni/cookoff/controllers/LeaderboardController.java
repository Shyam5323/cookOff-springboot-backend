package com.uni.cookoff.controllers;

import com.uni.cookoff.models.User;
import com.uni.cookoff.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/leaderboard")
@RequiredArgsConstructor
public class LeaderboardController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<LeaderboardEntry>> getLeaderboard() {
        List<User> topUsers = userService.findTopUsersByScore();
        List<LeaderboardEntry> leaderboard = topUsers.stream()
                .map(user -> new LeaderboardEntry(
                    user.getName(),
                    user.getRegNo(),
                    user.getScore(),
                    user.getRoundQualified()
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(leaderboard);
    }

    @GetMapping("/round/{roundNumber}")
    public ResponseEntity<List<LeaderboardEntry>> getLeaderboardByRound(@PathVariable int roundNumber) {
        List<User> qualifiedUsers = userService.findQualifiedUsersForRound(roundNumber);
        List<LeaderboardEntry> leaderboard = qualifiedUsers.stream()
                .map(user -> new LeaderboardEntry(
                    user.getName(),
                    user.getRegNo(),
                    user.getScore(),
                    user.getRoundQualified()
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(leaderboard);
    }

    @GetMapping("/my-rank")
    public ResponseEntity<UserRankInfo> getMyRank(Authentication authentication) {
        try {
            String userEmail = authentication.getName();
            Optional<User> userOpt = userService.findByEmail(userEmail);

            if (userOpt.isEmpty()) {
                return ResponseEntity.status(401).build();
            }

            User user = userOpt.get();
            List<User> allUsers = userService.findTopUsersByScore();

            int rank = 0;
            for (int i = 0; i < allUsers.size(); i++) {
                if (allUsers.get(i).getId().equals(user.getId())) {
                    rank = i + 1;
                    break;
                }
            }

            UserRankInfo rankInfo = new UserRankInfo(
                user.getName(),
                user.getRegNo(),
                user.getScore(),
                user.getRoundQualified(),
                rank,
                allUsers.size()
            );

            return ResponseEntity.ok(rankInfo);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Inner classes for response DTOs
    public static class LeaderboardEntry {
        public String name;
        public String regNo;
        public double score;
        public int roundQualified;

        public LeaderboardEntry(String name, String regNo, double score, int roundQualified) {
            this.name = name;
            this.regNo = regNo;
            this.score = score;
            this.roundQualified = roundQualified;
        }
    }

    public static class UserRankInfo {
        public String name;
        public String regNo;
        public double score;
        public int roundQualified;
        public int rank;
        public int totalParticipants;

        public UserRankInfo(String name, String regNo, double score, int roundQualified, int rank, int totalParticipants) {
            this.name = name;
            this.regNo = regNo;
            this.score = score;
            this.roundQualified = roundQualified;
            this.rank = rank;
            this.totalParticipants = totalParticipants;
        }
    }
}
