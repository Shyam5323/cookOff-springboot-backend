package com.uni.cookoff.repositories;

import com.uni.cookoff.models.Contest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ContestRepository extends JpaRepository<Contest, String> {

    List<Contest> findByIsActiveTrue();

    Optional<Contest> findByRoundNumberAndIsActiveTrue(int roundNumber);

    @Query("SELECT c FROM Contest c WHERE c.startTime <= :now AND c.endTime >= :now AND c.isActive = true")
    List<Contest> findActiveContestsAtTime(LocalDateTime now);

    List<Contest> findByRoundNumber(int roundNumber);

    @Query("SELECT c FROM Contest c ORDER BY c.roundNumber ASC")
    List<Contest> findAllOrderByRoundNumber();
}
