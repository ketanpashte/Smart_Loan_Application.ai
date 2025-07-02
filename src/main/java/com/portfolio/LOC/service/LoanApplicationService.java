package com.portfolio.LOC.service;

import com.portfolio.LOC.dto.LoanApplicationRequest;
import com.portfolio.LOC.dto.LoanApplicationResponse;
import com.portfolio.LOC.entity.LoanApplication;
import com.portfolio.LOC.entity.User;
import com.portfolio.LOC.entity.Document;
import com.portfolio.LOC.repository.LoanApplicationRepository;
import com.portfolio.LOC.repository.UserRepository;
import com.portfolio.LOC.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LoanApplicationService {
    
    @Autowired
    private LoanApplicationRepository loanApplicationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DocumentRepository documentRepository;
    
    public LoanApplicationResponse submitApplication(LoanApplicationRequest request, String submittedByEmail) {
        // Get the user who is submitting the application
        User submittedBy = userRepository.findByEmail(submittedByEmail)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Check for duplicate PAN or Aadhaar
        if (loanApplicationRepository.existsByPanNumber(request.getPersonalInfo().getPanNumber())) {
            throw new RuntimeException("Application with this PAN number already exists");
        }
        
        if (loanApplicationRepository.existsByAadhaarNumber(request.getPersonalInfo().getAadhaarNumber())) {
            throw new RuntimeException("Application with this Aadhaar number already exists");
        }
        
        // Create new loan application
        LoanApplication loanApplication = new LoanApplication();
        
        // Map personal information
        loanApplication.setFirstName(request.getPersonalInfo().getFirstName());
        loanApplication.setLastName(request.getPersonalInfo().getLastName());
        loanApplication.setDateOfBirth(request.getPersonalInfo().getDateOfBirth());
        loanApplication.setGender(request.getPersonalInfo().getGender());
        loanApplication.setMaritalStatus(request.getPersonalInfo().getMaritalStatus());
        loanApplication.setPanNumber(request.getPersonalInfo().getPanNumber());
        loanApplication.setAadhaarNumber(request.getPersonalInfo().getAadhaarNumber());
        loanApplication.setPhoneNumber(request.getPersonalInfo().getPhoneNumber());
        loanApplication.setEmail(request.getPersonalInfo().getEmail());
        
        // Map address information
        loanApplication.setCurrentStreet(request.getAddressInfo().getCurrentAddress());
        loanApplication.setCurrentCity(request.getAddressInfo().getCurrentCity());
        loanApplication.setCurrentState(request.getAddressInfo().getCurrentState());
        loanApplication.setCurrentPincode(request.getAddressInfo().getCurrentPincode());
        loanApplication.setResidenceType(request.getAddressInfo().getResidenceType());

        if (request.getAddressInfo().getSameAsCurrent()) {
            loanApplication.setPermanentStreet(request.getAddressInfo().getCurrentAddress());
            loanApplication.setPermanentCity(request.getAddressInfo().getCurrentCity());
            loanApplication.setPermanentState(request.getAddressInfo().getCurrentState());
            loanApplication.setPermanentPincode(request.getAddressInfo().getCurrentPincode());
        } else {
            loanApplication.setPermanentStreet(request.getAddressInfo().getPermanentAddress());
            loanApplication.setPermanentCity(request.getAddressInfo().getPermanentCity());
            loanApplication.setPermanentState(request.getAddressInfo().getPermanentState());
            loanApplication.setPermanentPincode(request.getAddressInfo().getPermanentPincode());
        }
        loanApplication.setSameAsCurrent(request.getAddressInfo().getSameAsCurrent());
        
        // Map financial information
        loanApplication.setEmploymentType(request.getFinancialInfo().getEmploymentType());
        loanApplication.setMonthlyIncome(request.getFinancialInfo().getMonthlyIncome());
        loanApplication.setEmployer(request.getFinancialInfo().getEmployer());
        loanApplication.setWorkExperience(request.getFinancialInfo().getWorkExperience());
        loanApplication.setLoanAmount(request.getFinancialInfo().getLoanAmount());
        loanApplication.setLoanPurpose(request.getFinancialInfo().getLoanPurpose());
        loanApplication.setLoanTenure(request.getFinancialInfo().getLoanTenure());
        
        // Map co-applicant information
        loanApplication.setHasCoApplicant(request.getCoApplicant().getHasCoApplicant());
        if (request.getCoApplicant().getHasCoApplicant()) {
            loanApplication.setCoApplicantFirstName(request.getCoApplicant().getFirstName());
            loanApplication.setCoApplicantLastName(request.getCoApplicant().getLastName());
            loanApplication.setCoApplicantDateOfBirth(request.getCoApplicant().getDateOfBirth());
            loanApplication.setCoApplicantPanNumber(request.getCoApplicant().getPanNumber());
            loanApplication.setCoApplicantPhoneNumber(request.getCoApplicant().getPhoneNumber());
            loanApplication.setCoApplicantEmail(request.getCoApplicant().getEmail());
            loanApplication.setCoApplicantMonthlyIncome(request.getCoApplicant().getMonthlyIncome());
            loanApplication.setCoApplicantEmployer(request.getCoApplicant().getEmployer());
        }

        // Calculate estimated EMI
        BigDecimal estimatedEmi = calculateEMI(
            request.getFinancialInfo().getLoanAmount(),
            request.getFinancialInfo().getLoanTenure()
        );
        loanApplication.setEstimatedEmi(estimatedEmi);

        // Calculate basic eligibility score
        Integer eligibilityScore = calculateEligibilityScore(request);
        loanApplication.setEligibilityScore(eligibilityScore);

        // Set application metadata
        loanApplication.setSubmittedBy(submittedBy);
        loanApplication.setStatus(LoanApplication.ApplicationStatus.SUBMITTED);
        loanApplication.setCreatedAt(LocalDateTime.now());
        
        // Save the application
        LoanApplication savedApplication = loanApplicationRepository.save(loanApplication);
        
        return new LoanApplicationResponse(savedApplication);
    }
    
    private BigDecimal calculateEMI(BigDecimal loanAmount, Integer tenureYears) {
        // Simple EMI calculation with 8.5% annual interest rate
        double principal = loanAmount.doubleValue();
        double annualRate = 0.085; // 8.5%
        double monthlyRate = annualRate / 12;
        int tenureMonths = tenureYears * 12;
        
        double emi = (principal * monthlyRate * Math.pow(1 + monthlyRate, tenureMonths)) /
                     (Math.pow(1 + monthlyRate, tenureMonths) - 1);
        
        return BigDecimal.valueOf(emi).setScale(2, RoundingMode.HALF_UP);
    }
    
    private Integer calculateEligibilityScore(LoanApplicationRequest request) {
        int score = 0;
        
        // Income factor (40% weightage)
        BigDecimal monthlyIncome = request.getFinancialInfo().getMonthlyIncome();
        if (monthlyIncome.compareTo(BigDecimal.valueOf(100000)) >= 0) {
            score += 40;
        } else if (monthlyIncome.compareTo(BigDecimal.valueOf(50000)) >= 0) {
            score += 30;
        } else if (monthlyIncome.compareTo(BigDecimal.valueOf(30000)) >= 0) {
            score += 20;
        } else {
            score += 10;
        }
        
        // Employment type factor (20% weightage)
        if (request.getFinancialInfo().getEmploymentType() == LoanApplication.EmploymentType.SALARIED) {
            score += 20;
        } else {
            score += 15;
        }
        
        // Work experience factor (20% weightage)
        Integer workExp = request.getFinancialInfo().getWorkExperience();
        if (workExp >= 5) {
            score += 20;
        } else if (workExp >= 2) {
            score += 15;
        } else {
            score += 10;
        }
        
        // Co-applicant factor (10% weightage)
        if (request.getCoApplicant().getHasCoApplicant()) {
            score += 10;
        } else {
            score += 5;
        }
        
        // Loan to income ratio factor (10% weightage)
        BigDecimal loanAmount = request.getFinancialInfo().getLoanAmount();
        BigDecimal annualIncome = monthlyIncome.multiply(BigDecimal.valueOf(12));
        BigDecimal loanToIncomeRatio = loanAmount.divide(annualIncome, 2, RoundingMode.HALF_UP);
        
        if (loanToIncomeRatio.compareTo(BigDecimal.valueOf(5)) <= 0) {
            score += 10;
        } else if (loanToIncomeRatio.compareTo(BigDecimal.valueOf(8)) <= 0) {
            score += 7;
        } else {
            score += 3;
        }
        
        return Math.min(score, 100); // Cap at 100
    }
    
    public List<LoanApplicationResponse> getAllApplications() {
        return loanApplicationRepository.findAll()
            .stream()
            .map(LoanApplicationResponse::new)
            .collect(Collectors.toList());
    }
    
    public List<LoanApplicationResponse> getApplicationsByStatus(LoanApplication.ApplicationStatus status) {
        return loanApplicationRepository.findByStatusOrderByCreatedAtAsc(status)
            .stream()
            .map(LoanApplicationResponse::new)
            .collect(Collectors.toList());
    }
    
    public LoanApplicationResponse getApplicationById(Long id) {
        LoanApplication application = loanApplicationRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Application not found"));
        return new LoanApplicationResponse(application);
    }
    
    public LoanApplicationResponse getApplicationByApplicationId(String applicationId) {
        LoanApplication application = loanApplicationRepository.findByApplicationId(applicationId)
            .orElseThrow(() -> new RuntimeException("Application not found"));
        return new LoanApplicationResponse(application);
    }
}
