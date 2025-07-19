package com.uni.cookoff.services;

import com.uni.cookoff.models.Testcase;
import com.uni.cookoff.repositories.TestcaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TestcaseService {
    @Autowired
    private TestcaseRepository testcaseRepository;

    public List<Testcase> findAll() {
        return testcaseRepository.findAll();
    }

    public Optional<Testcase> findById(String id) {
        return testcaseRepository.findById(id);
    }

    public List<Testcase> findByQuestionId(String questionId) {
        return testcaseRepository.findByQuestionId(questionId);
    }

    public Testcase saveTestcase(Testcase testcase) {
        return testcaseRepository.save(testcase);
    }

    public void deleteTestcaseById(String id) {
        testcaseRepository.deleteById(id);
    }

    public boolean existsById(String id) {
        return testcaseRepository.existsById(id);
    }
}