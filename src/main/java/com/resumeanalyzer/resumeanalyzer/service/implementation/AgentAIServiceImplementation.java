package com.resumeanalyzer.resumeanalyzer.service.implementation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.resumeanalyzer.resumeanalyzer.service.AgentAIService;
import com.resumeanalyzer.resumeanalyzer.config.WebConfig;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;


@Service
public class AgentAIServiceImplementation implements AgentAIService {

    @Autowired
    WebConfig webConfig;

    ObjectMapper objectMapper = new ObjectMapper();

    @Value("${genAIKey}")
    String genAIKey;


    @Override
    public List<Map<String, Object>> handleBulkResume(String jd, List<String> resumeText) {
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        List<Map<String, Object>> list = new ArrayList<>();
        List<Future<Map<String, Object>>> futures = resumeText.stream().map(
                resume ->  executorService.submit(() -> extractDetails(jd, resume))).collect(Collectors.toList());

        for (Future<Map<String, Object>> future : futures) {
            try {
                list.add(future.get());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }

        executorService.shutdown();
        return list;
    }

    public Map<String, Object> extractDetails(String jd, String resumeText) {
        String prompt = buildPrompt(jd, resumeText);
        System.out.println("build Prompt " + prompt);
        Map<String, Object> requestBody = Map.of("contents",
                List.of(Map.of("parts",
                        List.of(Map.of("text", prompt)))));
        String jsonResponse = webConfig.openAIClient().post()
                .uri(uriBuilder -> uriBuilder.queryParam("key", genAIKey).build())
                .bodyValue(requestBody)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(json -> json.at("/candidates/0/content/parts/0/text").asText())
                .block();
        System.out.println("json " + jsonResponse);
        String cleanedJson = jsonResponse
                .replaceAll("(?s)```json\\s*", "")  // remove ```json (with optional newline)
                .replaceAll("(?s)```", "")          // remove closing ```
                .trim();
        try {
            return objectMapper.readValue(cleanedJson, Map.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse Gemini response", e);
        }
    }

    private String buildPrompt(String jd, String resume) {
        String prompt = """
You are an AI Resume Screener.

🔹 Your response MUST be valid JSON only. Do not include any Markdown or special characters like ``` or quotes at the start or end.
🔹 Analyze the following Job Description (JD) and Resume.
🔹 Be strict and realistic in your evaluation.

Return a JSON object with the following fields:
- name: Full name of the candidate.
- mobileNumber: Mobile number (if found).
- emailId: Email address (if found).
- highestEducation: Highest degree or qualification (e.g., B.Tech in CSE).
- educationYear: Year the highest education was completed.
- previousCompanies: List of company names where the candidate has worked.
- matchedSkills: List of hard/technical skills from the resume that match the JD.
- softSkills: List of soft skills from the resume.
- experienceYears: Integer — total years of experience.
- extraNotes: Short summary about the candidate’s relevance to the JD.
- score: Integer (0 to 100) based on how well the resume matches the JD.

Scoring Guidelines:
- High score (75–100): Strong skill and experience match with the JD.
- Medium score (40–74): Partial match.
- Low score (below 40): Very little or no relevance.
- If the JD is missing, invalid, or too generic, return score as 0 and explain in extraNotes.

JD:
%s

Resume:
%s
""".formatted(jd, resume);
return prompt;
    }
}
