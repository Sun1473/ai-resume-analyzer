package com.ara.resumeanalyzer.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public class AnalysisDtos {

    public static class AnalysisRequest {
        @NotBlank(message = "Resume text is required")
        private String resumeText;

        @NotBlank(message = "Job description is required")
        private String jobDescription;

        public AnalysisRequest() {
        }

        public AnalysisRequest(String resumeText, String jobDescription) {
            this.resumeText = resumeText;
            this.jobDescription = jobDescription;
        }

        public String getResumeText() {
            return resumeText;
        }

        public void setResumeText(String resumeText) {
            this.resumeText = resumeText;
        }

        public String getJobDescription() {
            return jobDescription;
        }

        public void setJobDescription(String jobDescription) {
            this.jobDescription = jobDescription;
        }
    }

    public static class AnalysisResponse {
        private Long id;
        private Integer matchScore;
        private String missingKeywords;
        private String suggestions;
        private LocalDateTime createdAt;

        public AnalysisResponse(Long id, Integer matchScore, String missingKeywords,
                                 String suggestions, LocalDateTime createdAt) {
            this.id = id;
            this.matchScore = matchScore;
            this.missingKeywords = missingKeywords;
            this.suggestions = suggestions;
            this.createdAt = createdAt;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Integer getMatchScore() {
            return matchScore;
        }

        public void setMatchScore(Integer matchScore) {
            this.matchScore = matchScore;
        }

        public String getMissingKeywords() {
            return missingKeywords;
        }

        public void setMissingKeywords(String missingKeywords) {
            this.missingKeywords = missingKeywords;
        }

        public String getSuggestions() {
            return suggestions;
        }

        public void setSuggestions(String suggestions) {
            this.suggestions = suggestions;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
        }
    }
}
