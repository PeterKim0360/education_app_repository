package com.zjxu.educationapp.modules.entity;
import java.util.List;

public class LearningPlan {
    private List<SubjectAnalysis> subjectAnalyses;
    private String overallSuggestion;
    
    public LearningPlan() {}
    
    public LearningPlan(List<SubjectAnalysis> subjectAnalyses, String overallSuggestion) {
        this.subjectAnalyses = subjectAnalyses;
        this.overallSuggestion = overallSuggestion;
    }
    
    // Getters and Setters
    public List<SubjectAnalysis> getSubjectAnalyses() {
        return subjectAnalyses;
    }
    
    public void setSubjectAnalyses(List<SubjectAnalysis> subjectAnalyses) {
        this.subjectAnalyses = subjectAnalyses;
    }
    
    public String getOverallSuggestion() {
        return overallSuggestion;
    }
    
    public void setOverallSuggestion(String overallSuggestion) {
        this.overallSuggestion = overallSuggestion;
    }
}
