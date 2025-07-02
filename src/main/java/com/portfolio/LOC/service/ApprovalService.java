package com.portfolio.LOC.service;

import com.portfolio.LOC.entity.*;
import com.portfolio.LOC.repository.*;
import com.portfolio.LOC.dto.LoanApplicationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ApprovalService {
    
    @Autowired
    private LoanApplicationRepository loanApplicationRepository;
    
    @Autowired
    private ApprovalHistoryRepository approvalHistoryRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RcpuReportRepository rcpuReportRepository;
    
    // L1 Manager Methods
    public List<LoanApplicationResponse> getApplicationsForL1() {
        return loanApplicationRepository.findByStatusOrderByCreatedAtAsc(LoanApplication.ApplicationStatus.PENDING_L1)
            .stream()
            .map(LoanApplicationResponse::new)
            .collect(Collectors.toList());
    }
    
    public LoanApplicationResponse processL1Decision(Long applicationId, String decision, String remarks, String userEmail) {
        LoanApplication application = loanApplicationRepository.findById(applicationId)
            .orElseThrow(() -> new RuntimeException("Application not found"));
        
        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (!user.getRole().equals(User.Role.MANAGER_L1)) {
            throw new RuntimeException("User not authorized for L1 actions");
        }
        
        if (!application.getStatus().equals(LoanApplication.ApplicationStatus.PENDING_L1)) {
            throw new RuntimeException("Application is not in PENDING_L1 status");
        }
        
        ApprovalHistory.Decision historyDecision;
        
        if ("approve".equals(decision)) {
            // Check loan amount to determine next step
            if (application.getLoanAmount().compareTo(BigDecimal.valueOf(1000000)) <= 0) {
                // Loan amount <= 10 lakhs, L1 can approve directly
                application.setStatus(LoanApplication.ApplicationStatus.L1_APPROVED);
                historyDecision = ApprovalHistory.Decision.APPROVED;
            } else {
                // Loan amount > 10 lakhs, forward to L2
                application.setStatus(LoanApplication.ApplicationStatus.PENDING_L2);
                historyDecision = ApprovalHistory.Decision.FORWARDED;
            }
        } else if ("reject".equals(decision)) {
            application.setStatus(LoanApplication.ApplicationStatus.REJECTED);
            historyDecision = ApprovalHistory.Decision.REJECTED;
        } else {
            throw new RuntimeException("Invalid decision. Must be 'approve' or 'reject'");
        }
        
        application.setUpdatedAt(LocalDateTime.now());
        LoanApplication savedApplication = loanApplicationRepository.save(application);
        
        // Create approval history entry
        ApprovalHistory history = new ApprovalHistory(
            application, user, ApprovalHistory.Stage.L1, historyDecision, remarks
        );
        approvalHistoryRepository.save(history);
        
        return new LoanApplicationResponse(savedApplication);
    }
    
    // L2 Manager Methods
    public List<LoanApplicationResponse> getApplicationsForL2() {
        return loanApplicationRepository.findByStatusOrderByCreatedAtAsc(LoanApplication.ApplicationStatus.PENDING_L2)
            .stream()
            .map(LoanApplicationResponse::new)
            .collect(Collectors.toList());
    }
    
    public LoanApplicationResponse processL2Decision(Long applicationId, String decision, String remarks, String userEmail) {
        LoanApplication application = loanApplicationRepository.findById(applicationId)
            .orElseThrow(() -> new RuntimeException("Application not found"));
        
        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (!user.getRole().equals(User.Role.MANAGER_L2)) {
            throw new RuntimeException("User not authorized for L2 actions");
        }
        
        if (!application.getStatus().equals(LoanApplication.ApplicationStatus.PENDING_L2)) {
            throw new RuntimeException("Application is not in PENDING_L2 status");
        }
        
        ApprovalHistory.Decision historyDecision;
        
        if ("approve".equals(decision)) {
            // Check loan amount to determine next step
            if (application.getLoanAmount().compareTo(BigDecimal.valueOf(5000000)) <= 0) {
                // Loan amount <= 50 lakhs, L2 can approve directly
                application.setStatus(LoanApplication.ApplicationStatus.L2_APPROVED);
                historyDecision = ApprovalHistory.Decision.APPROVED;
            } else {
                // Loan amount > 50 lakhs, forward to L3
                application.setStatus(LoanApplication.ApplicationStatus.PENDING_L3);
                historyDecision = ApprovalHistory.Decision.FORWARDED;
            }
        } else if ("reject".equals(decision)) {
            application.setStatus(LoanApplication.ApplicationStatus.REJECTED);
            historyDecision = ApprovalHistory.Decision.REJECTED;
        } else {
            throw new RuntimeException("Invalid decision. Must be 'approve' or 'reject'");
        }
        
        application.setUpdatedAt(LocalDateTime.now());
        LoanApplication savedApplication = loanApplicationRepository.save(application);
        
        // Create approval history entry
        ApprovalHistory history = new ApprovalHistory(
            application, user, ApprovalHistory.Stage.L2, historyDecision, remarks
        );
        approvalHistoryRepository.save(history);
        
        return new LoanApplicationResponse(savedApplication);
    }
    
    // L3 Admin Methods
    public List<LoanApplicationResponse> getApplicationsForL3() {
        return loanApplicationRepository.findByStatusOrderByCreatedAtAsc(LoanApplication.ApplicationStatus.PENDING_L3)
            .stream()
            .map(LoanApplicationResponse::new)
            .collect(Collectors.toList());
    }
    
    public LoanApplicationResponse processL3Decision(Long applicationId, String decision, String remarks, String userEmail) {
        LoanApplication application = loanApplicationRepository.findById(applicationId)
            .orElseThrow(() -> new RuntimeException("Application not found"));
        
        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (!user.getRole().equals(User.Role.ADMIN)) {
            throw new RuntimeException("User not authorized for L3 actions");
        }
        
        if (!application.getStatus().equals(LoanApplication.ApplicationStatus.PENDING_L3)) {
            throw new RuntimeException("Application is not in PENDING_L3 status");
        }
        
        ApprovalHistory.Decision historyDecision;
        
        if ("approve".equals(decision)) {
            application.setStatus(LoanApplication.ApplicationStatus.L3_APPROVED);
            historyDecision = ApprovalHistory.Decision.APPROVED;
        } else if ("reject".equals(decision)) {
            application.setStatus(LoanApplication.ApplicationStatus.REJECTED);
            historyDecision = ApprovalHistory.Decision.REJECTED;
        } else {
            throw new RuntimeException("Invalid decision. Must be 'approve' or 'reject'");
        }
        
        application.setUpdatedAt(LocalDateTime.now());
        LoanApplication savedApplication = loanApplicationRepository.save(application);
        
        // Create approval history entry
        ApprovalHistory history = new ApprovalHistory(
            application, user, ApprovalHistory.Stage.L3, historyDecision, remarks
        );
        approvalHistoryRepository.save(history);
        
        return new LoanApplicationResponse(savedApplication);
    }
    
    // Common Methods
    public LoanApplicationResponse getApplicationDetails(Long applicationId) {
        LoanApplication application = loanApplicationRepository.findById(applicationId)
            .orElseThrow(() -> new RuntimeException("Application not found"));

        // Ensure approval history is loaded
        List<ApprovalHistory> history = approvalHistoryRepository.findByLoanApplicationIdOrderByCreatedAtAsc(applicationId);
        application.setApprovalHistory(history);

        return new LoanApplicationResponse(application);
    }
    
    public List<ApprovalHistory> getApprovalHistory(Long applicationId) {
        return approvalHistoryRepository.findByLoanApplicationIdOrderByCreatedAtAsc(applicationId);
    }
}
