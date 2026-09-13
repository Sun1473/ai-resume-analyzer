package com.ara.resumeanalyzer.repository;

import com.ara.resumeanalyzer.entity.ResumeAnalysis;
import com.ara.resumeanalyzer.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResumeAnalysisRepository extends JpaRepository<ResumeAnalysis, Long> {
    List<ResumeAnalysis> findByUserOrderByCreatedAtDesc(User user);
}
