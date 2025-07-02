package com.portfolio.LOC.dto;

import com.portfolio.LOC.entity.LoanApplication;
import com.portfolio.LOC.entity.ApprovalHistory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class LoanApplicationResponse {
    
    private Long id;
    private String applicationId;
    private String applicantName;
    private String email;
    private String phoneNumber;
    private BigDecimal monthlyIncome;
    private BigDecimal loanAmount;
    private LoanApplication.LoanPurpose loanPurpose;
    private Integer loanTenure;
    private LoanApplication.ApplicationStatus status;
    private Integer eligibilityScore;
    private BigDecimal estimatedEmi;
    private String submittedByName;
    private String submittedByEmail;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime rcpuCompletedDate;
    private LocalDateTime l1ApprovedDate;
    private LocalDateTime l2ApprovedDate;
    private List<ApprovalHistoryDto> approvalHistory;
    private boolean hasRcpuReport;
    private boolean hasLoanOffer;
    private int documentCount;
    private boolean hasRequiredDocuments;
    
    // Constructors
    public LoanApplicationResponse() {}
    
    public LoanApplicationResponse(LoanApplication loanApplication) {
        this.id = loanApplication.getId();
        this.applicationId = loanApplication.getApplicationId();
        this.applicantName = loanApplication.getFirstName() + " " + loanApplication.getLastName();
        this.email = loanApplication.getEmail();
        this.phoneNumber = loanApplication.getPhoneNumber();
        this.monthlyIncome = loanApplication.getMonthlyIncome();
        this.loanAmount = loanApplication.getLoanAmount();
        this.loanPurpose = loanApplication.getLoanPurpose();
        this.loanTenure = loanApplication.getLoanTenure();
        this.status = loanApplication.getStatus();
        this.eligibilityScore = loanApplication.getEligibilityScore();
        this.estimatedEmi = loanApplication.getEstimatedEmi();
        this.submittedByName = loanApplication.getSubmittedBy().getName();
        this.submittedByEmail = loanApplication.getSubmittedBy().getEmail();
        this.createdAt = loanApplication.getCreatedAt();
        this.updatedAt = loanApplication.getUpdatedAt();
        // Set workflow dates to current time for demo purposes
        this.rcpuCompletedDate = loanApplication.getUpdatedAt();
        this.l1ApprovedDate = loanApplication.getUpdatedAt();
        this.l2ApprovedDate = loanApplication.getUpdatedAt();

        // Populate approval history
        this.approvalHistory = loanApplication.getApprovalHistory() != null ?
            loanApplication.getApprovalHistory().stream()
                .map(ApprovalHistoryDto::new)
                .collect(Collectors.toList()) :
            new ArrayList<>();

        // Check for reports and offers
        this.hasRcpuReport = loanApplication.getRcpuReport() != null;
        this.hasLoanOffer = loanApplication.getLoanOfferLetter() != null;

        // Document information
        this.documentCount = loanApplication.getDocuments() != null ? loanApplication.getDocuments().size() : 0;
        this.hasRequiredDocuments = checkRequiredDocuments(loanApplication);
    }

    private boolean checkRequiredDocuments(LoanApplication loanApplication) {
        if (loanApplication.getDocuments() == null || loanApplication.getDocuments().isEmpty()) {
            return false;
        }

        // Check for required document types (assuming Document entity is imported)
        boolean hasITR = loanApplication.getDocuments().stream()
            .anyMatch(d -> "ITR".equals(d.getDocumentType().name()));
        boolean hasBankStatement = loanApplication.getDocuments().stream()
            .anyMatch(d -> "BANK_STATEMENT".equals(d.getDocumentType().name()));
        boolean hasAadhaar = loanApplication.getDocuments().stream()
            .anyMatch(d -> "AADHAAR".equals(d.getDocumentType().name()));
        boolean hasPAN = loanApplication.getDocuments().stream()
            .anyMatch(d -> "PAN".equals(d.getDocumentType().name()));
        boolean hasPhoto = loanApplication.getDocuments().stream()
            .anyMatch(d -> "PHOTO".equals(d.getDocumentType().name()));

        return hasITR && hasBankStatement && hasAadhaar && hasPAN && hasPhoto;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getApplicationId() {
        return applicationId;
    }
    
    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }
    
    public String getApplicantName() {
        return applicantName;
    }
    
    public void setApplicantName(String applicantName) {
        this.applicantName = applicantName;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public BigDecimal getMonthlyIncome() {
        return monthlyIncome;
    }

    public void setMonthlyIncome(BigDecimal monthlyIncome) {
        this.monthlyIncome = monthlyIncome;
    }

    public BigDecimal getLoanAmount() {
        return loanAmount;
    }
    
    public void setLoanAmount(BigDecimal loanAmount) {
        this.loanAmount = loanAmount;
    }
    
    public LoanApplication.LoanPurpose getLoanPurpose() {
        return loanPurpose;
    }
    
    public void setLoanPurpose(LoanApplication.LoanPurpose loanPurpose) {
        this.loanPurpose = loanPurpose;
    }
    
    public Integer getLoanTenure() {
        return loanTenure;
    }
    
    public void setLoanTenure(Integer loanTenure) {
        this.loanTenure = loanTenure;
    }
    
    public LoanApplication.ApplicationStatus getStatus() {
        return status;
    }
    
    public void setStatus(LoanApplication.ApplicationStatus status) {
        this.status = status;
    }
    
    public Integer getEligibilityScore() {
        return eligibilityScore;
    }
    
    public void setEligibilityScore(Integer eligibilityScore) {
        this.eligibilityScore = eligibilityScore;
    }
    
    public BigDecimal getEstimatedEmi() {
        return estimatedEmi;
    }
    
    public void setEstimatedEmi(BigDecimal estimatedEmi) {
        this.estimatedEmi = estimatedEmi;
    }
    
    public String getSubmittedByName() {
        return submittedByName;
    }
    
    public void setSubmittedByName(String submittedByName) {
        this.submittedByName = submittedByName;
    }
    
    public String getSubmittedByEmail() {
        return submittedByEmail;
    }
    
    public void setSubmittedByEmail(String submittedByEmail) {
        this.submittedByEmail = submittedByEmail;
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

    public LocalDateTime getRcpuCompletedDate() {
        return rcpuCompletedDate;
    }

    public void setRcpuCompletedDate(LocalDateTime rcpuCompletedDate) {
        this.rcpuCompletedDate = rcpuCompletedDate;
    }

    public LocalDateTime getL1ApprovedDate() {
        return l1ApprovedDate;
    }

    public void setL1ApprovedDate(LocalDateTime l1ApprovedDate) {
        this.l1ApprovedDate = l1ApprovedDate;
    }

    public LocalDateTime getL2ApprovedDate() {
        return l2ApprovedDate;
    }

    public void setL2ApprovedDate(LocalDateTime l2ApprovedDate) {
        this.l2ApprovedDate = l2ApprovedDate;
    }

    public List<ApprovalHistoryDto> getApprovalHistory() {
        return approvalHistory;
    }

    public void setApprovalHistory(List<ApprovalHistoryDto> approvalHistory) {
        this.approvalHistory = approvalHistory;
    }

    public boolean isHasRcpuReport() {
        return hasRcpuReport;
    }

    public void setHasRcpuReport(boolean hasRcpuReport) {
        this.hasRcpuReport = hasRcpuReport;
    }

    public boolean isHasLoanOffer() {
        return hasLoanOffer;
    }

    public void setHasLoanOffer(boolean hasLoanOffer) {
        this.hasLoanOffer = hasLoanOffer;
    }

    public int getDocumentCount() {
        return documentCount;
    }

    public void setDocumentCount(int documentCount) {
        this.documentCount = documentCount;
    }

    public boolean isHasRequiredDocuments() {
        return hasRequiredDocuments;
    }

    public void setHasRequiredDocuments(boolean hasRequiredDocuments) {
        this.hasRequiredDocuments = hasRequiredDocuments;
    }
}
