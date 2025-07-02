package com.portfolio.LOC.controller;

import com.portfolio.LOC.dto.LoanApplicationRequest;
import com.portfolio.LOC.dto.LoanApplicationResponse;
import com.portfolio.LOC.entity.LoanApplication;
import com.portfolio.LOC.service.LoanApplicationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/loans")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
public class LoanApplicationController {
    
    @Autowired
    private LoanApplicationService loanApplicationService;
    
    @PostMapping("/submit")
    public ResponseEntity<?> submitLoanApplication(@Valid @RequestBody LoanApplicationRequest request) {
        try {
            // Get current authenticated user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String submittedByEmail = authentication.getName();
            
            LoanApplicationResponse response = loanApplicationService.submitApplication(request, submittedByEmail);
            
            Map<String, Object> result = new HashMap<>();
            result.put("message", "Loan application submitted successfully");
            result.put("applicationId", response.getApplicationId());
            result.put("eligibilityScore", response.getEligibilityScore());
            result.put("estimatedEmi", response.getEstimatedEmi());
            result.put("status", response.getStatus());
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @GetMapping
    public ResponseEntity<?> getAllApplications() {
        try {
            List<LoanApplicationResponse> applications = loanApplicationService.getAllApplications();
            return ResponseEntity.ok(applications);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<?> getApplicationsByStatus(@PathVariable String status) {
        try {
            LoanApplication.ApplicationStatus applicationStatus = LoanApplication.ApplicationStatus.valueOf(status.toUpperCase());
            List<LoanApplicationResponse> applications = loanApplicationService.getApplicationsByStatus(applicationStatus);
            return ResponseEntity.ok(applications);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Invalid status: " + status);
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getApplicationById(@PathVariable Long id) {
        try {
            LoanApplicationResponse application = loanApplicationService.getApplicationById(id);
            return ResponseEntity.ok(application);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @GetMapping("/application/{applicationId}")
    public ResponseEntity<?> getApplicationByApplicationId(@PathVariable String applicationId) {
        try {
            LoanApplicationResponse application = loanApplicationService.getApplicationByApplicationId(applicationId);
            return ResponseEntity.ok(application);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    // Endpoints for different user roles
    @GetMapping("/pending-rcpu")
    public ResponseEntity<?> getPendingRcpuApplications() {
        try {
            List<LoanApplicationResponse> applications = loanApplicationService.getApplicationsByStatus(
                LoanApplication.ApplicationStatus.PENDING_RCPU
            );
            return ResponseEntity.ok(applications);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @GetMapping("/pending-l1")
    public ResponseEntity<?> getPendingL1Applications() {
        try {
            List<LoanApplicationResponse> applications = loanApplicationService.getApplicationsByStatus(
                LoanApplication.ApplicationStatus.PENDING_L1
            );
            return ResponseEntity.ok(applications);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @GetMapping("/pending-l2")
    public ResponseEntity<?> getPendingL2Applications() {
        try {
            List<LoanApplicationResponse> applications = loanApplicationService.getApplicationsByStatus(
                LoanApplication.ApplicationStatus.PENDING_L2
            );
            return ResponseEntity.ok(applications);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @GetMapping("/pending-l3")
    public ResponseEntity<?> getPendingL3Applications() {
        try {
            List<LoanApplicationResponse> applications = loanApplicationService.getApplicationsByStatus(
                LoanApplication.ApplicationStatus.PENDING_L3
            );
            return ResponseEntity.ok(applications);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}
