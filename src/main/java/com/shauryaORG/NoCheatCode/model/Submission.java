package com.shauryaORG.NoCheatCode.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "submissions")
public class Submission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "problem_id", nullable = false)
    private Problem problem;


    @Column(nullable = false, columnDefinition = "TEXT")
    private String code;

    @Column(name = "code_patterns", columnDefinition = "TEXT")
    private String codePatterns;

    @Column(nullable = false)
    private boolean solved;

    private LocalDateTime submittedAt;
}

