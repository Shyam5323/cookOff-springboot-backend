package com.uni.cookoff.services;

import com.uni.cookoff.models.SubmissionToken;
import com.uni.cookoff.repositories.SubmissionTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SubmissionTokenService {
    @Autowired
    SubmissionTokenRepository submissionTokenRepository;

    public SubmissionToken findToken(String submissionId, String testcaseId) {
        return submissionTokenRepository.findBySubmissionIdAndTestcaseId(submissionId, testcaseId);
    }
    public void deleteBySubmissionId(String submissionId) {
        submissionTokenRepository.deleteBySubmissionId(submissionId);
    }
    public void deleteByTestcaseId(String testcaseId) {
        submissionTokenRepository.deleteByTestcaseId(testcaseId);
    }

    public SubmissionToken saveSubmissionToken(SubmissionToken submissionToken) {
        return submissionTokenRepository.save(submissionToken);
    }
}
