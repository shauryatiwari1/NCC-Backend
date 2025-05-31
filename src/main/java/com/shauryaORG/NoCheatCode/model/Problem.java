package com.shauryaORG.NoCheatCode.model;

import com.shauryaORG.NoCheatCode.model.enums.Difficulty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "problems")
public class Problem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, unique = true)
    private String slug;

    @Enumerated(EnumType.STRING)
    private Difficulty difficulty;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String inputDescription;

    @Column(columnDefinition = "TEXT")
    private String outputDescription;

    @ElementCollection
    @CollectionTable(name = "problem_examples", joinColumns = @JoinColumn(name = "problem_id"))
    private List<String> examples = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "problem_test_cases", joinColumns = @JoinColumn(name = "problem_id"))
    private List<ProblemTestCase> testCases = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "problem_tags", joinColumns = @JoinColumn(name = "problem_id"))
    private List<String> tags = new ArrayList<>();

    @CreationTimestamp
    private LocalDateTime createdAt;
}

