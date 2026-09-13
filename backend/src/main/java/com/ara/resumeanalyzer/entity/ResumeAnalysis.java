package com.ara.resumeanalyzer.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Stores the result of a single resume-vs-job-description analysis,
 * linked to the user who requested it, so users can see their history.
 *
 * Note: getters/setters are written by hand instead of using Lombok,
 * to keep the project free of any Lombok/JDK version compatibility issues.
 */
@Entity
@Table(name = "resume_analyses")
public class ResumeAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String resumeText;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String jobDescription;

    private Integer matchScore;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String missingKeywords;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String suggestions;

    private LocalDateTime createdAt = LocalDateTime.now();

    public ResumeAnalysis() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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
