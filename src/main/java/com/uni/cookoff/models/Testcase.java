package com.uni.cookoff.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "testcases")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Testcase {

    @Id
    private String id;

    @Column(name = "expected_output", columnDefinition = "TEXT")
    private String expectedOutput;

    private double memory;

    @Column(columnDefinition = "TEXT")
    private String input;

    private boolean hidden;

    private double runtime;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private Question question;
}
