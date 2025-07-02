package com.portfolio.LOC.controller;

import com.portfolio.LOC.dto.LoanApplicationResponse;
import com.portfolio.LOC.service.LoanWorkflowService;
import com.portfolio.LOC.service.ApprovalService;
import com.portfolio.LOC.entity.ApprovalHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/workflow")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
public class LoanWorkflowController {
    
    @Autowired
    private LoanWorkflowService loanWorkflowService;

    @Autowired
    private ApprovalService approvalService;
    
    // RCPU Endpoints
    @GetMapping("/rcpu/applications")
    public ResponseEntity<?> getRcpuApplications() {
        try {
            List<LoanApplicationResponse> applications = loanWorkflowService.getApplicationsForRcpu();
            return ResponseEntity.ok(applications);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @PostMapping("/rcpu/{applicationId}/decision")
    public ResponseEntity<?> processRcpuDecision(
            @PathVariable Long applicationId,
            @RequestBody Map<String, String> request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userEmail = authentication.getName();
            
            String decision = request.get("decision");
            LoanApplicationResponse response = loanWorkflowService.processRcpuDecision(applicationId, decision, userEmail);
            
            Map<String, Object> result = new HashMap<>();
            result.put("message", "Application " + decision + "d successfully");
            result.put("application", response);
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    // L1 Manager Endpoints
    @GetMapping("/l1/applications")
    public ResponseEntity<?> getL1Applications() {
        try {
            List<LoanApplicationResponse> applications = approvalService.getApplicationsForL1();
            return ResponseEntity.ok(applications);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/l1/{applicationId}/decision")
    public ResponseEntity<?> processL1Decision(
            @PathVariable Long applicationId,
            @RequestBody Map<String, String> request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userEmail = authentication.getName();

            String decision = request.get("decision");
            String remarks = request.get("remarks");
            LoanApplicationResponse response = approvalService.processL1Decision(applicationId, decision, remarks, userEmail);

            Map<String, Object> result = new HashMap<>();
            result.put("message", "Application " + decision + "d successfully");
            result.put("application", response);

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    // L2 Manager Endpoints
    @GetMapping("/l2/applications")
    public ResponseEntity<?> getL2Applications() {
        try {
            List<LoanApplicationResponse> applications = approvalService.getApplicationsForL2();
            return ResponseEntity.ok(applications);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/l2/{applicationId}/decision")
    public ResponseEntity<?> processL2Decision(
            @PathVariable Long applicationId,
            @RequestBody Map<String, String> request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userEmail = authentication.getName();

            String decision = request.get("decision");
            String remarks = request.get("remarks");
            LoanApplicationResponse response = approvalService.processL2Decision(applicationId, decision, remarks, userEmail);

            Map<String, Object> result = new HashMap<>();
            result.put("message", "Application " + decision + "d successfully");
            result.put("application", response);

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    // L3 Manager (Admin) Endpoints
    @GetMapping("/l3/applications")
    public ResponseEntity<?> getL3Applications() {
        try {
            List<LoanApplicationResponse> applications = approvalService.getApplicationsForL3();
            return ResponseEntity.ok(applications);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Admin endpoint to get all applications (for admin dashboard)
    @GetMapping("/admin/all-applications")
    public ResponseEntity<?> getAllApplicationsForAdmin() {
        try {
            List<LoanApplicationResponse> applications = loanWorkflowService.getAllApplications();
            return ResponseEntity.ok(applications);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/l3/{applicationId}/decision")
    public ResponseEntity<?> processL3Decision(
            @PathVariable Long applicationId,
            @RequestBody Map<String, String> request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userEmail = authentication.getName();

            String decision = request.get("decision");
            String remarks = request.get("remarks");
            LoanApplicationResponse response = approvalService.processL3Decision(applicationId, decision, remarks, userEmail);

            Map<String, Object> result = new HashMap<>();
            result.put("message", "Application " + decision + "d successfully");
            result.put("application", response);

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    // Common endpoint to get application details
    @GetMapping("/application/{applicationId}")
    public ResponseEntity<?> getApplicationDetails(@PathVariable Long applicationId) {
        try {
            LoanApplicationResponse application = approvalService.getApplicationDetails(applicationId);
            return ResponseEntity.ok(application);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Get approval history for an application
    @GetMapping("/application/{applicationId}/history")
    public ResponseEntity<?> getApprovalHistory(@PathVariable Long applicationId) {
        try {
            List<ApprovalHistory> history = approvalService.getApprovalHistory(applicationId);
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Test endpoint to auto-approve RCPU for demo purposes
    @PostMapping("/test/auto-approve-rcpu")
    public ResponseEntity<?> autoApproveRcpu() {
        try {
            List<LoanApplicationResponse> applications = loanWorkflowService.getApplicationsForRcpu();
            int processedCount = 0;

            for (LoanApplicationResponse app : applications) {
                try {
                    // Auto-approve with RCPU user
                    loanWorkflowService.processRcpuDecision(app.getId(), "approve", "rcpu@smartloan.com");
                    processedCount++;
                } catch (Exception e) {
                    System.err.println("Failed to process application " + app.getId() + ": " + e.getMessage());
                }
            }

            Map<String, Object> result = new HashMap<>();
            result.put("message", "Auto-approved " + processedCount + " applications through RCPU");
            result.put("processedCount", processedCount);

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Test endpoint to check application statuses
    @GetMapping("/test/application-status")
    public ResponseEntity<?> checkApplicationStatus() {
        try {
            List<LoanApplicationResponse> allApplications = loanWorkflowService.getAllApplications();

            Map<String, Object> result = new HashMap<>();
            result.put("totalApplications", allApplications.size());
            result.put("applications", allApplications);

            // Count by status
            Map<String, Long> statusCounts = new HashMap<>();
            for (LoanApplicationResponse app : allApplications) {
                String status = app.getStatus().toString();
                statusCounts.put(status, statusCounts.getOrDefault(status, 0L) + 1);
            }
            result.put("statusCounts", statusCounts);

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Test endpoint to force applications to L3 status
    @PostMapping("/test/force-l3-status")
    public ResponseEntity<?> forceL3Status() {
        try {
            List<LoanApplicationResponse> allApplications = loanWorkflowService.getAllApplications();
            int updatedCount = 0;

            for (LoanApplicationResponse app : allApplications) {
                // Force applications with high loan amounts to L3 status
                if (app.getLoanAmount().compareTo(java.math.BigDecimal.valueOf(5000000)) > 0) {
                    try {
                        loanWorkflowService.forceApplicationToL3(app.getId());
                        updatedCount++;
                    } catch (Exception e) {
                        System.err.println("Failed to update application " + app.getId() + ": " + e.getMessage());
                    }
                }
            }

            Map<String, Object> result = new HashMap<>();
            result.put("message", "Forced " + updatedCount + " applications to L3 status");
            result.put("updatedCount", updatedCount);

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}
