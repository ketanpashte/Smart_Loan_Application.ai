package com.portfolio.LOC.repository;

import com.portfolio.LOC.entity.RcpuReport;
import com.portfolio.LOC.entity.LoanApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RcpuReportRepository extends JpaRepository<RcpuReport, Long> {
    
    Optional<RcpuReport> findByLoanApplication(LoanApplication loanApplication);
    
    Optional<RcpuReport> findByLoanApplicationId(Long loanApplicationId);
    
    boolean existsByLoanApplication(LoanApplication loanApplication);
}
