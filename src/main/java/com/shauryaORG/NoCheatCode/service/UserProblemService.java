package com.shauryaORG.NoCheatCode.service;

import com.shauryaORG.NoCheatCode.model.Submission;
import com.shauryaORG.NoCheatCode.model.User;
import com.shauryaORG.NoCheatCode.repository.SubmissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserProblemService {
    @Autowired
    private SubmissionRepository submissionRepository;

    /**
     * Get solved problems for a user with their code patterns instead of full code
     * @param user The user to get solved problems for
     * @return Map of problem IDs to code patterns
     */
    public Map<Long, String> getUserSolvedProblemsWithPatterns(User user) {
        List<Submission> solvedSubmissions = submissionRepository.findByUserAndSolvedTrue(user);

        // Group by problem and keep the latest submission's patterns for each problem
        return solvedSubmissions.stream()
                .collect(Collectors.toMap(
                        s -> s.getProblem().getId(),
                        s -> s.getCodePatterns() != null ? s.getCodePatterns() : "",
                        (existing, replacement) -> existing // Keep the first one we find
                ));
    }

    /**
     * Get solved problems for a user with their latest successful code submission
     * @param user The user to get solved problems for
     * @return Map of problem IDs to submitted code
     * @deprecated Use getUserSolvedProblemsWithPatterns() for AI Helper integration
     */
    @Deprecated
    public Map<Long, String> getUserSolvedProblemsWithCode(User user) {
        List<Submission> solvedSubmissions = submissionRepository.findByUserAndSolvedTrue(user);

        // Group by problem and keep the latest submission for each problem
        return solvedSubmissions.stream()
                .collect(Collectors.toMap(
                        s -> s.getProblem().getId(),
                        Submission::getCode,
                        (existing, replacement) -> existing // Keep the first one we find
                ));
    }

    /**
     * Check if a user has solved a specific problem
     * @param user The user to check
     * @param problemId The problem ID to check
     * @return true if the user has solved the problem, false otherwise
     */
    public boolean hasUserSolvedProblem(User user, Long problemId) {
        return submissionRepository.findByUserAndSolvedTrue(user).stream()
                .anyMatch(s -> s.getProblem().getId().equals(problemId));
    }

    /**
     * Get the latest code patterns for a problem by a user
     * @param user The user
     * @param problemId The problem ID
     * @return The code patterns, or null if no submission exists
     */
    public String getLatestCodePatternsForProblem(User user, Long problemId) {
        return submissionRepository.findByUserAndSolvedTrue(user).stream()
                .filter(s -> s.getProblem().getId().equals(problemId))
                .map(s -> s.getCodePatterns() != null ? s.getCodePatterns() : "")
                .findFirst()
                .orElse(null);
    }

    /**
     * Get the latest code submission for a problem by a user
     * @param user The user
     * @param problemId The problem ID
     * @return The code, or null if no submission exists
     * @deprecated Use getLatestCodePatternsForProblem() for AI Helper integration
     */
    @Deprecated
    public String getLatestCodeForProblem(User user, Long problemId) {
        return submissionRepository.findByUserAndSolvedTrue(user).stream()
                .filter(s -> s.getProblem().getId().equals(problemId))
                .map(Submission::getCode)
                .findFirst()
                .orElse(null);
    }

    /**
     * Get the number of problems a user has solved
     * @param user The user
     * @return The number of solved problems
     */
    public int getUserSolvedProblemsCount(User user) {
        return (int) submissionRepository.findByUserAndSolvedTrue(user).stream()
                .map(submission -> submission.getProblem().getId())
                .distinct()
                .count();
    }

    /**
     * Get all code patterns for problems solved by a user
     * @param user The user
     * @return List of code patterns from all solved problems
     */
    public List<String> getAllCodePatternsByUser(User user) {
        return submissionRepository.findByUserAndSolvedTrue(user).stream()
                .map(s -> s.getCodePatterns() != null ? s.getCodePatterns() : "")
                .filter(patterns -> !patterns.isEmpty())
                .collect(Collectors.toList());
    }

    /**
     * Get aggregated pattern summary for a user across all solved problems
     * @param user The user
     * @return The latest non-empty, detailed code patterns string for the user
     */
    public String getUserCodingPatternSummary(User user) {
        List<String> allPatterns = getAllCodePatternsByUser(user);
        if (allPatterns.isEmpty()) {
            return "No coding patterns available";
        }
        // Return the most recent non-empty code patterns string
        for (int i = allPatterns.size() - 1; i >= 0; i--) {
            String pattern = allPatterns.get(i);
            if (pattern != null && !pattern.trim().isEmpty() && !pattern.trim().equals("No code patterns detected.")) {
                return pattern.trim();
            }
        }
        return "No coding patterns available";
    }
}
