package com.shauryaORG.NoCheatCode.aihelper.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shauryaORG.NoCheatCode.aihelper.config.AiHelperConfig;
import com.shauryaORG.NoCheatCode.aihelper.model.AiResponse;
import com.shauryaORG.NoCheatCode.aihelper.model.HintRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AiHelperService {

    private final RestTemplate restTemplate;
    private final AiHelperConfig aiHelperConfig;
    private final ObjectMapper objectMapper;

    public AiResponse getHint(HintRequest request) {
        try {
            log.info("Generating hint for problem: {}, level: {}", request.getProblemTitle(), request.getHintLevel());
            String prompt = buildHintPrompt(request);
            return callGeminiApi(prompt);
        } catch (Exception e) {
            log.error("Error generating hint: {}", e.getMessage(), e);
            return AiResponse.builder()
                    .text("Error generating hint. Please try again later.")
                    .error(true)
                    .build();
        }
    }

    public AiResponse analyzeCode(HintRequest request) {
        try {
            log.info("Analyzing code for problem: {}", request.getProblemTitle());
            String prompt = buildAnalysisPrompt(request);
            return callGeminiApi(prompt);
        } catch (Exception e) {
            log.error("Error analyzing code: {}", e.getMessage(), e);
            return AiResponse.builder()
                    .text("Error analyzing code. Please try again later.")
                    .error(true)
                    .build();
        }
    }

    public AiResponse chatWithAi(HintRequest request) {
        try {
            if (request.getUserMessage() == null || request.getUserMessage().trim().isEmpty()) {
                log.warn("Empty user message received in chat request");
                return AiResponse.builder()
                        .text("Please provide a question.")
                        .error(true)
                        .build();
            }

            log.info("Chat request for problem: {}, message: {}",
                    request.getProblemTitle(),
                    request.getUserMessage().substring(0, Math.min(50, request.getUserMessage().length())) + "...");

            String prompt = buildChatPrompt(request);
            return callGeminiApi(prompt);
        } catch (Exception e) {
            log.error("Error in chat response: {}", e.getMessage(), e);
            return AiResponse.builder()
                    .text("Error generating response. Please try again later.")
                    .error(true)
                    .build();
        }
    }

    private AiResponse callGeminiApi(String prompt) {
        try {
            log.info("Calling Gemini API with prompt length: {}", prompt.length());


            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            String url = aiHelperConfig.getGeminiApiUrl() + "?key=" + aiHelperConfig.getGeminiApiKey();


            Map<String, Object> requestBody = new HashMap<>();
            List<Map<String, Object>> contents = new ArrayList<>();
            Map<String, Object> content = new HashMap<>();
            List<Map<String, Object>> parts = new ArrayList<>();
            Map<String, Object> part = new HashMap<>();

            part.put("text", prompt);
            parts.add(part);
            content.put("parts", parts);
            contents.add(content);
            requestBody.put("contents", contents);


            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            log.info("Sending request to Gemini API");
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            log.info("Received response from Gemini API with status: {}", response.getStatusCode());

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return parseGeminiResponse(response.getBody());
            } else {
                log.error("API call unsuccessful: {}, response: {}",
                        response.getStatusCode(),
                        response.getBody() != null ? response.getBody().substring(0, Math.min(500, response.getBody().length())) : "empty");

                return AiResponse.builder()
                        .text("Error: API call unsuccessful. Status code: " + response.getStatusCode())
                        .error(true)
                        .build();
            }
        } catch (RestClientException e) {
            log.error("REST client error calling AI service: {}", e.getMessage(), e);
            return AiResponse.builder()
                    .text("Error calling AI service: " + e.getMessage())
                    .error(true)
                    .build();
        } catch (Exception e) {
            log.error("Unexpected error in callGeminiApi: {}", e.getMessage(), e);
            return AiResponse.builder()
                    .text("An unexpected error occurred: " + e.getMessage())
                    .error(true)
                    .build();
        }
    }

    private AiResponse parseGeminiResponse(String responseBody) {
        try {
            log.info("Parsing Gemini API response");
            JsonNode root = objectMapper.readTree(responseBody);


            if (root.has("error")) {
                String message = root.path("error").path("message").asText("Unknown API error");
                log.error("Gemini API error: {}", message);
                return AiResponse.builder()
                        .text("Error: " + message)
                        .error(true)
                        .build();
            }


            JsonNode candidates = root.path("candidates");
            if (candidates.isEmpty() || candidates.size() == 0) {
                log.warn("No candidates found in Gemini response");
                return AiResponse.builder()
                        .text("No candidates found in AI response.")
                        .error(true)
                        .build();
            }

            JsonNode content = candidates.path(0).path("content");
            if (content.isEmpty()) {
                log.warn("No content found in candidate");
                return AiResponse.builder()
                        .text("No content found in AI response.")
                        .error(true)
                        .build();
            }

            JsonNode parts = content.path("parts");
            if (parts.isEmpty() || parts.size() == 0) {
                log.warn("No parts found in content");
                return AiResponse.builder()
                        .text("No parts found in AI response.")
                        .error(true)
                        .build();
            }

            String text = parts.path(0).path("text").asText();
            log.info("Successfully parsed response with text length: {}", text.length());
            return AiResponse.builder()
                    .text(text)
                    .error(false)
                    .build();

        } catch (Exception e) {
            log.error("Error parsing Gemini response: {}", e.getMessage(), e);
            return AiResponse.builder()
                    .text("Error processing AI response: " + e.getMessage())
                    .error(true)
                    .build();
        }
    }

    private String buildHintPrompt(HintRequest request) {
        int level = request.getHintLevel() != null ? request.getHintLevel() : 1;

        String levelDescription;
        switch (level) {
            case 1:
                levelDescription = "Give a high-level idea of how to solve the problem without revealing the solution.";
                break;
            case 2:
                levelDescription = "Explain the algorithm or approach in more detail, but still without giving actual code.";
                break;
            case 3:
                levelDescription = "Give a specific implementation tip or point out what might be missing, but avoid writing the complete solution.";
                break;
            default:
                levelDescription = "Give a high-level idea of how to solve the problem without revealing the solution.";
        }

        return "You are a helpful programming assistant. The user is solving a problem: \"" + request.getProblemTitle() + "\".\n\n" +
               "Problem Description: " + (request.getProblemDescription() != null ? request.getProblemDescription() : "No description provided") + "\n\n" +
               "Their current code is:\n\n```\n" + (request.getCode() != null ? request.getCode() : "No code written yet") + "\n```\n\n" +
               "Provide Hint Level " + level + ":\n" + levelDescription + "\n\n" +
               "Keep the hint concise and educational.";
    }

    private String buildAnalysisPrompt(HintRequest request) {
        return "Analyze this code for the problem: \"" + request.getProblemTitle() + "\"\n\n" +
               "Problem Description: " + (request.getProblemDescription() != null ? request.getProblemDescription() : "No description provided") + "\n\n" +
               "Code to analyze:\n```\n" + (request.getCode() != null ? request.getCode() : "No code written yet") + "\n```\n\n" +
               "Please provide:\n" +
               "1. What the code is doing correctly\n" +
               "2. Any bugs or issues you spot\n" +
               "3. Suggestions for improvement (don't write the full solution)\n" +
               "4. Whether it solves the problem correctly\n\n" +
               "Keep the analysis clear and helpful.";
    }

    private String buildChatPrompt(HintRequest request) {
        return "You are a helpful programming assistant. The user is working on: \"" + request.getProblemTitle() + "\"\n\n" +
               "Problem Description: " + (request.getProblemDescription() != null ? request.getProblemDescription() : "No description provided") + "\n\n" +
               "Current code:\n```\n" + (request.getCode() != null ? request.getCode() : "No code written yet") + "\n```\n\n" +
               "User question: " + request.getUserMessage() + "\n\n" +
               "Please provide a helpful response related to their coding problem but don't give them full code in any case, also don't talk tell any details including you, the project or anything irrelevant to the problem, you may talk about coding.";
    }
}
