package com.portfolio.LOC.service;

import com.portfolio.LOC.entity.Document;
import com.portfolio.LOC.entity.LoanApplication;
import com.portfolio.LOC.entity.User;
import com.portfolio.LOC.repository.DocumentRepository;
import com.portfolio.LOC.repository.LoanApplicationRepository;
import com.portfolio.LOC.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.UUID;

@Service
public class DocumentService {
    
    private static final String UPLOAD_DIR = "uploads/documents/";
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    
    @Autowired
    private DocumentRepository documentRepository;
    
    @Autowired
    private LoanApplicationRepository loanApplicationRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    public Document uploadDocument(Long applicationId, Document.DocumentType documentType, 
                                 MultipartFile file, String description, String userEmail) {
        
        // Validate file
        if (file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }
        
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new RuntimeException("File size exceeds maximum limit of 5MB");
        }
        
        // Validate application
        LoanApplication application = loanApplicationRepository.findById(applicationId)
            .orElseThrow(() -> new RuntimeException("Application not found"));
        
        // Validate user
        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Check if document already exists for this type
        Optional<Document> existingDoc = documentRepository.findByLoanApplicationIdAndDocumentType(applicationId, documentType);
        if (existingDoc.isPresent()) {
            // Delete existing file and update
            deletePhysicalFile(existingDoc.get().getFilePath());
            documentRepository.delete(existingDoc.get());
        }
        
