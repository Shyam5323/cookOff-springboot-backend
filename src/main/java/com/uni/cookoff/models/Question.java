package com.uni.cookoff.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "questions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Question {

    @Id
    private String id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String title;

    @Column(name = "input_format", columnDefinition = "TEXT")
    private String inputFormat;

    private int points;

    private int round;

    @Column(columnDefinition = "TEXT")
    private String constraints;

    @Column(name = "output_format", columnDefinition = "TEXT")
    private String outputFormat;

    @Column(name = "sample_test_input", columnDefinition = "TEXT")
    private String sampleTestInput;

    @Column(name = "sample_test_output", columnDefinition = "TEXT")
    private String sampleTestOutput;

    @Column(columnDefinition = "TEXT")
    private String explanation;
}
