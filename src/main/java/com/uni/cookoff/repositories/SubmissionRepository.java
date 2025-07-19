package com.uni.cookoff.repositories;

import com.uni.cookoff.models.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, String> {
    List<Submission> findByUserId(String userId);
    List<Submission> findByQuestionId(String questionId);
    List<Submission> findByStatus(String status);
}