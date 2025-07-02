package com.portfolio.LOC.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
@Table(name = "rcpu_reports")
public class RcpuReport {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "loan_application_id", nullable = false)
    private LoanApplication loanApplication;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by", nullable = false)
    private User reviewedBy;
    
    @NotBlank(message = "Report file name is required")
    @Column(name = "report_file_name", nullable = false)
    private String reportFileName;
    
    @NotBlank(message = "Report file path is required")
    @Column(name = "report_file_path", nullable = false)
    private String reportFilePath;
    
    @Column(name = "remarks", columnDefinition = "TEXT")
    private String remarks;
    
    @NotNull(message = "Credit score is required")
    @Column(name = "credit_score", nullable = false)
    private Integer creditScore;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "recommendation", nullable = false)
    private Recommendation recommendation;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Enums
    public enum Recommendation {
        APPROVE, REJECT, REVIEW_REQUIRED
    }
    
    // Constructors
    public RcpuReport() {
        this.createdAt = LocalDateTime.now();
    }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public LoanApplication getLoanApplication() {
        return loanApplication;
    }
    
    public void setLoanApplication(LoanApplication loanApplication) {
        this.loanApplication = loanApplication;
    }
    
    public User getReviewedBy() {
        return reviewedBy;
    }
    
    public void setReviewedBy(User reviewedBy) {
        this.reviewedBy = reviewedBy;
    }
    
    public String getReportFileName() {
        return reportFileName;
    }
    
    public void setReportFileName(String reportFileName) {
        this.reportFileName = reportFileName;
    }
    
    public String getReportFilePath() {
        return reportFilePath;
    }
    
    public void setReportFilePath(String reportFilePath) {
        this.reportFilePath = reportFilePath;
    }
    
    public String getRemarks() {
        return remarks;
    }
    
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
    
    public Integer getCreditScore() {
        return creditScore;
    }
    
    public void setCreditScore(Integer creditScore) {
        this.creditScore = creditScore;
    }
    
    public Recommendation getRecommendation() {
        return recommendation;
    }
    
    public void setRecommendation(Recommendation recommendation) {
        this.recommendation = recommendation;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
