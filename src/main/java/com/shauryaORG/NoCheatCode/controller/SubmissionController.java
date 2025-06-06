package com.shauryaORG.NoCheatCode.controller;
import java.util.List;
import com.shauryaORG.NoCheatCode.dto.submission.SubmissionRequestDto;
import com.shauryaORG.NoCheatCode.dto.submission.SubmissionResponseDto;
import com.shauryaORG.NoCheatCode.service.SubmissionService;
import com.shauryaORG.NoCheatCode.util.CodeValidationUtil;
import com.shauryaORG.NoCheatCode.util.CodeSubmissionRateLimiter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import com.shauryaORG.NoCheatCode.dto.submission.SubmissionListResponseDto;
import com.shauryaORG.NoCheatCode.model.User;
import com.shauryaORG.NoCheatCode.model.Problem;
import com.shauryaORG.NoCheatCode.model.Submission;
import com.shauryaORG.NoCheatCode.repository.SubmissionRepository;
import com.shauryaORG.NoCheatCode.repository.UserRepository;
import com.shauryaORG.NoCheatCode.repository.ProblemRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.stream.Collectors;
import org.springframework.transaction.annotation.Transactional;

@RestController
public class SubmissionController {

    @Autowired
    private SubmissionService submissionService;

    @Autowired
    private CodeSubmissionRateLimiter codeSubmissionRateLimiter;

    @Autowired
    private SubmissionRepository submissionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProblemRepository problemRepository;

    @PostMapping("/submit")
    public SubmissionResponseDto submitSolution(@RequestBody SubmissionRequestDto requestDto) {
        // Extract username from security context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication != null ? authentication.getName() : null;
        if (username == null) {
            return new SubmissionResponseDto("FAILED", "User not authenticated.", null);
        }

        // Check code length
        if (CodeValidationUtil.isCodeTooLong(requestDto.getCode())) {
            return new SubmissionResponseDto("FAILED", "Code too long. Max 100 lines or 5,000 characters allowed.", null);
        }

        // Check for suspicious patterns
        String pattern = CodeValidationUtil.findSuspiciousPattern(requestDto.getCode());
        if (pattern != null) {
            return new SubmissionResponseDto("FAILED", "Suspicious code pattern detected: " + pattern, null);
        }

        // Rate limit check
        if (!codeSubmissionRateLimiter.allow(username)) {
            return new SubmissionResponseDto("FAILED", "Rate limit exceeded. Max 5 submissions per hour.", null);
        }

        return submissionService.handleSubmission(requestDto);
    }

    @Transactional
    @GetMapping("/submissions")
    public ResponseEntity<SubmissionListResponseDto> getUserSubmissions() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) return ResponseEntity.status(401).build();
        List<SubmissionListResponseDto.SubmissionDto> dtos = submissionRepository.findByUserId(user.getId()).stream()
                .map(this::toDto).collect(Collectors.toList());
        return ResponseEntity.ok(new SubmissionListResponseDto(dtos));
    }

    @Transactional
    @GetMapping("/submissions/problem/{problemId}")
    public ResponseEntity<SubmissionListResponseDto> getUserSubmissionsForProblem(@PathVariable Long problemId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElse(null);
        Problem problem = problemRepository.findById(problemId).orElse(null);
        if (user == null || problem == null) return ResponseEntity.badRequest().build();
        List<Submission> submissions = submissionRepository.findByUserIdAndProblemId(user.getId(), problem.getId());
        List<SubmissionListResponseDto.SubmissionDto> dtos = submissions.stream()
            .map(this::toDto).collect(Collectors.toList());
        return ResponseEntity.ok(new SubmissionListResponseDto(dtos));
    }

    private SubmissionListResponseDto.SubmissionDto toDto(Submission s) {
        return new SubmissionListResponseDto.SubmissionDto(
                s.getId(),
                s.getProblem() != null ? s.getProblem().getId() : null,
                s.getProblem() != null ? s.getProblem().getTitle() : null,
                s.getCode(),
                s.isSolved(),
                s.getSubmittedAt(),
                s.getCodePatterns()
        );
    }
}

