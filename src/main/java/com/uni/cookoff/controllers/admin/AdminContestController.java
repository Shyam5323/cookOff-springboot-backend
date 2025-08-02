package com.uni.cookoff.controllers.admin;

import com.uni.cookoff.models.Contest;
import com.uni.cookoff.services.ContestService;
import com.uni.cookoff.dto.request.CreateContestRequest;
import com.uni.cookoff.dto.request.UpdateContestRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin/contests")
@RequiredArgsConstructor
public class AdminContestController {

    private final ContestService contestService;

    @PostMapping
    public ResponseEntity<Contest> createContest(@RequestBody CreateContestRequest request) {
        Contest contest = Contest.builder()
                .id(UUID.randomUUID().toString())
                .name(request.getName())
                .description(request.getDescription())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .roundNumber(request.getRoundNumber())
                .isActive(request.isActive())
                .maxParticipants(request.getMaxParticipants())
                .build();

        Contest savedContest = contestService.createContest(contest);
        return ResponseEntity.ok(savedContest);
    }

    @GetMapping
    public ResponseEntity<List<Contest>> getAllContests() {
        List<Contest> contests = contestService.getAllContests();
        return ResponseEntity.ok(contests);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Contest> getContestById(@PathVariable String id) {
        return contestService.getContestById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Contest> updateContest(@PathVariable String id, @RequestBody UpdateContestRequest request) {
        return contestService.getContestById(id)
                .map(contest -> {
                    if (request.getName() != null) contest.setName(request.getName());
                    if (request.getDescription() != null) contest.setDescription(request.getDescription());
                    if (request.getStartTime() != null) contest.setStartTime(request.getStartTime());
                    if (request.getEndTime() != null) contest.setEndTime(request.getEndTime());
                    if (request.getRoundNumber() != null) contest.setRoundNumber(request.getRoundNumber());
                    if (request.getIsActive() != null) contest.setActive(request.getIsActive());
                    if (request.getMaxParticipants() != null) contest.setMaxParticipants(request.getMaxParticipants());

                    Contest updated = contestService.updateContest(contest);
                    return ResponseEntity.ok(updated);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContest(@PathVariable String id) {
        if (contestService.getContestById(id).isPresent()) {
            contestService.deleteContest(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/{id}/activate")
    public ResponseEntity<Contest> activateContest(@PathVariable String id) {
        return contestService.getContestById(id)
                .map(contest -> {
                    contest.setActive(true);
                    Contest updated = contestService.updateContest(contest);
                    return ResponseEntity.ok(updated);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/deactivate")
    public ResponseEntity<Contest> deactivateContest(@PathVariable String id) {
        return contestService.getContestById(id)
                .map(contest -> {
                    contest.setActive(false);
                    Contest updated = contestService.updateContest(contest);
                    return ResponseEntity.ok(updated);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
