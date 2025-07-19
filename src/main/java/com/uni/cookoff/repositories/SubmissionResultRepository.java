package com.uni.cookoff.repositories;

import com.uni.cookoff.models.SubmissionResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubmissionResultRepository extends JpaRepository<SubmissionResult, String> {
    List<SubmissionResult> findBySubmissionId(String submissionId);
    List<SubmissionResult> findByTestcaseId(String testcaseId);
    List<SubmissionResult> findByStatus(String status);
}