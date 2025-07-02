package com.portfolio.LOC.repository;

import com.portfolio.LOC.entity.EmiSchedule;
import com.portfolio.LOC.entity.LoanApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EmiScheduleRepository extends JpaRepository<EmiSchedule, Long> {
    
    List<EmiSchedule> findByLoanApplicationOrderByEmiNumberAsc(LoanApplication loanApplication);
    
    List<EmiSchedule> findByLoanApplicationIdOrderByEmiNumberAsc(Long loanApplicationId);
    
    List<EmiSchedule> findByStatusOrderByDueDateAsc(EmiSchedule.EmiStatus status);
    
    List<EmiSchedule> findByDueDateBeforeAndStatus(LocalDate date, EmiSchedule.EmiStatus status);
    
    @Query("SELECT e FROM EmiSchedule e WHERE e.dueDate <= :date AND e.status = 'PENDING'")
    List<EmiSchedule> findOverdueEmis(LocalDate date);
    
    @Query("SELECT COUNT(e) FROM EmiSchedule e WHERE e.loanApplication.id = :loanApplicationId AND e.status = 'PAID'")
    Long countPaidEmisByLoanApplicationId(Long loanApplicationId);
    
    @Query("SELECT COUNT(e) FROM EmiSchedule e WHERE e.loanApplication.id = :loanApplicationId")
    Long countTotalEmisByLoanApplicationId(Long loanApplicationId);
}
