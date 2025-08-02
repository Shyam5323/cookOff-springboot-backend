package com.uni.cookoff.controllers;

import com.uni.cookoff.models.Contest;
import com.uni.cookoff.services.ContestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/contests")
@RequiredArgsConstructor
public class ContestController {

    private final ContestService contestService;

    @GetMapping
    public ResponseEntity<List<Contest>> getAllContests() {
        List<Contest> contests = contestService.getAllContests();
        return ResponseEntity.ok(contests);
    }

    @GetMapping("/active")
    public ResponseEntity<List<Contest>> getActiveContests() {
        List<Contest> activeContests = contestService.getActiveContests();
        return ResponseEntity.ok(activeContests);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Contest> getContestById(@PathVariable String id) {
        return contestService.getContestById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/round/{roundNumber}")
    public ResponseEntity<List<Contest>> getContestsByRound(@PathVariable int roundNumber) {
        List<Contest> contests = contestService.getContestsByRound(roundNumber);
        return ResponseEntity.ok(contests);
    }
}
