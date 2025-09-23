package com.zjxu.educationapp.modules.entity;

// SubjectAnalysis.java
public class SubjectAnalysis {
    private String subject;           // 科目
    private double averageScore;      // 平均分
    private int totalAssignments;     // 作业总数
    private String suggestion;        // 学习建议
    private int recommendedHours;     // 推荐学习时间(小时/周)
    
    public SubjectAnalysis() {}
    
    public SubjectAnalysis(String subject, double averageScore, int totalAssignments, String suggestion, int recommendedHours) {
        this.subject = subject;
        this.averageScore = averageScore;
        this.totalAssignments = totalAssignments;
        this.suggestion = suggestion;
        this.recommendedHours = recommendedHours;
    }
    
    // Getters and Setters
    public String getSubject() {
        return subject;
    }
    
    public void setSubject(String subject) {
        this.subject = subject;
    }
    
    public double getAverageScore() {
        return averageScore;
    }
    
    public void setAverageScore(double averageScore) {
        this.averageScore = averageScore;
    }
    
    public int getTotalAssignments() {
        return totalAssignments;
    }
    
    public void setTotalAssignments(int totalAssignments) {
        this.totalAssignments = totalAssignments;
    }
    
    public String getSuggestion() {
        return suggestion;
    }
    
    public void setSuggestion(String suggestion) {
        this.suggestion = suggestion;
    }
    
    public int getRecommendedHours() {
        return recommendedHours;
    }
    
    public void setRecommendedHours(int recommendedHours) {
        this.recommendedHours = recommendedHours;
    }
}
