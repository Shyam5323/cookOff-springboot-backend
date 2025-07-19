package com.uni.cookoff.services;

import com.uni.cookoff.models.SubmissionResult;
import com.uni.cookoff.repositories.SubmissionResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SubmissionResultService {
    @Autowired
    private SubmissionResultRepository submissionResultRepository;

    public List<SubmissionResult> findAll() {
        return submissionResultRepository.findAll();
    }

    public Optional<SubmissionResult> findById(String id) {
        return submissionResultRepository.findById(id);
    }

    public List<SubmissionResult> findBySubmissionId(String submissionId) {
        return submissionResultRepository.findBySubmissionId(submissionId);
    }

    public List<SubmissionResult> findByStatus(String status) {
        return submissionResultRepository.findByStatus(status);
    }

    public SubmissionResult saveSubmissionResult(SubmissionResult submissionResult) {
        return submissionResultRepository.save(submissionResult);
    }

    public void deleteSubmissionResultById(String id) {
        submissionResultRepository.deleteById(id);
    }

    public boolean existsById(String id) {
        return submissionResultRepository.existsById(id);
    }
}