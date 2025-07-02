package com.portfolio.LOC.controller;

import com.portfolio.LOC.service.FileDownloadService;
import com.portfolio.LOC.service.DocumentService;
import com.portfolio.LOC.entity.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/files")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
public class FileDownloadController {
    
    @Autowired
    private FileDownloadService fileDownloadService;

    @Autowired
    private DocumentService documentService;
    
    @GetMapping("/download/rcpu-report/{applicationId}")
    public ResponseEntity<Resource> downloadRcpuReport(@PathVariable Long applicationId) {
        try {
            Resource file = fileDownloadService.downloadRcpuReport(applicationId);
            String fileName = fileDownloadService.getRcpuReportFileName(applicationId);
            
            return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .body(file);
                
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/download/loan-offer/{applicationId}")
    public ResponseEntity<Resource> downloadLoanOffer(@PathVariable Long applicationId) {
        try {
            Resource file = fileDownloadService.downloadLoanOffer(applicationId);
            String fileName = fileDownloadService.getLoanOfferFileName(applicationId);
            
            return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .body(file);
                
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/check/{applicationId}")
    public ResponseEntity<?> checkAvailableFiles(@PathVariable Long applicationId) {
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("hasRcpuReport", fileDownloadService.hasRcpuReport(applicationId));
            response.put("hasLoanOffer", fileDownloadService.hasLoanOffer(applicationId));
            
            if (fileDownloadService.hasRcpuReport(applicationId)) {
                response.put("rcpuReportFileName", fileDownloadService.getRcpuReportFileName(applicationId));
            }
            
            if (fileDownloadService.hasLoanOffer(applicationId)) {
                response.put("loanOfferFileName", fileDownloadService.getLoanOfferFileName(applicationId));
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/download/document/{documentId}")
    public ResponseEntity<Resource> downloadDocument(@PathVariable Long documentId) {
        try {
            Document document = documentService.getDocument(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found"));

            Resource file = documentService.downloadDocument(documentId);

            String contentType = document.getContentType();
            if (contentType == null) {
                contentType = Files.probeContentType(Paths.get(document.getFilePath()));
                if (contentType == null) {
                    contentType = "application/octet-stream";
                }
            }

            return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"" + document.getOriginalFileName() + "\"")
                .body(file);

        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/view/document/{documentId}")
    public ResponseEntity<Resource> viewDocument(@PathVariable Long documentId) {
        try {
            Document document = documentService.getDocument(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found"));

            Resource file = documentService.downloadDocument(documentId);

            String contentType = document.getContentType();
            if (contentType == null) {
                contentType = Files.probeContentType(Paths.get(document.getFilePath()));
                if (contentType == null) {
                    contentType = "application/octet-stream";
                }
            }

            return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
                .body(file);

        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
