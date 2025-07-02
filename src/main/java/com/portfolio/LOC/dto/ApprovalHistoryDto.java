package com.portfolio.LOC.dto;

import com.portfolio.LOC.entity.ApprovalHistory;

import java.time.LocalDateTime;

public class ApprovalHistoryDto {
    
    private Long id;
    private String stage;
    private String decision;
    private String remarks;
    private String approvedByName;
    private String approvedByEmail;
    private LocalDateTime createdAt;
    
    // Default constructor
    public ApprovalHistoryDto() {}
    
    // Constructor from entity
    public ApprovalHistoryDto(ApprovalHistory approvalHistory) {
        this.id = approvalHistory.getId();
        this.stage = approvalHistory.getStage().toString();
        this.decision = approvalHistory.getDecision().toString();
        this.remarks = approvalHistory.getRemarks();
        this.approvedByName = approvalHistory.getApprovedBy().getName();
        this.approvedByEmail = approvalHistory.getApprovedBy().getEmail();
        this.createdAt = approvalHistory.getCreatedAt();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getStage() {
        return stage;
    }
    
    public void setStage(String stage) {
        this.stage = stage;
    }
    
    public String getDecision() {
        return decision;
    }
    
    public void setDecision(String decision) {
        this.decision = decision;
    }
    
    public String getRemarks() {
        return remarks;
    }
    
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
    
    public String getApprovedByName() {
        return approvedByName;
    }
    
    public void setApprovedByName(String approvedByName) {
        this.approvedByName = approvedByName;
    }
    
    public String getApprovedByEmail() {
        return approvedByEmail;
    }
    
    public void setApprovedByEmail(String approvedByEmail) {
        this.approvedByEmail = approvedByEmail;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
