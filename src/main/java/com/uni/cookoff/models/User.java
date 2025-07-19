package com.uni.cookoff.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    private String id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "reg_no", nullable = false, unique = true)
    private String regNo;

    @Column(nullable = false)
    private String password;

    private String role;

    @Column(name = "round_qualified")
    private int roundQualified;

    private double score;

    private String name;

    @Column(name = "is_banned")
    private boolean isBanned;
}
