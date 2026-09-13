package com.ara.resumeanalyzer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * AI Resume Analyzer — entry point.
 *
 * Lets a logged-in user paste their resume text and a target job description,
 * sends both to an LLM (Claude/OpenAI) for analysis, and stores + returns
 * a match score, missing keywords, and improvement suggestions.
 */
@SpringBootApplication
public class ResumeAnalyzerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ResumeAnalyzerApplication.class, args);
    }

}
