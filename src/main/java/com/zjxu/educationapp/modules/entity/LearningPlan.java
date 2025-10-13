package com.zjxu.educationapp.modules.entity;
import java.util.List;

public class LearningPlan {
    private List<SubjectAnalysis> subjectAnalyses;
    private String overallSuggestion;
    private String leidaPicture;
    
    public LearningPlan() {}
    
    public LearningPlan(List<SubjectAnalysis> subjectAnalyses, String overallSuggestion,String leidaPicture) {
        this.subjectAnalyses = subjectAnalyses;
        this.overallSuggestion = overallSuggestion;
        this.leidaPicture = leidaPicture;
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

    public String getLeidaPicture() {
        return leidaPicture;
    }

}
