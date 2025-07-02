package com.portfolio.LOC.service;

import com.portfolio.LOC.dto.LoanApplicationResponse;
import com.portfolio.LOC.entity.LoanApplication;
import com.portfolio.LOC.entity.User;
import com.portfolio.LOC.repository.LoanApplicationRepository;
import com.portfolio.LOC.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LoanWorkflowService {
    
    @Autowired
    private LoanApplicationRepository loanApplicationRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    // RCPU Actions
    public LoanApplicationResponse processRcpuDecision(Long applicationId, String decision, String userEmail) {
        LoanApplication application = loanApplicationRepository.findById(applicationId)
            .orElseThrow(() -> new RuntimeException("Application not found"));
        
        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (!user.getRole().equals(User.Role.UNDERWRITER)) {
            throw new RuntimeException("User not authorized for RCPU actions");
        }
        
        if (!application.getStatus().equals(LoanApplication.ApplicationStatus.SUBMITTED)) {
            throw new RuntimeException("Application is not in SUBMITTED status");
        }
        
        if ("approve".equals(decision)) {
            application.setStatus(LoanApplication.ApplicationStatus.PENDING_L1);
        } else if ("reject".equals(decision)) {
            application.setStatus(LoanApplication.ApplicationStatus.REJECTED);
        } else {
            throw new RuntimeException("Invalid decision. Must be 'approve' or 'reject'");
        }
        
        application.setUpdatedAt(LocalDateTime.now());
        LoanApplication savedApplication = loanApplicationRepository.save(application);
        
        return new LoanApplicationResponse(savedApplication);
    }
    
    // L1 Manager Actions
    public LoanApplicationResponse processL1Decision(Long applicationId, String decision, String userEmail) {
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
        
        if ("approve".equals(decision)) {
            // Check loan amount to determine next step
            if (application.getLoanAmount().compareTo(java.math.BigDecimal.valueOf(1000000)) <= 0) {
                // Loan amount <= 10 lakhs, L1 can approve directly
                application.setStatus(LoanApplication.ApplicationStatus.L1_APPROVED);
            } else {
                // Loan amount > 10 lakhs, forward to L2
                application.setStatus(LoanApplication.ApplicationStatus.PENDING_L2);
            }
        } else if ("reject".equals(decision)) {
            application.setStatus(LoanApplication.ApplicationStatus.REJECTED);
        } else {
            throw new RuntimeException("Invalid decision. Must be 'approve' or 'reject'");
        }
        
        application.setUpdatedAt(LocalDateTime.now());
        LoanApplication savedApplication = loanApplicationRepository.save(application);
        
        return new LoanApplicationResponse(savedApplication);
    }
    
    // L2 Manager Actions
    public LoanApplicationResponse processL2Decision(Long applicationId, String decision, String userEmail) {
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
        
        if ("approve".equals(decision)) {
            // Check loan amount to determine next step
            if (application.getLoanAmount().compareTo(java.math.BigDecimal.valueOf(5000000)) <= 0) {
                // Loan amount <= 50 lakhs, L2 can approve directly
                application.setStatus(LoanApplication.ApplicationStatus.L2_APPROVED);
            } else {
                // Loan amount > 50 lakhs, forward to L3
                application.setStatus(LoanApplication.ApplicationStatus.PENDING_L3);
            }
        } else if ("reject".equals(decision)) {
            application.setStatus(LoanApplication.ApplicationStatus.REJECTED);
        } else {
            throw new RuntimeException("Invalid decision. Must be 'approve' or 'reject'");
        }
        
        application.setUpdatedAt(LocalDateTime.now());
        LoanApplication savedApplication = loanApplicationRepository.save(application);
        
        return new LoanApplicationResponse(savedApplication);
    }
    
    // L3 Manager Actions (Admin)
    public LoanApplicationResponse processL3Decision(Long applicationId, String decision, String userEmail) {
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
        
        if ("approve".equals(decision)) {
            application.setStatus(LoanApplication.ApplicationStatus.L3_APPROVED);
        } else if ("reject".equals(decision)) {
            application.setStatus(LoanApplication.ApplicationStatus.REJECTED);
        } else {
            throw new RuntimeException("Invalid decision. Must be 'approve' or 'reject'");
        }
        
        application.setUpdatedAt(LocalDateTime.now());
        LoanApplication savedApplication = loanApplicationRepository.save(application);
        
        return new LoanApplicationResponse(savedApplication);
    }
    
    // Get applications by status for different roles
    public List<LoanApplicationResponse> getApplicationsForRcpu() {
        return loanApplicationRepository.findByStatusOrderByCreatedAtAsc(LoanApplication.ApplicationStatus.SUBMITTED)
            .stream()
            .map(LoanApplicationResponse::new)
            .collect(Collectors.toList());
    }
    
    public List<LoanApplicationResponse> getApplicationsForL1() {
        return loanApplicationRepository.findByStatusOrderByCreatedAtAsc(LoanApplication.ApplicationStatus.PENDING_L1)
            .stream()
            .map(LoanApplicationResponse::new)
            .collect(Collectors.toList());
    }
    
    public List<LoanApplicationResponse> getApplicationsForL2() {
        return loanApplicationRepository.findByStatusOrderByCreatedAtAsc(LoanApplication.ApplicationStatus.PENDING_L2)
            .stream()
            .map(LoanApplicationResponse::new)
            .collect(Collectors.toList());
    }
    
    public List<LoanApplicationResponse> getApplicationsForL3() {
        return loanApplicationRepository.findByStatusOrderByCreatedAtAsc(LoanApplication.ApplicationStatus.PENDING_L3)
            .stream()
            .map(LoanApplicationResponse::new)
            .collect(Collectors.toList());
    }

    // Test method to force application to L3 status
    public void forceApplicationToL3(Long applicationId) {
        LoanApplication application = loanApplicationRepository.findById(applicationId)
            .orElseThrow(() -> new RuntimeException("Application not found"));

        application.setStatus(LoanApplication.ApplicationStatus.PENDING_L3);
        application.setUpdatedAt(LocalDateTime.now());
        loanApplicationRepository.save(application);
    }
    
    // Get application details
    public LoanApplicationResponse getApplicationDetails(Long applicationId) {
        LoanApplication application = loanApplicationRepository.findById(applicationId)
            .orElseThrow(() -> new RuntimeException("Application not found"));
        return new LoanApplicationResponse(application);
    }

    public List<LoanApplicationResponse> getAllApplications() {
        return loanApplicationRepository.findAll()
            .stream()
            .map(LoanApplicationResponse::new)
            .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
            .collect(Collectors.toList());
    }
}
