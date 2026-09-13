package com.ara.resumeanalyzer.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Talks to the Anthropic Claude API to analyze a resume against a job
 * description. This is the "AI" part of the project — everything else
 * (auth, DB, REST layer) exists to support this core feature.
 *
 * How it works:
 *  1. We build a prompt asking Claude to compare the resume and JD.
 *  2. We instruct Claude to reply ONLY in JSON (score, missing keywords, suggestions)
 *     so we can parse it reliably instead of doing fragile string-matching.
 *  3. We call the /v1/messages endpoint with our API key.
 *  4. We parse Claude's JSON reply into a simple result object.
 *
 * MOCK MODE:
 * If no real API key is configured (i.e. anthropic.api.key is still the
 * placeholder "your-api-key-here"), this service skips the real network call
 * and returns a locally-computed, realistic-looking analysis instead. This
 * lets you run and demo the entire app end-to-end — auth, DB, frontend, the
 * whole flow — without needing to pay for API credits. Once you add a real
 * key as an environment variable, it automatically switches to live AI calls.
 */
@Service
public class AiAnalysisService {

    @Value("${anthropic.api.key}")
    private String apiKey;

    @Value("${anthropic.api.url}")
    private String apiUrl;

    @Value("${anthropic.api.model}")
    private String model;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Random random = new Random();

    public AnalysisResult analyze(String resumeText, String jobDescription) {
        if (isMockMode()) {
            return mockAnalyze(resumeText, jobDescription);
        }

        String prompt = buildPrompt(resumeText, jobDescription);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", model);
        requestBody.put("max_tokens", 1000);
        requestBody.put("messages", List.of(
                Map.of("role", "user", "content", prompt)
        ));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-api-key", apiKey);
        headers.set("anthropic-version", "2023-06-01");

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, entity, String.class);
            return parseResponse(response.getBody());
        } catch (Exception e) {
            // Graceful fallback so the whole request doesn't 500
            // if the AI API is down, rate-limited, or misconfigured.
            return new AnalysisResult(
                    0,
                    "Could not reach AI service",
                    "AI analysis temporarily unavailable: " + e.getMessage()
            );
        }
    }

    private String buildPrompt(String resumeText, String jobDescription) {
        return """
                You are an expert technical recruiter. Compare the RESUME against the
                JOB DESCRIPTION below and respond with ONLY a raw JSON object
                (no markdown, no code fences, no extra text) in exactly this shape:

                {
                  "matchScore": <integer 0-100>,
                  "missingKeywords": "<comma-separated important keywords/skills present in the job description but missing from the resume>",
                  "suggestions": "<2-4 concise, actionable sentences on how to improve the resume for this specific job>"
                }

                RESUME:
                %s

                JOB DESCRIPTION:
                %s
                """.formatted(resumeText, jobDescription);
    }

    private AnalysisResult parseResponse(String rawBody) throws Exception {
        JsonNode root = objectMapper.readTree(rawBody);
        // Claude's response: { "content": [ { "type": "text", "text": "..." } ] }
        String aiText = root.path("content").get(0).path("text").asText();

        JsonNode parsed = objectMapper.readTree(aiText);

        int score = parsed.path("matchScore").asInt(0);
        String missing = parsed.path("missingKeywords").asText("");
        String suggestions = parsed.path("suggestions").asText("");

        return new AnalysisResult(score, missing, suggestions);
    }

    private boolean isMockMode() {
        return apiKey == null || apiKey.isBlank() || apiKey.equals("your-api-key-here");
    }

    /**
     * Produces a believable, non-random-feeling analysis using simple keyword
     * overlap between the resume and the job description — no external API
     * call, no cost. Good enough to demo the full app flow end-to-end.
     */
    private AnalysisResult mockAnalyze(String resumeText, String jobDescription) {
        List<String> jdKeywords = extractKeywords(jobDescription);
        List<String> resumeWords = extractKeywords(resumeText);

        List<String> missing = jdKeywords.stream()
                .filter(k -> !resumeWords.contains(k))
                .limit(6)
                .toList();

        int overlap = jdKeywords.isEmpty() ? 0 : jdKeywords.size() - missing.size();
        int score = jdKeywords.isEmpty()
                ? 50
                : Math.min(97, Math.max(20, (overlap * 100) / jdKeywords.size()));

        String missingKeywords = missing.isEmpty()
                ? "None detected — good keyword coverage!"
                : String.join(", ", missing);

        String suggestions = missing.isEmpty()
                ? "Your resume already covers most key terms from this job description. "
                  + "Consider quantifying your achievements with specific numbers or metrics to stand out further."
                : "Consider adding these missing keywords where genuinely applicable: " + missingKeywords + ". "
                  + "Tailor your project descriptions to mirror the language used in the job description, "
                  + "and make sure your most relevant experience appears near the top of your resume.";

        return new AnalysisResult(score, missingKeywords,
                "[MOCK MODE — set a real ANTHROPIC_API_KEY for live AI analysis] " + suggestions);
    }

    private static final Pattern WORD_PATTERN = Pattern.compile("[a-zA-Z][a-zA-Z0-9+.#]{2,}");
    private static final List<String> STOP_WORDS = List.of(
            "the", "and", "for", "with", "you", "are", "our", "will", "have",
            "this", "that", "your", "from", "has", "can", "role", "job",
            "work", "team", "using", "years", "experience", "ability", "strong"
    );

    private List<String> extractKeywords(String text) {
        List<String> words = new java.util.ArrayList<>();
        Matcher matcher = WORD_PATTERN.matcher(text.toLowerCase());
        while (matcher.find()) {
            String word = matcher.group();
            if (!STOP_WORDS.contains(word) && !words.contains(word)) {
                words.add(word);
            }
        }
        return words;
    }

    public record AnalysisResult(int matchScore, String missingKeywords, String suggestions) {}
}
