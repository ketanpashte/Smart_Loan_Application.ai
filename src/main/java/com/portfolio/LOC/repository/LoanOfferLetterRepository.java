package com.portfolio.LOC.repository;

import com.portfolio.LOC.entity.LoanOfferLetter;
import com.portfolio.LOC.entity.LoanApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LoanOfferLetterRepository extends JpaRepository<LoanOfferLetter, Long> {
    
    Optional<LoanOfferLetter> findByLoanApplication(LoanApplication loanApplication);
    
    Optional<LoanOfferLetter> findByLoanApplicationId(Long loanApplicationId);
    
    Optional<LoanOfferLetter> findByOfferLetterNumber(String offerLetterNumber);
    
    boolean existsByLoanApplication(LoanApplication loanApplication);
}
