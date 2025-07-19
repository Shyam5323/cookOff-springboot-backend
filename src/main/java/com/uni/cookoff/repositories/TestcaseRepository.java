package com.uni.cookoff.repositories;

import com.uni.cookoff.models.Testcase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestcaseRepository extends JpaRepository<Testcase, String> {
    List<Testcase> findByQuestionId(String questionId);
    List<Testcase> findByHidden(boolean hidden);
}