package com.shauryaORG.NoCheatCode.aihelper.controller;

import com.shauryaORG.NoCheatCode.aihelper.model.AiResponse;
import com.shauryaORG.NoCheatCode.aihelper.model.HintRequest;
import com.shauryaORG.NoCheatCode.aihelper.service.AiHelperService;
import com.shauryaORG.NoCheatCode.aihelper.service.AiHelperRateLimiter;
import com.shauryaORG.NoCheatCode.util.CodeValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/aihelper")
@RequiredArgsConstructor
@Slf4j
public class AiHelperController {

    private final AiHelperService aiHelperService;
    private final AiHelperRateLimiter rateLimiter;

    private String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : "anonymous";
    }

    private ResponseEntity<AiResponse> checkRateLimitOrReject() {
        String username = getCurrentUsername();
        if (!rateLimiter.allow(username)) {
            AiResponse resp = new AiResponse();
            resp.setText("Rate limit exceeded: Only 5 prompts allowed per hour.");
            resp.setError(true);
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(resp);
        }
        return null;
    }

    private ResponseEntity<AiResponse> checkCodeSafetyOrReject(String code) {
        if (code != null) {
            if (CodeValidationUtil.isCodeTooLong(code)) {
                AiResponse resp = new AiResponse();
                resp.setText("Code too long. Max 50 lines or 1,000 characters allowed.");
                resp.setError(true);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resp);
            }
            String pattern = CodeValidationUtil.findSuspiciousPattern(code);
            if (pattern != null) {
                AiResponse resp = new AiResponse();
                resp.setText("Suspicious code pattern detected: " + pattern);
                resp.setError(true);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resp);
            }
        }
        return null;
    }

    private ResponseEntity<AiResponse> checkPromptSafetyOrReject(String prompt) {
        if (prompt != null) {
            if (prompt.length() > 1000 || prompt.split("\\r?\\n").length > 50) {
                AiResponse resp = new AiResponse();
                resp.setText("Prompt too long. Max 50 lines or 1,000 characters allowed.");
                resp.setError(true);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resp);
            }
            String[] susPatterns = {
                "rm -rf", "shutdown", "format c:", "drop table", "delete from", "hack", "exploit",
                "ignore previous instructions", "disregard previous instructions", "forget previous instructions",
                "act as", "you are now", "pretend to be", "simulate", "bypass", "jailbreak",
                "disregard all previous", "override all previous", "repeat after me", "print", "output", "execute",
                "system: ", "user: ", "assistant: ", "###", "--", "//", "/*", "*/",
                "respond as", "write code", "generate code", "leak", "exfiltrate", "prompt injection",
                "ignore all instructions", "disregard all instructions", "as an ai language model",
                "please ignore", "please pretend", "please simulate", "please act as"
            };
            for (String pattern : susPatterns) {
                if (prompt.toLowerCase().contains(pattern)) {
                    AiResponse resp = new AiResponse();
                    resp.setText("Suspicious prompt pattern detected: " + pattern);
                    resp.setError(true);
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resp);
                }
            }
        }
        return null;
    }

    @PostMapping("/hint")
    public ResponseEntity<AiResponse> getHint(@RequestBody HintRequest request) {
        ResponseEntity<AiResponse> limitResp = checkRateLimitOrReject();
        if (limitResp != null) return limitResp;
        ResponseEntity<AiResponse> codeResp = checkCodeSafetyOrReject(request.getCode());
        if (codeResp != null) return codeResp;
        ResponseEntity<AiResponse> promptResp = checkPromptSafetyOrReject(request.getUserMessage());
        if (promptResp != null) return promptResp;
        log.info("Received hint request for problem: {}, level: {}", request.getProblemTitle(), request.getHintLevel());
        AiResponse response = aiHelperService.getHint(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/analyze")
    public ResponseEntity<AiResponse> analyzeCode(@RequestBody HintRequest request) {
        ResponseEntity<AiResponse> limitResp = checkRateLimitOrReject();
        if (limitResp != null) return limitResp;
        ResponseEntity<AiResponse> codeResp = checkCodeSafetyOrReject(request.getCode());
        if (codeResp != null) return codeResp;
        ResponseEntity<AiResponse> promptResp = checkPromptSafetyOrReject(request.getUserMessage());
        if (promptResp != null) return promptResp;
        log.info("Received code analysis request for problem: {}", request.getProblemTitle());
        AiResponse response = aiHelperService.analyzeCode(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/chat")
    public ResponseEntity<AiResponse> chatWithAi(@RequestBody HintRequest request) {
        ResponseEntity<AiResponse> limitResp = checkRateLimitOrReject();
        if (limitResp != null) return limitResp;
        ResponseEntity<AiResponse> codeResp = checkCodeSafetyOrReject(request.getCode());
        if (codeResp != null) return codeResp;
        ResponseEntity<AiResponse> promptResp = checkPromptSafetyOrReject(request.getUserMessage());
        if (promptResp != null) return promptResp;
        log.info("Received chat request for problem: {}", request.getProblemTitle());
        AiResponse response = aiHelperService.chatWithAi(request);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/test")
    public ResponseEntity<String> testEndpoint() {
        log.info("Test endpoint called");
        return ResponseEntity.ok("AI Helper API is working!");
    }
}
