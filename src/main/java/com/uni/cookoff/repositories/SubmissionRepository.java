package com.uni.cookoff.repositories;

import com.uni.cookoff.models.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, String> {
    List<Submission> findByUserId(String userId);
    List<Submission> findByQuestionId(String questionId);
    List<Submission> findByStatus(String status);

    @Query("SELECT s FROM Submission s WHERE s.user.id = :userId ORDER BY s.submissionTime DESC")
    List<Submission> findRecentByUserId(@Param("userId") String userId, Pageable pageable);

    default List<Submission> findRecentByUserId(String userId, int limit) {
        return findRecentByUserId(userId, PageRequest.of(0, limit));
    }

    List<Submission> findByUserIdAndStatus(String userId, String status);
}