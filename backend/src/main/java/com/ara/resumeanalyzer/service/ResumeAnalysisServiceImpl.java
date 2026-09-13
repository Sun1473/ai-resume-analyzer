package com.ara.resumeanalyzer.service;

import com.ara.resumeanalyzer.dto.AnalysisDtos.AnalysisRequest;
import com.ara.resumeanalyzer.dto.AnalysisDtos.AnalysisResponse;
import com.ara.resumeanalyzer.entity.ResumeAnalysis;
import com.ara.resumeanalyzer.entity.User;
import com.ara.resumeanalyzer.repository.ResumeAnalysisRepository;
import com.ara.resumeanalyzer.repository.UserRepository;
import com.ara.resumeanalyzer.service.AiAnalysisService.AnalysisResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Orchestrates a resume analysis request: calls the AI service, saves the
 * result against the logged-in user, and maps entities to DTOs for the API layer.
 */
@Service
public class ResumeAnalysisServiceImpl {

    private final AiAnalysisService aiAnalysisService;
    private final ResumeAnalysisRepository analysisRepository;
    private final UserRepository userRepository;

    @Autowired
    public ResumeAnalysisServiceImpl(AiAnalysisService aiAnalysisService,
                                      ResumeAnalysisRepository analysisRepository,
                                      UserRepository userRepository) {
        this.aiAnalysisService = aiAnalysisService;
        this.analysisRepository = analysisRepository;
        this.userRepository = userRepository;
    }

    public AnalysisResponse analyzeAndSave(String username, AnalysisRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("User not found: " + username));

        AnalysisResult result = aiAnalysisService.analyze(
                request.getResumeText(), request.getJobDescription());

        ResumeAnalysis analysis = new ResumeAnalysis();
        analysis.setUser(user);
        analysis.setResumeText(request.getResumeText());
        analysis.setJobDescription(request.getJobDescription());
        analysis.setMatchScore(result.matchScore());
        analysis.setMissingKeywords(result.missingKeywords());
        analysis.setSuggestions(result.suggestions());

        ResumeAnalysis saved = analysisRepository.save(analysis);
        return toResponse(saved);
    }

    public List<AnalysisResponse> getHistory(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("User not found: " + username));

        return analysisRepository.findByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private AnalysisResponse toResponse(ResumeAnalysis a) {
        return new AnalysisResponse(
                a.getId(), a.getMatchScore(), a.getMissingKeywords(),
                a.getSuggestions(), a.getCreatedAt()
        );
    }
}
