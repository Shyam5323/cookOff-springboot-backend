package com.uni.cookoff.models;

import jakarta.persistence.*;
import lombok.*;
import java.sql.Timestamp;

@Entity
@Table(name = "submissions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Submission {

    @Id
    private String id;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private Question question;

    private int testcasesPassed;
    private int testcasesFailed;

    @Column(name = "submission_time")
    private Timestamp submissionTime;

    @Column(name = "language_id")
    private int languageId;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String status;
}
