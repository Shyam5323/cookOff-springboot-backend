package com.uni.cookoff.services;

import com.uni.cookoff.models.Contest;
import com.uni.cookoff.repositories.ContestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ContestService {

    private final ContestRepository contestRepository;

    public Contest createContest(Contest contest) {
        return contestRepository.save(contest);
    }

    public List<Contest> getAllContests() {
        return contestRepository.findAllOrderByRoundNumber();
    }

    public Optional<Contest> getContestById(String id) {
        return contestRepository.findById(id);
    }

    public List<Contest> getActiveContests() {
        return contestRepository.findActiveContestsAtTime(LocalDateTime.now());
    }

    public Optional<Contest> getActiveContestByRound(int roundNumber) {
        return contestRepository.findByRoundNumberAndIsActiveTrue(roundNumber);
    }

    public Contest updateContest(Contest contest) {
        return contestRepository.save(contest);
    }

    public void deleteContest(String id) {
        contestRepository.deleteById(id);
    }

    public boolean isContestActive(String contestId) {
        Optional<Contest> contest = contestRepository.findById(contestId);
        if (contest.isPresent()) {
            Contest c = contest.get();
            LocalDateTime now = LocalDateTime.now();
            return c.isActive() &&
                   c.getStartTime().isBefore(now) &&
                   c.getEndTime().isAfter(now);
        }
        return false;
    }

    public List<Contest> getContestsByRound(int roundNumber) {
        return contestRepository.findByRoundNumber(roundNumber);
    }
}
