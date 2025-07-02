package com.portfolio.LOC.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "loan_applications")
public class LoanApplication {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "application_id", unique = true, nullable = false)
    private String applicationId;
    
    // Personal Information
    @NotBlank(message = "First name is required")
    @Column(name = "first_name", nullable = false)
    private String firstName;
    
    @NotBlank(message = "Last name is required")
    @Column(name = "last_name", nullable = false)
    private String lastName;
    
    @NotNull(message = "Date of birth is required")
    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;
    
    @NotBlank(message = "PAN number is required")
    @Pattern(regexp = "[A-Z]{5}[0-9]{4}[A-Z]{1}", message = "Invalid PAN number format")
    @Column(name = "pan_number", nullable = false, unique = true)
    private String panNumber;
    
    @NotBlank(message = "Aadhaar number is required")
    @Pattern(regexp = "\\d{12}", message = "Aadhaar number must be 12 digits")
    @Column(name = "aadhaar_number", nullable = false)
    private String aadhaarNumber;
    
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "\\d{10}", message = "Phone number must be 10 digits")
    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Column(nullable = false)
    private String email;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "marital_status")
    private MaritalStatus maritalStatus;
    
    // Address Information
    @Column(name = "current_street", nullable = false)
    private String currentStreet;
    
    @Column(name = "current_city", nullable = false)
    private String currentCity;
    
    @Column(name = "current_state", nullable = false)
    private String currentState;
    
    @Column(name = "current_pincode", nullable = false)
    private String currentPincode;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "residence_type", nullable = false)
    private ResidenceType residenceType;
    
    @Column(name = "permanent_street")
    private String permanentStreet;
    
    @Column(name = "permanent_city")
    private String permanentCity;
    
    @Column(name = "permanent_state")
    private String permanentState;
    
    @Column(name = "permanent_pincode")
    private String permanentPincode;
    
    @Column(name = "same_as_current")
    private Boolean sameAsCurrent = false;
    
    // Financial Information
    @Enumerated(EnumType.STRING)
    @Column(name = "employment_type", nullable = false)
    private EmploymentType employmentType;
    
    @NotNull(message = "Monthly income is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Monthly income must be positive")
    @Column(name = "monthly_income", nullable = false, precision = 12, scale = 2)
    private BigDecimal monthlyIncome;
    
    @NotBlank(message = "Employer is required")
    @Column(nullable = false)
    private String employer;
    
    @NotNull(message = "Work experience is required")
    @Min(value = 0, message = "Work experience cannot be negative")
    @Column(name = "work_experience", nullable = false)
    private Integer workExperience;
    
    @NotNull(message = "Loan amount is required")
    @DecimalMin(value = "50000.0", message = "Minimum loan amount is 50,000")
    @DecimalMax(value = "10000000.0", message = "Maximum loan amount is 1,00,00,000")
    @Column(name = "loan_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal loanAmount;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "loan_purpose", nullable = false)
    private LoanPurpose loanPurpose;
    
    @NotNull(message = "Loan tenure is required")
    @Min(value = 5, message = "Minimum loan tenure is 5 years")
    @Max(value = 30, message = "Maximum loan tenure is 30 years")
    @Column(name = "loan_tenure", nullable = false)
    private Integer loanTenure;
    
    // Application Status and Workflow
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus status = ApplicationStatus.SUBMITTED;
    
    @Column(name = "eligibility_score")
    private Integer eligibilityScore;
    
    @Column(name = "estimated_emi", precision = 12, scale = 2)
    private BigDecimal estimatedEmi;
    
    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submitted_by", nullable = false)
    private User submittedBy;

    @OneToOne(mappedBy = "loanApplication", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private RcpuReport rcpuReport;

    @OneToMany(mappedBy = "loanApplication", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ApprovalHistory> approvalHistory = new ArrayList<>();

    @OneToOne(mappedBy = "loanApplication", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private LoanOfferLetter loanOfferLetter;

    @OneToMany(mappedBy = "loanApplication", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<EmiSchedule> emiSchedules = new ArrayList<>();

    @OneToMany(mappedBy = "loanApplication", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Document> documents = new ArrayList<>();
    
    // Timestamps
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Enums
    public enum Gender {
        MALE, FEMALE, OTHER
    }
    
    public enum MaritalStatus {
        SINGLE, MARRIED, DIVORCED, WIDOWED
    }
    
    public enum ResidenceType {
        OWNED, RENTED, FAMILY_OWNED
    }
    
    public enum EmploymentType {
        SALARIED, SELF_EMPLOYED, BUSINESS, PROFESSIONAL
    }
    
    public enum LoanPurpose {
        HOME_PURCHASE, HOME_CONSTRUCTION, HOME_RENOVATION, 
        PLOT_PURCHASE, BALANCE_TRANSFER, TOP_UP
    }
    
    public enum ApplicationStatus {
        SUBMITTED, PENDING_RCPU, RCPU_COMPLETED, PENDING_L1, 
        L1_APPROVED, PENDING_L2, L2_APPROVED, PENDING_L3, 
        L3_APPROVED, REJECTED, CANCELLED
    }
    
    // Constructors
    public LoanApplication() {
        this.createdAt = LocalDateTime.now();
        generateApplicationId();
    }
    
    private void generateApplicationId() {
        this.applicationId = "LA" + String.format("%06d", System.currentTimeMillis() % 1000000);
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
    
    public String getApplicationId() {
        return applicationId;
    }
    
    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }
    
    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
    
    public Gender getGender() {
        return gender;
    }
    
    public void setGender(Gender gender) {
        this.gender = gender;
    }
    
    public String getPanNumber() {
        return panNumber;
    }
    
    public void setPanNumber(String panNumber) {
        this.panNumber = panNumber;
    }
    
    public String getAadhaarNumber() {
        return aadhaarNumber;
    }
    
    public void setAadhaarNumber(String aadhaarNumber) {
        this.aadhaarNumber = aadhaarNumber;
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public MaritalStatus getMaritalStatus() {
        return maritalStatus;
    }
    
    public void setMaritalStatus(MaritalStatus maritalStatus) {
        this.maritalStatus = maritalStatus;
    }
    
    public String getCurrentStreet() {
        return currentStreet;
    }
    
    public void setCurrentStreet(String currentStreet) {
        this.currentStreet = currentStreet;
    }
    
    public String getCurrentCity() {
        return currentCity;
    }
    
    public void setCurrentCity(String currentCity) {
        this.currentCity = currentCity;
    }
    
    public String getCurrentState() {
        return currentState;
    }
    
    public void setCurrentState(String currentState) {
        this.currentState = currentState;
    }
    
    public String getCurrentPincode() {
        return currentPincode;
    }
    
    public void setCurrentPincode(String currentPincode) {
        this.currentPincode = currentPincode;
    }
    
    public ResidenceType getResidenceType() {
        return residenceType;
    }
    
    public void setResidenceType(ResidenceType residenceType) {
        this.residenceType = residenceType;
    }
    
    public String getPermanentStreet() {
        return permanentStreet;
    }
    
    public void setPermanentStreet(String permanentStreet) {
        this.permanentStreet = permanentStreet;
    }
    
    public String getPermanentCity() {
        return permanentCity;
    }
    
    public void setPermanentCity(String permanentCity) {
        this.permanentCity = permanentCity;
    }
    
    public String getPermanentState() {
        return permanentState;
    }
    
    public void setPermanentState(String permanentState) {
        this.permanentState = permanentState;
    }
    
    public String getPermanentPincode() {
        return permanentPincode;
    }
    
    public void setPermanentPincode(String permanentPincode) {
        this.permanentPincode = permanentPincode;
    }
    
    public Boolean getSameAsCurrent() {
        return sameAsCurrent;
    }
    
    public void setSameAsCurrent(Boolean sameAsCurrent) {
        this.sameAsCurrent = sameAsCurrent;
    }
    
    public EmploymentType getEmploymentType() {
        return employmentType;
    }
    
    public void setEmploymentType(EmploymentType employmentType) {
        this.employmentType = employmentType;
    }
    
    public BigDecimal getMonthlyIncome() {
        return monthlyIncome;
    }
    
    public void setMonthlyIncome(BigDecimal monthlyIncome) {
        this.monthlyIncome = monthlyIncome;
    }
    
    public String getEmployer() {
        return employer;
    }
    
    public void setEmployer(String employer) {
        this.employer = employer;
    }
    
    public Integer getWorkExperience() {
        return workExperience;
    }
    
    public void setWorkExperience(Integer workExperience) {
        this.workExperience = workExperience;
    }
    
    public BigDecimal getLoanAmount() {
        return loanAmount;
    }
    
    public void setLoanAmount(BigDecimal loanAmount) {
        this.loanAmount = loanAmount;
    }
    
    public LoanPurpose getLoanPurpose() {
        return loanPurpose;
    }
    
    public void setLoanPurpose(LoanPurpose loanPurpose) {
        this.loanPurpose = loanPurpose;
    }
    
    public Integer getLoanTenure() {
        return loanTenure;
    }
    
    public void setLoanTenure(Integer loanTenure) {
        this.loanTenure = loanTenure;
    }
    
    public ApplicationStatus getStatus() {
        return status;
    }
    
    public void setStatus(ApplicationStatus status) {
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
    
    public User getSubmittedBy() {
        return submittedBy;
    }
    
    public void setSubmittedBy(User submittedBy) {
        this.submittedBy = submittedBy;
    }
    
    // Co-Applicant Information
    @Column(name = "has_co_applicant")
    private Boolean hasCoApplicant = false;

    @Column(name = "co_applicant_first_name")
    private String coApplicantFirstName;

    @Column(name = "co_applicant_last_name")
    private String coApplicantLastName;

    @Column(name = "co_applicant_dob")
    private LocalDate coApplicantDateOfBirth;

    @Column(name = "co_applicant_pan")
    private String coApplicantPanNumber;

    @Column(name = "co_applicant_phone")
    private String coApplicantPhoneNumber;

    @Column(name = "co_applicant_email")
    private String coApplicantEmail;

    @Column(name = "co_applicant_income", precision = 12, scale = 2)
    private BigDecimal coApplicantMonthlyIncome;

    @Column(name = "co_applicant_employer")
    private String coApplicantEmployer;
    
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

    // Co-Applicant Getters and Setters
    public Boolean getHasCoApplicant() {
        return hasCoApplicant;
    }

    public void setHasCoApplicant(Boolean hasCoApplicant) {
        this.hasCoApplicant = hasCoApplicant;
    }

    public String getCoApplicantFirstName() {
        return coApplicantFirstName;
    }

    public void setCoApplicantFirstName(String coApplicantFirstName) {
        this.coApplicantFirstName = coApplicantFirstName;
    }

    public String getCoApplicantLastName() {
        return coApplicantLastName;
    }

    public void setCoApplicantLastName(String coApplicantLastName) {
        this.coApplicantLastName = coApplicantLastName;
    }

    public LocalDate getCoApplicantDateOfBirth() {
        return coApplicantDateOfBirth;
    }

    public void setCoApplicantDateOfBirth(LocalDate coApplicantDateOfBirth) {
        this.coApplicantDateOfBirth = coApplicantDateOfBirth;
    }

    public String getCoApplicantPanNumber() {
        return coApplicantPanNumber;
    }

    public void setCoApplicantPanNumber(String coApplicantPanNumber) {
        this.coApplicantPanNumber = coApplicantPanNumber;
    }

    public String getCoApplicantPhoneNumber() {
        return coApplicantPhoneNumber;
    }

    public void setCoApplicantPhoneNumber(String coApplicantPhoneNumber) {
        this.coApplicantPhoneNumber = coApplicantPhoneNumber;
    }

    public String getCoApplicantEmail() {
        return coApplicantEmail;
    }

    public void setCoApplicantEmail(String coApplicantEmail) {
        this.coApplicantEmail = coApplicantEmail;
    }

    public BigDecimal getCoApplicantMonthlyIncome() {
        return coApplicantMonthlyIncome;
    }

    public void setCoApplicantMonthlyIncome(BigDecimal coApplicantMonthlyIncome) {
        this.coApplicantMonthlyIncome = coApplicantMonthlyIncome;
    }

    public String getCoApplicantEmployer() {
        return coApplicantEmployer;
    }

    public void setCoApplicantEmployer(String coApplicantEmployer) {
        this.coApplicantEmployer = coApplicantEmployer;
    }

    // New Relationship Getters and Setters
    public RcpuReport getRcpuReport() {
        return rcpuReport;
    }

    public void setRcpuReport(RcpuReport rcpuReport) {
        this.rcpuReport = rcpuReport;
    }

    public List<ApprovalHistory> getApprovalHistory() {
        return approvalHistory;
    }

    public void setApprovalHistory(List<ApprovalHistory> approvalHistory) {
        this.approvalHistory = approvalHistory;
    }

    public LoanOfferLetter getLoanOfferLetter() {
        return loanOfferLetter;
    }

    public void setLoanOfferLetter(LoanOfferLetter loanOfferLetter) {
        this.loanOfferLetter = loanOfferLetter;
    }

    public List<EmiSchedule> getEmiSchedules() {
        return emiSchedules;
    }

    public void setEmiSchedules(List<EmiSchedule> emiSchedules) {
        this.emiSchedules = emiSchedules;
    }

    public List<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }
}
