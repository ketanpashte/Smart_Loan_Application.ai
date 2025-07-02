package com.portfolio.LOC.controller;

import com.portfolio.LOC.entity.RcpuReport;
import com.portfolio.LOC.service.RcpuService;
import com.portfolio.LOC.dto.LoanApplicationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rcpu")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
public class RcpuController {
    
    @Autowired
    private RcpuService rcpuService;
    
    @GetMapping("/applications")
    public ResponseEntity<?> getRcpuApplications() {
        try {
            List<LoanApplicationResponse> applications = rcpuService.getApplicationsForRcpu();
            return ResponseEntity.ok(applications);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @GetMapping("/applications/{applicationId}")
    public ResponseEntity<?> getApplicationDetails(@PathVariable Long applicationId) {
        try {
            LoanApplicationResponse application = rcpuService.getApplicationDetails(applicationId);
            return ResponseEntity.ok(application);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @PostMapping("/applications/{applicationId}/upload-report")
    public ResponseEntity<?> uploadRcpuReport(
            @PathVariable Long applicationId,
            @RequestParam("reportFile") MultipartFile reportFile,
            @RequestParam("remarks") String remarks,
            @RequestParam("creditScore") Integer creditScore,
            @RequestParam("recommendation") String recommendation) {
        
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userEmail = authentication.getName();
            
            RcpuReport.Recommendation rcpuRecommendation = RcpuReport.Recommendation.valueOf(recommendation.toUpperCase());
            
            RcpuReport report = rcpuService.uploadRcpuReport(
                applicationId, reportFile, remarks, creditScore, rcpuRecommendation, userEmail
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "RCPU report uploaded successfully");
            response.put("reportId", report.getId());
            response.put("fileName", report.getReportFileName());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @PostMapping("/applications/{applicationId}/decision")
    public ResponseEntity<?> processRcpuDecision(
            @PathVariable Long applicationId,
            @RequestBody Map<String, String> request) {
        
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userEmail = authentication.getName();
            
            String decision = request.get("decision");
            LoanApplicationResponse response = rcpuService.processRcpuDecision(applicationId, decision, userEmail);
            
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
    
    @GetMapping("/applications/{applicationId}/report")
    public ResponseEntity<?> getRcpuReport(@PathVariable Long applicationId) {
        try {
            RcpuReport report = rcpuService.getRcpuReport(applicationId);
            
            if (report == null) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "No RCPU report found for this application");
                return ResponseEntity.ok(response);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("id", report.getId());
            response.put("fileName", report.getReportFileName());
            response.put("remarks", report.getRemarks());
            response.put("creditScore", report.getCreditScore());
            response.put("recommendation", report.getRecommendation());
            response.put("reviewedBy", report.getReviewedBy().getName());
            response.put("createdAt", report.getCreatedAt());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}
