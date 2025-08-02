package com.uni.cookoff.services;

import com.uni.cookoff.models.Question;
import com.uni.cookoff.repositories.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class QuestionService {
    @Autowired
    private QuestionRepository questionRepository;

    public List<Question> findAll() {
        return questionRepository.findAll();
    }

    public Optional<Question> findById(String id) {
        return questionRepository.findById(id);
    }

    public Question saveQuestion(Question question) {
        return questionRepository.save(question);
    }

    public void deleteQuestionById(String id) {
        questionRepository.deleteById(id);
    }

    public boolean existsById(String id) {
        return questionRepository.existsById(id);
    }

    public List<Question> findByRound(int round) {
        return questionRepository.findByRound(round);
    }

    public List<Question> findByRoundLessThanEqual(int round) {
        return questionRepository.findByRoundLessThanEqual(round);
    }
}