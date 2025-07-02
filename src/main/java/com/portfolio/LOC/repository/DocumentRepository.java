package com.portfolio.LOC.repository;

import com.portfolio.LOC.entity.Document;
import com.portfolio.LOC.entity.LoanApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
    
    List<Document> findByLoanApplication(LoanApplication loanApplication);
    
    List<Document> findByLoanApplicationId(Long loanApplicationId);
    
    List<Document> findByLoanApplicationIdOrderByCreatedAtAsc(Long loanApplicationId);
    
    Optional<Document> findByLoanApplicationAndDocumentType(LoanApplication loanApplication, Document.DocumentType documentType);
    
    Optional<Document> findByLoanApplicationIdAndDocumentType(Long loanApplicationId, Document.DocumentType documentType);
    
    @Query("SELECT d FROM Document d WHERE d.loanApplication.id = :applicationId AND d.documentType = :documentType")
    Optional<Document> findByApplicationIdAndType(@Param("applicationId") Long applicationId, 
                                                 @Param("documentType") Document.DocumentType documentType);
    
    boolean existsByLoanApplicationAndDocumentType(LoanApplication loanApplication, Document.DocumentType documentType);
    
    boolean existsByLoanApplicationIdAndDocumentType(Long loanApplicationId, Document.DocumentType documentType);
    
    @Query("SELECT COUNT(d) FROM Document d WHERE d.loanApplication.id = :applicationId")
    long countByLoanApplicationId(@Param("applicationId") Long applicationId);
    
    void deleteByLoanApplicationId(Long loanApplicationId);
}
