package com.portfolio.LOC.service;

import com.portfolio.LOC.entity.RcpuReport;
import com.portfolio.LOC.entity.LoanOfferLetter;
import com.portfolio.LOC.repository.RcpuReportRepository;
import com.portfolio.LOC.repository.LoanOfferLetterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Optional;

@Service
public class FileDownloadService {
    
    @Autowired
    private RcpuReportRepository rcpuReportRepository;
    
    @Autowired
    private LoanOfferLetterRepository loanOfferLetterRepository;
    
    public Resource downloadRcpuReport(Long applicationId) {
        Optional<RcpuReport> reportOpt = rcpuReportRepository.findByLoanApplicationId(applicationId);
        
        if (reportOpt.isEmpty()) {
            throw new RuntimeException("RCPU report not found for application ID: " + applicationId);
        }
        
        RcpuReport report = reportOpt.get();
        File file = new File(report.getReportFilePath());
        
        if (!file.exists()) {
            throw new RuntimeException("RCPU report file not found: " + report.getReportFileName());
        }
        
        return new FileSystemResource(file);
    }
    
    public Resource downloadLoanOffer(Long applicationId) {
        Optional<LoanOfferLetter> offerOpt = loanOfferLetterRepository.findByLoanApplicationId(applicationId);
        
        if (offerOpt.isEmpty()) {
            throw new RuntimeException("Loan offer not found for application ID: " + applicationId);
        }
        
        LoanOfferLetter offer = offerOpt.get();
        File file = new File(offer.getFilePath());
        
        if (!file.exists()) {
            throw new RuntimeException("Loan offer file not found: " + offer.getFileName());
        }
        
        return new FileSystemResource(file);
    }
    
    public String getRcpuReportFileName(Long applicationId) {
        Optional<RcpuReport> reportOpt = rcpuReportRepository.findByLoanApplicationId(applicationId);
        return reportOpt.map(RcpuReport::getReportFileName).orElse("rcpu-report.pdf");
    }
    
    public String getLoanOfferFileName(Long applicationId) {
        Optional<LoanOfferLetter> offerOpt = loanOfferLetterRepository.findByLoanApplicationId(applicationId);
        return offerOpt.map(LoanOfferLetter::getFileName).orElse("loan-offer.pdf");
    }
    
    public boolean hasRcpuReport(Long applicationId) {
        return rcpuReportRepository.findByLoanApplicationId(applicationId).isPresent();
    }
    
    public boolean hasLoanOffer(Long applicationId) {
        return loanOfferLetterRepository.findByLoanApplicationId(applicationId).isPresent();
    }
}
