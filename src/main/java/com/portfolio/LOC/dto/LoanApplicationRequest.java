package com.portfolio.LOC.dto;

import com.portfolio.LOC.entity.LoanApplication;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public class LoanApplicationRequest {
    
    @Valid
    private PersonalInfo personalInfo;
    
    @Valid
    private AddressInfo addressInfo;
    
    @Valid
    private FinancialInfo financialInfo;
    
    @Valid
    private CoApplicant coApplicant;
    
    // Nested classes for structured data
    public static class PersonalInfo {
        @NotBlank(message = "First name is required")
        private String firstName;
        
        @NotBlank(message = "Last name is required")
        private String lastName;
        
        @NotNull(message = "Date of birth is required")
        private LocalDate dateOfBirth;
        
        @NotNull(message = "Gender is required")
        private LoanApplication.Gender gender;
        
        @NotNull(message = "Marital status is required")
        private LoanApplication.MaritalStatus maritalStatus;
        
        @NotBlank(message = "PAN number is required")
        @Pattern(regexp = "[A-Z]{5}[0-9]{4}[A-Z]{1}", message = "Invalid PAN number format")
        private String panNumber;
        
        @NotBlank(message = "Aadhaar number is required")
        @Pattern(regexp = "\\d{12}", message = "Aadhaar number must be 12 digits")
        private String aadhaarNumber;
        
        @NotBlank(message = "Phone number is required")
        @Pattern(regexp = "\\d{10}", message = "Phone number must be 10 digits")
        private String phoneNumber;
        
        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        private String email;
        
        // Getters and Setters
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        
        public LocalDate getDateOfBirth() { return dateOfBirth; }
        public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }
        
        public LoanApplication.Gender getGender() { return gender; }
        public void setGender(LoanApplication.Gender gender) { this.gender = gender; }
        
        public LoanApplication.MaritalStatus getMaritalStatus() { return maritalStatus; }
        public void setMaritalStatus(LoanApplication.MaritalStatus maritalStatus) { this.maritalStatus = maritalStatus; }
        
        public String getPanNumber() { return panNumber; }
        public void setPanNumber(String panNumber) { this.panNumber = panNumber; }
        
        public String getAadhaarNumber() { return aadhaarNumber; }
        public void setAadhaarNumber(String aadhaarNumber) { this.aadhaarNumber = aadhaarNumber; }
        
        public String getPhoneNumber() { return phoneNumber; }
        public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }
    
    public static class AddressInfo {
        @NotBlank(message = "Current address is required")
        private String currentAddress;

        @NotBlank(message = "Current city is required")
        private String currentCity;

        @NotBlank(message = "Current state is required")
        private String currentState;

        @NotBlank(message = "Current pincode is required")
        @Pattern(regexp = "\\d{6}", message = "Pincode must be 6 digits")
        private String currentPincode;

        @NotNull(message = "Residence type is required")
        private LoanApplication.ResidenceType residenceType;

        private String permanentAddress;
        private String permanentCity;
        private String permanentState;
        private String permanentPincode;
        private Boolean sameAsCurrent = false;

        // Getters and Setters
        public String getCurrentAddress() { return currentAddress; }
        public void setCurrentAddress(String currentAddress) { this.currentAddress = currentAddress; }

        public String getCurrentCity() { return currentCity; }
        public void setCurrentCity(String currentCity) { this.currentCity = currentCity; }

        public String getCurrentState() { return currentState; }
        public void setCurrentState(String currentState) { this.currentState = currentState; }

        public String getCurrentPincode() { return currentPincode; }
        public void setCurrentPincode(String currentPincode) { this.currentPincode = currentPincode; }

        public LoanApplication.ResidenceType getResidenceType() { return residenceType; }
        public void setResidenceType(LoanApplication.ResidenceType residenceType) { this.residenceType = residenceType; }

        public String getPermanentAddress() { return permanentAddress; }
        public void setPermanentAddress(String permanentAddress) { this.permanentAddress = permanentAddress; }

        public String getPermanentCity() { return permanentCity; }
        public void setPermanentCity(String permanentCity) { this.permanentCity = permanentCity; }

        public String getPermanentState() { return permanentState; }
        public void setPermanentState(String permanentState) { this.permanentState = permanentState; }

        public String getPermanentPincode() { return permanentPincode; }
        public void setPermanentPincode(String permanentPincode) { this.permanentPincode = permanentPincode; }

        public Boolean getSameAsCurrent() { return sameAsCurrent; }
        public void setSameAsCurrent(Boolean sameAsCurrent) { this.sameAsCurrent = sameAsCurrent; }
    }

    public static class FinancialInfo {
        @NotNull(message = "Employment type is required")
        private LoanApplication.EmploymentType employmentType;

        @NotNull(message = "Monthly income is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "Monthly income must be positive")
        private BigDecimal monthlyIncome;

        @NotBlank(message = "Employer is required")
        private String employer;

        @NotNull(message = "Work experience is required")
        @Min(value = 0, message = "Work experience cannot be negative")
        private Integer workExperience;

        @NotNull(message = "Loan amount is required")
        @DecimalMin(value = "50000.0", message = "Minimum loan amount is 50,000")
        @DecimalMax(value = "10000000.0", message = "Maximum loan amount is 1,00,00,000")
        private BigDecimal loanAmount;

        @NotNull(message = "Loan purpose is required")
        private LoanApplication.LoanPurpose loanPurpose;

        @NotNull(message = "Loan tenure is required")
        @Min(value = 5, message = "Minimum loan tenure is 5 years")
        @Max(value = 30, message = "Maximum loan tenure is 30 years")
        private Integer loanTenure;

        // Getters and Setters
        public LoanApplication.EmploymentType getEmploymentType() { return employmentType; }
        public void setEmploymentType(LoanApplication.EmploymentType employmentType) { this.employmentType = employmentType; }

        public BigDecimal getMonthlyIncome() { return monthlyIncome; }
        public void setMonthlyIncome(BigDecimal monthlyIncome) { this.monthlyIncome = monthlyIncome; }

        public String getEmployer() { return employer; }
        public void setEmployer(String employer) { this.employer = employer; }

        public Integer getWorkExperience() { return workExperience; }
        public void setWorkExperience(Integer workExperience) { this.workExperience = workExperience; }

        public BigDecimal getLoanAmount() { return loanAmount; }
        public void setLoanAmount(BigDecimal loanAmount) { this.loanAmount = loanAmount; }

        public LoanApplication.LoanPurpose getLoanPurpose() { return loanPurpose; }
        public void setLoanPurpose(LoanApplication.LoanPurpose loanPurpose) { this.loanPurpose = loanPurpose; }

        public Integer getLoanTenure() { return loanTenure; }
        public void setLoanTenure(Integer loanTenure) { this.loanTenure = loanTenure; }
    }

    public static class CoApplicant {
        private Boolean hasCoApplicant = false;
        private String firstName;
        private String lastName;
        private LocalDate dateOfBirth;
        private String panNumber;
        private String phoneNumber;
        private String email;
        private BigDecimal monthlyIncome;
        private String employer;

        // Getters and Setters
        public Boolean getHasCoApplicant() { return hasCoApplicant; }
        public void setHasCoApplicant(Boolean hasCoApplicant) { this.hasCoApplicant = hasCoApplicant; }

        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }

        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }

        public LocalDate getDateOfBirth() { return dateOfBirth; }
        public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

        public String getPanNumber() { return panNumber; }
        public void setPanNumber(String panNumber) { this.panNumber = panNumber; }

        public String getPhoneNumber() { return phoneNumber; }
        public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public BigDecimal getMonthlyIncome() { return monthlyIncome; }
        public void setMonthlyIncome(BigDecimal monthlyIncome) { this.monthlyIncome = monthlyIncome; }

        public String getEmployer() { return employer; }
        public void setEmployer(String employer) { this.employer = employer; }
    }
    
    // Getters and Setters for main class
    public PersonalInfo getPersonalInfo() { return personalInfo; }
    public void setPersonalInfo(PersonalInfo personalInfo) { this.personalInfo = personalInfo; }
    
    public AddressInfo getAddressInfo() { return addressInfo; }
    public void setAddressInfo(AddressInfo addressInfo) { this.addressInfo = addressInfo; }
    
    public FinancialInfo getFinancialInfo() { return financialInfo; }
    public void setFinancialInfo(FinancialInfo financialInfo) { this.financialInfo = financialInfo; }
    
    public CoApplicant getCoApplicant() { return coApplicant; }
    public void setCoApplicant(CoApplicant coApplicant) { this.coApplicant = coApplicant; }
}
