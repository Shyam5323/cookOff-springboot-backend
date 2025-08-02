package com.uni.cookoff.services;

import com.uni.cookoff.models.Submission;
import com.uni.cookoff.repositories.SubmissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SubmissionService {
    @Autowired
    private SubmissionRepository submissionRepository;

    public List<Submission> findAll() {
        return submissionRepository.findAll();
    }

    public Optional<Submission> findById(String id) {
        return submissionRepository.findById(id);
    }

    public List<Submission> findByUserId(String userId) {
        return submissionRepository.findByUserId(userId);
    }

    public List<Submission> findByQuestionId(String questionId) {
        return submissionRepository.findByQuestionId(questionId);
    }

    public List<Submission> findRecentByUserId(String userId, int limit) {
        return submissionRepository.findRecentByUserId(userId, limit);
    }

    public Submission saveSubmission(Submission submission) {
        return submissionRepository.save(submission);
    }

    public void deleteSubmissionById(String id) {
        submissionRepository.deleteById(id);
    }

    public boolean existsById(String id) {
        return submissionRepository.existsById(id);
    }

    public List<Submission> findByUserIdAndStatus(String userId, String status) {
        return submissionRepository.findByUserIdAndStatus(userId, status);
    }
}