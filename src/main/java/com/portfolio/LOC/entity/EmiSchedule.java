package com.portfolio.LOC.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "emi_schedule")
public class EmiSchedule {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_application_id", nullable = false)
    private LoanApplication loanApplication;
    
    @NotNull(message = "EMI number is required")
    @Column(name = "emi_number", nullable = false)
    private Integer emiNumber;
    
    @NotNull(message = "Due date is required")
    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;
    
    @NotNull(message = "EMI amount is required")
    @Column(name = "emi_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal emiAmount;
    
    @NotNull(message = "Principal amount is required")
    @Column(name = "principal_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal principalAmount;
    
    @NotNull(message = "Interest amount is required")
    @Column(name = "interest_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal interestAmount;
    
    @NotNull(message = "Outstanding balance is required")
    @Column(name = "outstanding_balance", nullable = false, precision = 12, scale = 2)
    private BigDecimal outstandingBalance;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private EmiStatus status = EmiStatus.PENDING;
    
    @Column(name = "paid_date")
    private LocalDate paidDate;
    
    @Column(name = "paid_amount", precision = 12, scale = 2)
    private BigDecimal paidAmount;
    
    @Column(name = "late_fee", precision = 12, scale = 2)
    private BigDecimal lateFee;
    
    @Column(name = "remarks")
    private String remarks;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Enums
    public enum EmiStatus {
        PENDING, PAID, OVERDUE, PARTIAL_PAID
    }
    
    // Constructors
    public EmiSchedule() {
        this.createdAt = LocalDateTime.now();
    }
    
    public EmiSchedule(LoanApplication loanApplication, Integer emiNumber, LocalDate dueDate, 
                      BigDecimal emiAmount, BigDecimal principalAmount, BigDecimal interestAmount, 
                      BigDecimal outstandingBalance) {
        this();
        this.loanApplication = loanApplication;
        this.emiNumber = emiNumber;
        this.dueDate = dueDate;
        this.emiAmount = emiAmount;
        this.principalAmount = principalAmount;
        this.interestAmount = interestAmount;
        this.outstandingBalance = outstandingBalance;
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
    
    public Integer getEmiNumber() {
        return emiNumber;
    }
    
    public void setEmiNumber(Integer emiNumber) {
        this.emiNumber = emiNumber;
    }
    
    public LocalDate getDueDate() {
        return dueDate;
    }
    
    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }
    
    public BigDecimal getEmiAmount() {
        return emiAmount;
    }
    
    public void setEmiAmount(BigDecimal emiAmount) {
        this.emiAmount = emiAmount;
    }
    
    public BigDecimal getPrincipalAmount() {
        return principalAmount;
    }
    
    public void setPrincipalAmount(BigDecimal principalAmount) {
        this.principalAmount = principalAmount;
    }
    
    public BigDecimal getInterestAmount() {
        return interestAmount;
    }
    
    public void setInterestAmount(BigDecimal interestAmount) {
        this.interestAmount = interestAmount;
    }
    
    public BigDecimal getOutstandingBalance() {
        return outstandingBalance;
    }
    
    public void setOutstandingBalance(BigDecimal outstandingBalance) {
        this.outstandingBalance = outstandingBalance;
    }
    
    public EmiStatus getStatus() {
        return status;
    }
    
    public void setStatus(EmiStatus status) {
        this.status = status;
    }
    
    public LocalDate getPaidDate() {
        return paidDate;
    }
    
    public void setPaidDate(LocalDate paidDate) {
        this.paidDate = paidDate;
    }
    
    public BigDecimal getPaidAmount() {
        return paidAmount;
    }
    
    public void setPaidAmount(BigDecimal paidAmount) {
        this.paidAmount = paidAmount;
    }
    
    public BigDecimal getLateFee() {
        return lateFee;
    }
    
    public void setLateFee(BigDecimal lateFee) {
        this.lateFee = lateFee;
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
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
