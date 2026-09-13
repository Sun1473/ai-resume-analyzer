package com.ara.resumeanalyzer.controller;

import com.ara.resumeanalyzer.dto.AnalysisDtos.AnalysisRequest;
import com.ara.resumeanalyzer.dto.AnalysisDtos.AnalysisResponse;
import com.ara.resumeanalyzer.service.ResumeAnalysisServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Protected endpoints (JWT required) for submitting a resume analysis
 * and viewing past analysis history for the logged-in user.
 */
@RestController
@RequestMapping("/api/resume")
@CrossOrigin(origins = {"http://localhost:3000", "https://ai-resume-analyzer-mmzsbd6md-naman-092d.vercel.app"})
public class ResumeController {

    private final ResumeAnalysisServiceImpl analysisService;

    @Autowired
    public ResumeController(ResumeAnalysisServiceImpl analysisService) {
        this.analysisService = analysisService;
    }

    @PostMapping("/analyze")
    public ResponseEntity<AnalysisResponse> analyze(
            @Valid @RequestBody AnalysisRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        AnalysisResponse response = analysisService.analyzeAndSave(userDetails.getUsername(), request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/history")
    public ResponseEntity<List<AnalysisResponse>> history(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(analysisService.getHistory(userDetails.getUsername()));
    }
}
