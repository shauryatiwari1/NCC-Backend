package com.shauryaORG.NoCheatCode.repository;

import com.shauryaORG.NoCheatCode.model.Submission;
import com.shauryaORG.NoCheatCode.model.User;
import com.shauryaORG.NoCheatCode.model.Problem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    List<Submission> findByUser(User user);
    List<Submission> findByUserAndSolvedTrue(User user);
    Optional<Submission> findTopByUserAndProblemOrderBySubmittedAtDesc(User user, Problem problem);
    List<Submission> findByUserAndProblem(User user, Problem problem);
    List<Submission> findByUserId(Long userId);
    List<Submission> findByUserIdAndProblemId(Long userId, Long problemId);
}

