package com.uni.cookoff.services;

import com.uni.cookoff.models.User;
import com.uni.cookoff.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public Optional<User> findByUserId(String userId){
        return userRepository.findById(userId);
    }


    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findByRegNo(String regNo) {
        return userRepository.findByRegNo(regNo);
    }

    public List<User> findByRole(String role) {
        return userRepository.findByRole(role);
    }

    public List<User> findActiveUsers() {
        return userRepository.findByIsBannedFalse();
    }

    public List<User> findByRoundQualified(int roundQualified) {
        return userRepository.findByRoundQualified(roundQualified);
    }

    public List<User> findByScoreGreaterThanEqual(double score) {
        return userRepository.findByScoreGreaterThanEqual(score);
    }

    public List<User> findActiveUsersByRole(String role) {
        return userRepository.findByRoleAndIsBannedFalse(role);
    }

    public List<User> findQualifiedUsersForRound(int round) {
        return userRepository.findQualifiedUsersForRound(round);
    }

    public List<User> findTopUsersByScore() {
        return userRepository.findTopUsersByScore();
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean existsByRegNo(String regNo) {
        return userRepository.existsByRegNo(regNo);
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public void deleteUserById(String id) {
        userRepository.deleteById(id);
    }
}