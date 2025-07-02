package com.portfolio.LOC.repository;

import com.portfolio.LOC.entity.LoanApplication;
import com.portfolio.LOC.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface LoanApplicationRepository extends JpaRepository<LoanApplication, Long> {
    
    Optional<LoanApplication> findByApplicationId(String applicationId);
    
    List<LoanApplication> findBySubmittedBy(User submittedBy);
    
    List<LoanApplication> findByStatus(LoanApplication.ApplicationStatus status);
    
    List<LoanApplication> findByStatusIn(List<LoanApplication.ApplicationStatus> statuses);
    
    @Query("SELECT la FROM LoanApplication la WHERE la.status = :status ORDER BY la.createdAt ASC")
    List<LoanApplication> findByStatusOrderByCreatedAtAsc(@Param("status") LoanApplication.ApplicationStatus status);
    
    @Query("SELECT la FROM LoanApplication la WHERE la.status IN :statuses ORDER BY la.createdAt ASC")
    List<LoanApplication> findByStatusInOrderByCreatedAtAsc(@Param("statuses") List<LoanApplication.ApplicationStatus> statuses);
    
    @Query("SELECT la FROM LoanApplication la WHERE la.submittedBy.email = :email ORDER BY la.createdAt DESC")
    List<LoanApplication> findBySubmittedByEmailOrderByCreatedAtDesc(@Param("email") String email);
    
    @Query("SELECT COUNT(la) FROM LoanApplication la WHERE la.status = :status")
    long countByStatus(@Param("status") LoanApplication.ApplicationStatus status);
    
    @Query("SELECT COUNT(la) FROM LoanApplication la WHERE la.createdAt >= :startDate")
    long countByCreatedAtAfter(@Param("startDate") LocalDateTime startDate);
    
    @Query("SELECT la FROM LoanApplication la WHERE la.firstName LIKE %:name% OR la.lastName LIKE %:name% OR la.applicationId LIKE %:applicationId%")
    List<LoanApplication> searchByNameOrApplicationId(@Param("name") String name, @Param("applicationId") String applicationId);
    
    boolean existsByPanNumber(String panNumber);
    
    boolean existsByAadhaarNumber(String aadhaarNumber);
}
