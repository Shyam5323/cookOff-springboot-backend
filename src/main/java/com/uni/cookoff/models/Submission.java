package com.uni.cookoff.models;

import jakarta.persistence.*;
import lombok.*;
import java.sql.Timestamp;

@Entity
@Table(name = "submissions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Submission {

    @Id
    private String id;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private Question question;

    private int testcasesPassed;
    private int testcasesFailed;
    private double runtime;

    @Column(name = "submission_time")
    private Timestamp submissionTime;

    @ManyToOne
    @JoinColumn(name = "testcase_id")
    private Testcase testcase;

    @Column(name = "language_id")
    private int languageId;

    @Column(columnDefinition = "TEXT")
    private String description;

    private double memory;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String status;
}
