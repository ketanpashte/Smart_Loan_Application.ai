package com.portfolio.LOC.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "loan_offer_letters")
public class LoanOfferLetter {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "loan_application_id", nullable = false)
    private LoanApplication loanApplication;
    
    @NotBlank(message = "Offer letter number is required")
    @Column(name = "offer_letter_number", unique = true, nullable = false)
    private String offerLetterNumber;
    
    @NotBlank(message = "File name is required")
    @Column(name = "file_name", nullable = false)
    private String fileName;
    
    @NotBlank(message = "File path is required")
    @Column(name = "file_path", nullable = false)
    private String filePath;
    
    @NotNull(message = "Approved loan amount is required")
    @Column(name = "approved_loan_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal approvedLoanAmount;
    
    @NotNull(message = "Interest rate is required")
    @Column(name = "interest_rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal interestRate;
    
    @NotNull(message = "Loan tenure is required")
    @Column(name = "loan_tenure", nullable = false)
    private Integer loanTenure;
    
    @NotNull(message = "EMI amount is required")
    @Column(name = "emi_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal emiAmount;
    
    @Column(name = "processing_fee", precision = 12, scale = 2)
    private BigDecimal processingFee;
    
    @Column(name = "terms_and_conditions", columnDefinition = "TEXT")
    private String termsAndConditions;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "generated_by", nullable = false)
    private User generatedBy;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Constructors
    public LoanOfferLetter() {
        this.createdAt = LocalDateTime.now();
        generateOfferLetterNumber();
    }
    
    private void generateOfferLetterNumber() {
        this.offerLetterNumber = "OL" + String.format("%08d", System.currentTimeMillis() % 100000000);
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
    
    public String getOfferLetterNumber() {
        return offerLetterNumber;
    }
    
    public void setOfferLetterNumber(String offerLetterNumber) {
        this.offerLetterNumber = offerLetterNumber;
    }
    
    public String getFileName() {
        return fileName;
    }
    
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    public String getFilePath() {
        return filePath;
    }
    
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
    
    public BigDecimal getApprovedLoanAmount() {
        return approvedLoanAmount;
    }
    
    public void setApprovedLoanAmount(BigDecimal approvedLoanAmount) {
        this.approvedLoanAmount = approvedLoanAmount;
    }
    
    public BigDecimal getInterestRate() {
        return interestRate;
    }
    
    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }
    
    public Integer getLoanTenure() {
        return loanTenure;
    }
    
    public void setLoanTenure(Integer loanTenure) {
        this.loanTenure = loanTenure;
    }
    
    public BigDecimal getEmiAmount() {
        return emiAmount;
    }
    
    public void setEmiAmount(BigDecimal emiAmount) {
        this.emiAmount = emiAmount;
    }
    
    public BigDecimal getProcessingFee() {
        return processingFee;
    }
    
    public void setProcessingFee(BigDecimal processingFee) {
        this.processingFee = processingFee;
    }
    
    public String getTermsAndConditions() {
        return termsAndConditions;
    }
    
    public void setTermsAndConditions(String termsAndConditions) {
        this.termsAndConditions = termsAndConditions;
    }
    
    public User getGeneratedBy() {
        return generatedBy;
    }
    
    public void setGeneratedBy(User generatedBy) {
        this.generatedBy = generatedBy;
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
