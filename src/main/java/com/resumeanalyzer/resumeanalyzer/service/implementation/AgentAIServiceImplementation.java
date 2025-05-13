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
        return """
            You are an AI Resume Screener.
            
            🔹 Your response MUST be valid JSON only. Do not start or end with any special characters like ``` or quotes.
            🔹 Analyze the Job Description (JD) and the Resume provided.
            🔹 Be strict in evaluating how well the resume matches the JD.
             - Be careful in reading the job description extract minute details like skills required, years of experience needed
               
            
            Return a JSON object with these fields:
            - matchedSkills: List of hard/technical skills from resume that match the JD.
            - softSkills: List of soft skills from the resume.
            - experienceYears: Integer years of experience mentioned in the resume.
            - extraNotes: A short summary of the candidate's strengths and how they relate to the JD.
            - score: A number between 0 and 100 that reflects how well the resume matches the JD.
              
            Scoring Guidelines:
            - Only give high scores (above 75) if there is a strong skill and years of experience match with the Job description.
            - Give a low score (below 40) if the resume is not relevant to the JD, missing most skills, or completely off-topic.
            - If the JD is too short, missing, or invalid, return score as 0 and include that info in extraNotes.
            
            JD:
            %s
            
            Resume:
            %s
            """.formatted(jd, resume);

    }
}
