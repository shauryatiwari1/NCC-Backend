package com.shauryaORG.NoCheatCode.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shauryaORG.NoCheatCode.dto.submission.SubmissionRequestDto;
import com.shauryaORG.NoCheatCode.dto.submission.SubmissionResponseDto;
import com.shauryaORG.NoCheatCode.model.Problem;
import com.shauryaORG.NoCheatCode.model.ProblemTestCase;
import com.shauryaORG.NoCheatCode.repository.ProblemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.util.*;

@Service
public class SubmissionService {
    @Autowired
    private ProblemRepository problemRepository;

    private static final String JUDGE0_URL = "https://judge0-ce.p.rapidapi.com/submissions?base64_encoded=false&wait=true";
    @Value("${judge0.api.key}")
    private String judge0Key;
    private static final Map<String, Integer> LANGUAGE_MAP = Map.of(
            "java", 62,
            "javascript", 63,
            "python", 71,
            "cpp", 54
    );

    public SubmissionResponseDto handleSubmission(SubmissionRequestDto requestDto) {
        Optional<Problem> problemOpt = problemRepository.findById(requestDto.getProblemId());
        if (problemOpt.isEmpty()) {
            return new SubmissionResponseDto("FAILED", "Problem not found", null);
        }
        Problem problem = problemOpt.get();
        List<ProblemTestCase> testCases = problem.getTestCases();
        if (testCases == null || testCases.isEmpty()) {
            return new SubmissionResponseDto("FAILED", "No test cases found for this problem", null);
        }
        Integer languageId = LANGUAGE_MAP.getOrDefault(requestDto.getLanguage().toLowerCase(), 62);
        List<Map<String, Object>> testResults = new ArrayList<>();
        boolean allPassed = true;
        for (ProblemTestCase testCase : testCases) {
            Map<String, Object> result = judge0Evaluate(requestDto.getCode(), testCase.getInput(), testCase.getOutput(), languageId);
            testResults.add(result);
            if (!(Boolean.TRUE.equals(result.get("passed")))) {
                allPassed = false;
            }
        }
        String status = allPassed ? "ACCEPTED" : "FAILED";
        String message = allPassed ? "All test cases passed!" : "Some test cases failed.";
        return new SubmissionResponseDto(status, message, testResults);
    }

    private Map<String, Object> judge0Evaluate(String code, String input, String expectedOutput, int languageId) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-RapidAPI-Key", judge0Key);
        headers.set("X-RapidAPI-Host", "judge0-ce.p.rapidapi.com");
        Map<String, Object> body = new HashMap<>();
        body.put("source_code", code);
        body.put("language_id", languageId);
        body.put("stdin", input);
        body.put("expected_output", expectedOutput);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        Map<String, Object> result = new HashMap<>();
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(JUDGE0_URL, entity, String.class);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(response.getBody());
            String stdout = node.path("stdout").asText("");
            String stderr = node.path("stderr").asText("");
            int statusId = node.path("status").path("id").asInt();
            boolean passed = stdout.trim().equals(expectedOutput.trim()) && statusId == 3;
            result.put("input", input);
            result.put("expectedOutput", expectedOutput);
            result.put("stdout", stdout);
            result.put("stderr", stderr);
            result.put("statusId", statusId);
            result.put("passed", passed);
        } catch (Exception e) {
            result.put("input", input);
            result.put("expectedOutput", expectedOutput);
            result.put("stdout", "");
            result.put("stderr", e.getMessage());
            result.put("statusId", -1);
            result.put("passed", false);
        }
        return result;
    }
}










