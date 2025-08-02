package com.uni.cookoff.repositories;

import com.uni.cookoff.models.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, String> {
    List<Question> findByRound(int round);
    List<Question> findByTitleContainingIgnoreCase(String title);
    List<Question> findByRoundLessThanEqual(int round);
}