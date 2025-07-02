package com.portfolio.LOC.repository;

import com.portfolio.LOC.entity.ApprovalHistory;
import com.portfolio.LOC.entity.LoanApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApprovalHistoryRepository extends JpaRepository<ApprovalHistory, Long> {
    
    List<ApprovalHistory> findByLoanApplicationOrderByCreatedAtAsc(LoanApplication loanApplication);
    
    List<ApprovalHistory> findByLoanApplicationIdOrderByCreatedAtAsc(Long loanApplicationId);
    
    List<ApprovalHistory> findByStageOrderByCreatedAtDesc(ApprovalHistory.Stage stage);
}
