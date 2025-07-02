package com.portfolio.LOC.controller;

import com.portfolio.LOC.entity.Document;
import com.portfolio.LOC.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/documents")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
public class DocumentController {
    
    @Autowired
    private DocumentService documentService;
    
    @PostMapping("/upload/{applicationId}")
    public ResponseEntity<?> uploadDocument(
            @PathVariable Long applicationId,
            @RequestParam("documentType") String documentType,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "description", required = false) String description) {
        
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userEmail = authentication.getName();
            
            Document.DocumentType docType = Document.DocumentType.valueOf(documentType.toUpperCase());
            
            Document document = documentService.uploadDocument(applicationId, docType, file, description, userEmail);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Document uploaded successfully");
            response.put("documentId", document.getId());
            response.put("fileName", document.getOriginalFileName());
            response.put("documentType", document.getDocumentType().getDisplayName());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @GetMapping("/application/{applicationId}")
    public ResponseEntity<?> getDocumentsByApplication(@PathVariable Long applicationId) {
        try {
            List<Document> documents = documentService.getDocumentsByApplication(applicationId);
            
            List<Map<String, Object>> documentList = documents.stream()
                .map(this::convertDocumentToMap)
                .toList();
            
            return ResponseEntity.ok(documentList);
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @GetMapping("/download/{documentId}")
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
    
    @GetMapping("/view/{documentId}")
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
    
    @DeleteMapping("/{documentId}")
    public ResponseEntity<?> deleteDocument(@PathVariable Long documentId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userEmail = authentication.getName();
            
            documentService.deleteDocument(documentId, userEmail);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Document deleted successfully");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @GetMapping("/check-required/{applicationId}")
    public ResponseEntity<?> checkRequiredDocuments(@PathVariable Long applicationId) {
        try {
            boolean hasRequired = documentService.hasRequiredDocuments(applicationId);
            long documentCount = documentService.getDocumentCount(applicationId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("hasRequiredDocuments", hasRequired);
            response.put("documentCount", documentCount);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @PostMapping("/test/create-mock-documents/{applicationId}")
    public ResponseEntity<?> createMockDocuments(@PathVariable Long applicationId) {
        try {
            List<Map<String, Object>> mockDocuments = documentService.createMockDocuments(applicationId);
            return ResponseEntity.ok(mockDocuments);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    private Map<String, Object> convertDocumentToMap(Document document) {
        Map<String, Object> docMap = new HashMap<>();
        docMap.put("id", document.getId());
        docMap.put("documentType", document.getDocumentType());
        docMap.put("documentTypeName", document.getDocumentType().getDisplayName());
        docMap.put("originalFileName", document.getOriginalFileName());
        docMap.put("contentType", document.getContentType());
        docMap.put("fileSize", document.getFileSize());
        docMap.put("description", document.getDescription());
        docMap.put("uploadedBy", document.getUploadedBy().getName());
        docMap.put("createdAt", document.getCreatedAt());
        docMap.put("updatedAt", document.getUpdatedAt());
        return docMap;
    }
}