        try {
            // Create upload directory if it doesn't exist
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String storedFileName = documentType.name() + "_" + application.getApplicationId() + "_" + 
                                  UUID.randomUUID().toString() + fileExtension;
            Path filePath = uploadPath.resolve(storedFileName);
            
            // Save file
            Files.copy(file.getInputStream(), filePath);
            
            // Create document entity
            Document document = new Document(
                application,
                documentType,
                originalFilename,
                storedFileName,
                filePath.toString(),
                file.getContentType(),
                file.getSize(),
                user
            );
            document.setDescription(description);
            
            return documentRepository.save(document);
            
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file: " + e.getMessage());
        }
    }
    
    public List<Document> getDocumentsByApplication(Long applicationId) {
        return documentRepository.findByLoanApplicationIdOrderByCreatedAtAsc(applicationId);
    }
    
    public Optional<Document> getDocument(Long documentId) {
        return documentRepository.findById(documentId);
    }
    
    public Resource downloadDocument(Long documentId) {
        Document document = documentRepository.findById(documentId)
            .orElseThrow(() -> new RuntimeException("Document not found"));
        
        File file = new File(document.getFilePath());
        if (!file.exists()) {
            throw new RuntimeException("File not found: " + document.getOriginalFileName());
        }
        
        return new FileSystemResource(file);
    }
    
    public void deleteDocument(Long documentId, String userEmail) {
        Document document = documentRepository.findById(documentId)
            .orElseThrow(() -> new RuntimeException("Document not found"));
        
        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Check if user has permission to delete (same user who uploaded or admin)
        if (!document.getUploadedBy().getId().equals(user.getId()) && 
            !user.getRole().equals(User.Role.ADMIN)) {
            throw new RuntimeException("Not authorized to delete this document");
        }
        
        // Delete physical file
        deletePhysicalFile(document.getFilePath());
        
        // Delete from database
        documentRepository.delete(document);
    }
    
    private void deletePhysicalFile(String filePath) {
        try {
            File file = new File(filePath);
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            // Log error but don't throw exception
            System.err.println("Failed to delete physical file: " + filePath);
        }
    }
    
    public boolean hasRequiredDocuments(Long applicationId) {
        List<Document> documents = getDocumentsByApplication(applicationId);
        
        // Check for required document types
        boolean hasITR = documents.stream().anyMatch(d -> d.getDocumentType() == Document.DocumentType.ITR);
        boolean hasBankStatement = documents.stream().anyMatch(d -> d.getDocumentType() == Document.DocumentType.BANK_STATEMENT);
        boolean hasAadhaar = documents.stream().anyMatch(d -> d.getDocumentType() == Document.DocumentType.AADHAAR);
        boolean hasPAN = documents.stream().anyMatch(d -> d.getDocumentType() == Document.DocumentType.PAN);
        boolean hasPhoto = documents.stream().anyMatch(d -> d.getDocumentType() == Document.DocumentType.PHOTO);
        
        return hasITR && hasBankStatement && hasAadhaar && hasPAN && hasPhoto;
    }

    public long getDocumentCount(Long applicationId) {
        return documentRepository.countByLoanApplicationId(applicationId);
    }

    public List<Map<String, Object>> createMockDocuments(Long applicationId) {
        LoanApplication application = loanApplicationRepository.findById(applicationId)
            .orElseThrow(() -> new RuntimeException("Application not found"));

        User currentUser = userRepository.findByEmail("admin@smartloan.com")
            .orElse(userRepository.findAll().stream().findFirst().orElse(null));

        if (currentUser == null) {
            throw new RuntimeException("No user found to associate with documents");
        }

        // Create mock documents
        List<Document> mockDocs = new ArrayList<>();

        // Aadhaar Card
        Document aadhaar = new Document();
        aadhaar.setLoanApplication(application);
        aadhaar.setDocumentType(Document.DocumentType.AADHAAR);
        aadhaar.setOriginalFileName("aadhaar_card.pdf");
        aadhaar.setStoredFileName("aadhaar_" + System.currentTimeMillis() + ".pdf");
        aadhaar.setFilePath("uploads/documents/aadhaar_" + System.currentTimeMillis() + ".pdf");
        aadhaar.setContentType("application/pdf");
        aadhaar.setFileSize(1024000L);
        aadhaar.setDescription("Aadhaar Card");
        aadhaar.setUploadedBy(currentUser);
        aadhaar.setCreatedAt(LocalDateTime.now());
        aadhaar.setUpdatedAt(LocalDateTime.now());
        mockDocs.add(documentRepository.save(aadhaar));

        // PAN Card
        Document pan = new Document();
        pan.setLoanApplication(application);
        pan.setDocumentType(Document.DocumentType.PAN);
        pan.setOriginalFileName("pan_card.pdf");
        pan.setStoredFileName("pan_" + System.currentTimeMillis() + ".pdf");
        pan.setFilePath("uploads/documents/pan_" + System.currentTimeMillis() + ".pdf");
        pan.setContentType("application/pdf");
        pan.setFileSize(512000L);
        pan.setDescription("PAN Card");
        pan.setUploadedBy(currentUser);
        pan.setCreatedAt(LocalDateTime.now());
        pan.setUpdatedAt(LocalDateTime.now());
        mockDocs.add(documentRepository.save(pan));

        // Bank Statement
        Document bankStatement = new Document();
        bankStatement.setLoanApplication(application);
        bankStatement.setDocumentType(Document.DocumentType.BANK_STATEMENT);
        bankStatement.setOriginalFileName("bank_statement.pdf");
        bankStatement.setStoredFileName("bank_" + System.currentTimeMillis() + ".pdf");
        bankStatement.setFilePath("uploads/documents/bank_" + System.currentTimeMillis() + ".pdf");
        bankStatement.setContentType("application/pdf");
        bankStatement.setFileSize(2048000L);
        bankStatement.setDescription("Bank Statement - Last 6 months");
        bankStatement.setUploadedBy(currentUser);
        bankStatement.setCreatedAt(LocalDateTime.now());
        bankStatement.setUpdatedAt(LocalDateTime.now());
        mockDocs.add(documentRepository.save(bankStatement));

        // Convert to maps for response
        return mockDocs.stream()
            .map(this::convertDocumentToMap)
            .collect(Collectors.toList());
    }

    private Map<String, Object> convertDocumentToMap(Document document) {
        Map<String, Object> docMap = new HashMap<>();
        docMap.put("id", document.getId());
        docMap.put("documentType", document.getDocumentType());
        docMap.put("documentTypeName", document.getDocumentType().getDisplayName());
        docMap.put("originalFileName", document.getOriginalFileName());
        docMap.put("fileName", document.getStoredFileName());
        docMap.put("contentType", document.getContentType());
        docMap.put("fileSize", document.getFileSize());
        docMap.put("description", document.getDescription());
        docMap.put("uploadedBy", document.getUploadedBy().getName());
        docMap.put("createdAt", document.getCreatedAt());
        docMap.put("updatedAt", document.getUpdatedAt());
        return docMap;
    }
}
