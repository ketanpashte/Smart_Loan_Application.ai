package com.portfolio.LOC.service;

import com.portfolio.LOC.entity.*;
import com.portfolio.LOC.repository.*;
import com.portfolio.LOC.dto.LoanApplicationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class RcpuService {
    
    @Autowired
    private LoanApplicationRepository loanApplicationRepository;
    
    @Autowired
    private RcpuReportRepository rcpuReportRepository;
    
    @Autowired
    private ApprovalHistoryRepository approvalHistoryRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    private final String UPLOAD_DIR = "uploads/rcpu-reports/";
    
    public List<LoanApplicationResponse> getApplicationsForRcpu() {
        return loanApplicationRepository.findByStatusOrderByCreatedAtAsc(LoanApplication.ApplicationStatus.SUBMITTED)
            .stream()
            .map(LoanApplicationResponse::new)
            .collect(Collectors.toList());
    }
    
    public LoanApplicationResponse getApplicationDetails(Long applicationId) {
        LoanApplication application = loanApplicationRepository.findById(applicationId)
            .orElseThrow(() -> new RuntimeException("Application not found"));

        // Ensure approval history is loaded
        List<ApprovalHistory> history = approvalHistoryRepository.findByLoanApplicationIdOrderByCreatedAtAsc(applicationId);
        application.setApprovalHistory(history);

        return new LoanApplicationResponse(application);
    }
    
    public RcpuReport uploadRcpuReport(Long applicationId, MultipartFile reportFile, 
                                     String remarks, Integer creditScore, 
                                     RcpuReport.Recommendation recommendation, 
                                     String userEmail) {
        
        // Validate application
        LoanApplication application = loanApplicationRepository.findById(applicationId)
            .orElseThrow(() -> new RuntimeException("Application not found"));
        
        if (!application.getStatus().equals(LoanApplication.ApplicationStatus.SUBMITTED)) {
            throw new RuntimeException("Application is not in SUBMITTED status");
        }
        
        // Validate user
        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (!user.getRole().equals(User.Role.UNDERWRITER)) {
            throw new RuntimeException("User not authorized for RCPU actions");
        }
        
        // Check if report already exists
        if (rcpuReportRepository.existsByLoanApplication(application)) {
            throw new RuntimeException("RCPU report already exists for this application");
        }
        
        try {
            // Create upload directory if it doesn't exist
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            // Generate unique filename
            String originalFilename = reportFile.getOriginalFilename();
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String fileName = "RCPU_" + application.getApplicationId() + "_" + System.currentTimeMillis() + fileExtension;
            Path filePath = uploadPath.resolve(fileName);
            
            // Save file
            Files.copy(reportFile.getInputStream(), filePath);
            
            // Create RCPU report entity
            RcpuReport rcpuReport = new RcpuReport();
            rcpuReport.setLoanApplication(application);
            rcpuReport.setReviewedBy(user);
            rcpuReport.setReportFileName(fileName);
            rcpuReport.setReportFilePath(filePath.toString());
            rcpuReport.setRemarks(remarks);
            rcpuReport.setCreditScore(creditScore);
            rcpuReport.setRecommendation(recommendation);
            
            // Save RCPU report
            rcpuReport = rcpuReportRepository.save(rcpuReport);
            
            // Update application status
            application.setStatus(LoanApplication.ApplicationStatus.PENDING_RCPU);
            application.setUpdatedAt(LocalDateTime.now());
            loanApplicationRepository.save(application);
            
            // Create approval history entry
            ApprovalHistory history = new ApprovalHistory(
                application, user, ApprovalHistory.Stage.RCPU, 
                ApprovalHistory.Decision.FORWARDED, remarks
            );
            approvalHistoryRepository.save(history);
            
            return rcpuReport;
            
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file: " + e.getMessage());
        }
    }
    
    public LoanApplicationResponse processRcpuDecision(Long applicationId, String decision, String userEmail) {
        LoanApplication application = loanApplicationRepository.findById(applicationId)
            .orElseThrow(() -> new RuntimeException("Application not found"));
        
        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (!user.getRole().equals(User.Role.UNDERWRITER)) {
            throw new RuntimeException("User not authorized for RCPU actions");
        }
        
        if (!application.getStatus().equals(LoanApplication.ApplicationStatus.PENDING_RCPU)) {
            throw new RuntimeException("Application is not in PENDING_RCPU status");
        }
        
        // Check if RCPU report exists
        RcpuReport rcpuReport = rcpuReportRepository.findByLoanApplication(application)
            .orElseThrow(() -> new RuntimeException("RCPU report not found. Please upload report first."));
        
        ApprovalHistory.Decision historyDecision;
        
        if ("approve".equals(decision)) {
            application.setStatus(LoanApplication.ApplicationStatus.PENDING_L1);
            historyDecision = ApprovalHistory.Decision.APPROVED;
        } else if ("reject".equals(decision)) {
            application.setStatus(LoanApplication.ApplicationStatus.REJECTED);
            historyDecision = ApprovalHistory.Decision.REJECTED;
        } else {
            throw new RuntimeException("Invalid decision. Must be 'approve' or 'reject'");
        }
        
        application.setUpdatedAt(LocalDateTime.now());
        LoanApplication savedApplication = loanApplicationRepository.save(application);
        
        // Update approval history
        ApprovalHistory history = new ApprovalHistory(
            application, user, ApprovalHistory.Stage.RCPU, 
            historyDecision, "RCPU " + decision + "d based on report analysis"
        );
        approvalHistoryRepository.save(history);
        
        return new LoanApplicationResponse(savedApplication);
    }
    
    public RcpuReport getRcpuReport(Long applicationId) {
        LoanApplication application = loanApplicationRepository.findById(applicationId)
            .orElseThrow(() -> new RuntimeException("Application not found"));
        
        return rcpuReportRepository.findByLoanApplication(application)
            .orElse(null);
    }
}
