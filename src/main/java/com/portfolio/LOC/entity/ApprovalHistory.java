package com.portfolio.LOC.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
@Table(name = "approval_history")
public class ApprovalHistory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_application_id", nullable = false)
    private LoanApplication loanApplication;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by", nullable = false)
    private User approvedBy;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "stage", nullable = false)
    private Stage stage;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "decision", nullable = false)
    private Decision decision;
    
    @Column(name = "remarks", columnDefinition = "TEXT")
    private String remarks;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    // Enums
    public enum Stage {
        RCPU, L1, L2, L3
    }
    
    public enum Decision {
        APPROVED, REJECTED, FORWARDED
    }
    
    // Constructors
    public ApprovalHistory() {
        this.createdAt = LocalDateTime.now();
    }
    
    public ApprovalHistory(LoanApplication loanApplication, User approvedBy, Stage stage, Decision decision, String remarks) {
        this();
        this.loanApplication = loanApplication;
        this.approvedBy = approvedBy;
        this.stage = stage;
        this.decision = decision;
        this.remarks = remarks;
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
    
    public User getApprovedBy() {
        return approvedBy;
    }
    
    public void setApprovedBy(User approvedBy) {
        this.approvedBy = approvedBy;
    }
    
    public Stage getStage() {
        return stage;
    }
    
    public void setStage(Stage stage) {
        this.stage = stage;
    }
    
    public Decision getDecision() {
        return decision;
    }
    
    public void setDecision(Decision decision) {
        this.decision = decision;
    }
    
    public String getRemarks() {
        return remarks;
    }
    
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
