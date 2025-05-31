package com.shauryaORG.NoCheatCode.controller;

import com.shauryaORG.NoCheatCode.dto.submission.SubmissionRequestDto;
import com.shauryaORG.NoCheatCode.dto.submission.SubmissionResponseDto;
import com.shauryaORG.NoCheatCode.service.SubmissionService;
import com.shauryaORG.NoCheatCode.util.CodeValidationUtil;
import com.shauryaORG.NoCheatCode.util.CodeSubmissionRateLimiter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
public class SubmissionController {

    @Autowired
    private SubmissionService submissionService;

    @Autowired
    private CodeSubmissionRateLimiter codeSubmissionRateLimiter;

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
}

