package com.portfolio.LOC.controller;

import com.portfolio.LOC.entity.LoanOfferLetter;
import com.portfolio.LOC.entity.EmiSchedule;
import com.portfolio.LOC.service.LoanOfferService;
import com.portfolio.LOC.service.EmiScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/loan-offers")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
public class LoanOfferController {
    
    @Autowired
    private LoanOfferService loanOfferService;
    
    @Autowired
    private EmiScheduleService emiScheduleService;
    
    @PostMapping("/generate/{applicationId}")
    public ResponseEntity<?> generateLoanOffer(
            @PathVariable Long applicationId,
            @RequestBody Map<String, Object> request) {
        
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userEmail = authentication.getName();
            
            BigDecimal approvedAmount = new BigDecimal(request.get("approvedAmount").toString());
            BigDecimal interestRate = new BigDecimal(request.get("interestRate").toString());
            Integer tenure = Integer.valueOf(request.get("tenure").toString());
            
            LoanOfferLetter offerLetter = loanOfferService.generateLoanOffer(
                applicationId, approvedAmount, interestRate, tenure, userEmail
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Loan offer generated successfully");
            response.put("offerLetterNumber", offerLetter.getOfferLetterNumber());
            response.put("fileName", offerLetter.getFileName());
            response.put("approvedAmount", offerLetter.getApprovedLoanAmount());
            response.put("emiAmount", offerLetter.getEmiAmount());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @GetMapping("/{applicationId}")
    public ResponseEntity<?> getLoanOffer(@PathVariable Long applicationId) {
        try {
            LoanOfferLetter offerLetter = loanOfferService.getLoanOffer(applicationId);
            
            if (offerLetter == null) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "No loan offer found for this application");
                return ResponseEntity.ok(response);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("id", offerLetter.getId());
            response.put("offerLetterNumber", offerLetter.getOfferLetterNumber());
            response.put("fileName", offerLetter.getFileName());
            response.put("approvedLoanAmount", offerLetter.getApprovedLoanAmount());
            response.put("interestRate", offerLetter.getInterestRate());
            response.put("loanTenure", offerLetter.getLoanTenure());
            response.put("emiAmount", offerLetter.getEmiAmount());
            response.put("processingFee", offerLetter.getProcessingFee());
            response.put("generatedBy", offerLetter.getGeneratedBy().getName());
            response.put("createdAt", offerLetter.getCreatedAt());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @GetMapping("/download/{applicationId}")
    public ResponseEntity<Resource> downloadLoanOffer(@PathVariable Long applicationId) {
        try {
            LoanOfferLetter offerLetter = loanOfferService.getLoanOffer(applicationId);
            
            if (offerLetter == null) {
                return ResponseEntity.notFound().build();
            }
            
            Resource file = new FileSystemResource(offerLetter.getFilePath());
            
            if (!file.exists()) {
                return ResponseEntity.notFound().build();
            }
            
            String contentType = Files.probeContentType(Paths.get(offerLetter.getFilePath()));
            if (contentType == null) {
                contentType = "application/pdf";
            }
            
            return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, 
                    "attachment; filename=\"" + offerLetter.getFileName() + "\"")
                .body(file);
                
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/{applicationId}/emi-schedule")
    public ResponseEntity<?> getEmiSchedule(@PathVariable Long applicationId) {
        try {
            List<EmiSchedule> emiSchedule = emiScheduleService.getEmiSchedule(applicationId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("emiSchedule", emiSchedule);
            response.put("totalEmis", emiScheduleService.getTotalEmiCount(applicationId));
            response.put("paidEmis", emiScheduleService.getPaidEmiCount(applicationId));
            response.put("totalOutstanding", emiScheduleService.getTotalOutstanding(applicationId));
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @PostMapping("/emi/{emiId}/pay")
    public ResponseEntity<?> payEmi(
            @PathVariable Long emiId,
            @RequestBody Map<String, Object> request) {
        
        try {
            BigDecimal paidAmount = new BigDecimal(request.get("paidAmount").toString());
            String remarks = request.get("remarks").toString();
            
            EmiSchedule emi = emiScheduleService.payEmi(emiId, paidAmount, remarks);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "EMI payment recorded successfully");
            response.put("emiId", emi.getId());
            response.put("status", emi.getStatus());
            response.put("paidAmount", emi.getPaidAmount());
            response.put("paidDate", emi.getPaidDate());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @GetMapping("/overdue-emis")
    public ResponseEntity<?> getOverdueEmis() {
        try {
            List<EmiSchedule> overdueEmis = emiScheduleService.getOverdueEmis();
            return ResponseEntity.ok(overdueEmis);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}
