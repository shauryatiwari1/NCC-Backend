package com.shauryaORG.NoCheatCode.repository;

import com.shauryaORG.NoCheatCode.model.Problem;
import com.shauryaORG.NoCheatCode.model.enums.Difficulty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProblemRepository extends JpaRepository<Problem, Long> {
    Page<Problem> findAll(Pageable pageable);
    Page<Problem> findByDifficulty(Difficulty difficulty, Pageable pageable);
    Optional<Problem> findBySlug(String slug);
}