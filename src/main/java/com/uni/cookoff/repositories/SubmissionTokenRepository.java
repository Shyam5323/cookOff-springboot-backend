package com.uni.cookoff.repositories;

import com.uni.cookoff.models.SubmissionResult;
import com.uni.cookoff.models.SubmissionToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubmissionTokenRepository extends JpaRepository<SubmissionToken, String> {
    SubmissionToken findBySubmissionIdAndTestcaseId(String submissionId, String testcaseId);
    void deleteBySubmissionId(String submissionId);
    void deleteByTestcaseId(String testcaseId);
}
