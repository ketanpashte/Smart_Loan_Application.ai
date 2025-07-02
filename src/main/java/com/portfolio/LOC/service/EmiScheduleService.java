package com.portfolio.LOC.service;

import com.portfolio.LOC.entity.EmiSchedule;
import com.portfolio.LOC.entity.LoanApplication;
import com.portfolio.LOC.repository.EmiScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class EmiScheduleService {
    
    @Autowired
    private EmiScheduleRepository emiScheduleRepository;
    
    public List<EmiSchedule> generateEmiSchedule(LoanApplication application, BigDecimal loanAmount, 
                                               BigDecimal annualInterestRate, Integer tenureYears, BigDecimal emiAmount) {
        
        List<EmiSchedule> emiSchedules = new ArrayList<>();
        
        BigDecimal monthlyRate = annualInterestRate.divide(BigDecimal.valueOf(1200), 10, RoundingMode.HALF_UP);
        Integer totalMonths = tenureYears * 12;
        BigDecimal outstandingBalance = loanAmount;
        
        LocalDate currentDate = LocalDate.now().plusMonths(1); // First EMI after 1 month
        
        for (int i = 1; i <= totalMonths; i++) {
            // Calculate interest for this month
            BigDecimal interestAmount = outstandingBalance.multiply(monthlyRate).setScale(2, RoundingMode.HALF_UP);
            
            // Calculate principal for this month
            BigDecimal principalAmount = emiAmount.subtract(interestAmount);
            
            // Adjust for last EMI to handle rounding differences
            if (i == totalMonths) {
                principalAmount = outstandingBalance;
                emiAmount = principalAmount.add(interestAmount);
            }
            
            // Update outstanding balance
            outstandingBalance = outstandingBalance.subtract(principalAmount);
            
            // Create EMI schedule entry
            EmiSchedule emiSchedule = new EmiSchedule(
                application, i, currentDate, emiAmount, principalAmount, interestAmount, outstandingBalance
            );
            
            emiSchedules.add(emiSchedule);
            
            // Move to next month
            currentDate = currentDate.plusMonths(1);
        }
        
        // Save all EMI schedules
        return emiScheduleRepository.saveAll(emiSchedules);
    }
    
    public List<EmiSchedule> getEmiSchedule(Long applicationId) {
        return emiScheduleRepository.findByLoanApplicationIdOrderByEmiNumberAsc(applicationId);
    }
    
    public List<EmiSchedule> getOverdueEmis() {
        return emiScheduleRepository.findOverdueEmis(LocalDate.now());
    }
    
    public EmiSchedule payEmi(Long emiId, BigDecimal paidAmount, String remarks) {
        EmiSchedule emi = emiScheduleRepository.findById(emiId)
            .orElseThrow(() -> new RuntimeException("EMI not found"));
        
        if (emi.getStatus() == EmiSchedule.EmiStatus.PAID) {
            throw new RuntimeException("EMI already paid");
        }
        
        emi.setPaidAmount(paidAmount);
        emi.setPaidDate(LocalDate.now());
        emi.setRemarks(remarks);
        
        if (paidAmount.compareTo(emi.getEmiAmount()) >= 0) {
            emi.setStatus(EmiSchedule.EmiStatus.PAID);
        } else {
            emi.setStatus(EmiSchedule.EmiStatus.PARTIAL_PAID);
        }
        
        // Calculate late fee if payment is after due date
        if (LocalDate.now().isAfter(emi.getDueDate())) {
            long daysLate = LocalDate.now().toEpochDay() - emi.getDueDate().toEpochDay();
            BigDecimal lateFee = BigDecimal.valueOf(daysLate * 100); // ₹100 per day
            emi.setLateFee(lateFee);
        }
        
        return emiScheduleRepository.save(emi);
    }
    
    public void markOverdueEmis() {
        List<EmiSchedule> overdueEmis = emiScheduleRepository.findByDueDateBeforeAndStatus(
            LocalDate.now(), EmiSchedule.EmiStatus.PENDING
        );
        
        for (EmiSchedule emi : overdueEmis) {
            emi.setStatus(EmiSchedule.EmiStatus.OVERDUE);
        }
        
        emiScheduleRepository.saveAll(overdueEmis);
    }
    
    public BigDecimal getTotalOutstanding(Long applicationId) {
        List<EmiSchedule> pendingEmis = emiScheduleRepository.findByLoanApplicationIdOrderByEmiNumberAsc(applicationId)
            .stream()
            .filter(emi -> emi.getStatus() != EmiSchedule.EmiStatus.PAID)
            .toList();
        
        return pendingEmis.stream()
            .map(EmiSchedule::getEmiAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    public Long getPaidEmiCount(Long applicationId) {
        return emiScheduleRepository.countPaidEmisByLoanApplicationId(applicationId);
    }
    
    public Long getTotalEmiCount(Long applicationId) {
        return emiScheduleRepository.countTotalEmisByLoanApplicationId(applicationId);
    }
}
