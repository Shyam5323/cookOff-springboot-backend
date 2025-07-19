package com.uni.cookoff.repositories;

import com.uni.cookoff.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByEmail(String email);
    Optional<User> findByRegNo(String regNo);
    List<User> findByRole(String role);
    List<User> findByIsBannedFalse();
    List<User> findByRoundQualified(int roundQualified);
    List<User> findByScoreGreaterThanEqual(double score);
    List<User> findByRoleAndIsBannedFalse(String role);
    @Query("SELECT u FROM User u WHERE u.roundQualified = :round AND u.isBanned = false")
    List<User> findQualifiedUsersForRound(@Param("round") int round);
    @Query("SELECT u FROM User u WHERE u.isBanned = false ORDER BY u.score DESC")
    List<User> findTopUsersByScore();
    boolean existsByEmail(String email);
    boolean existsByRegNo(String regNo);

}