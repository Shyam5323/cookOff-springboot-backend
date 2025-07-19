package com.uni.cookoff.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "submission_results")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubmissionResult {

    @Id
    private String id;

    @ManyToOne
    @JoinColumn(name = "submission_id")
    private Submission submission;

    private double runtime;

    @Column(columnDefinition = "TEXT")
    private String description;

    private double memory;

    @ManyToOne
    @JoinColumn(name = "testcase_id")
    private Testcase testcase;

    private String status;
}
