import React, { useState } from "react";
import { resumeService } from "../services/api";

// Core feature UI: paste resume + JD, submit, show the AI's structured feedback.
export default function AnalyzerForm({ onNewAnalysis }) {
  const [resumeText, setResumeText] = useState("");
  const [jobDescription, setJobDescription] = useState("");
  const [result, setResult] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError("");
    setResult(null);
    try {
      const res = await resumeService.analyze({ resumeText, jobDescription });
      setResult(res.data);
      onNewAnalysis();
    } catch (err) {
      setError(err.response?.data?.message || "Analysis failed. Please try again.");
    } finally {
      setLoading(false);
    }
  };

  const scoreColor = (score) => {
    if (score >= 75) return "#2e7d32";
    if (score >= 50) return "#e6a817";
    return "#c0392b";
  };

  return (
    <div className="analyzer-card">
      <h2>Analyze Your Resume</h2>
      <p className="subtitle">
        Paste your resume text and a target job description — AI will score the match
        and suggest improvements.
      </p>

      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label>Resume Text</label>
          <textarea
            rows={8}
            value={resumeText}
            onChange={(e) => setResumeText(e.target.value)}
            placeholder="Paste your resume content here..."
            required
          />
        </div>

        <div className="form-group">
          <label>Job Description</label>
          <textarea
            rows={8}
            value={jobDescription}
            onChange={(e) => setJobDescription(e.target.value)}
            placeholder="Paste the job description here..."
            required
          />
        </div>

        {error && <p className="error">{error}</p>}

        <button type="submit" disabled={loading}>
          {loading ? "Analyzing with AI..." : "Analyze Match"}
        </button>
      </form>

      {result && (
        <div className="result-box">
          <div className="score-circle" style={{ borderColor: scoreColor(result.matchScore) }}>
            <span style={{ color: scoreColor(result.matchScore) }}>{result.matchScore}%</span>
            <small>Match Score</small>
          </div>

          <div className="result-details">
            <h4>Missing Keywords</h4>
            <p>{result.missingKeywords || "None found — good coverage!"}</p>

            <h4>Suggestions</h4>
            <p>{result.suggestions}</p>
          </div>
        </div>
      )}
    </div>
  );
}
