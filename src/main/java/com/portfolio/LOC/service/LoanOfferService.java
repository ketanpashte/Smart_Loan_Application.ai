package com.portfolio.LOC.service;

import com.portfolio.LOC.entity.*;
import com.portfolio.LOC.repository.*;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Transactional
public class LoanOfferService {
    
    @Autowired
    private LoanApplicationRepository loanApplicationRepository;
    
    @Autowired
    private LoanOfferLetterRepository loanOfferLetterRepository;
    
    @Autowired
    private ApprovalHistoryRepository approvalHistoryRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private EmiScheduleService emiScheduleService;
    
    private final String UPLOAD_DIR = "uploads/loan-offers/";
    
    public LoanOfferLetter generateLoanOffer(Long applicationId, BigDecimal approvedAmount, 
                                           BigDecimal interestRate, Integer tenure, String userEmail) {
        
        // Validate application
        LoanApplication application = loanApplicationRepository.findById(applicationId)
            .orElseThrow(() -> new RuntimeException("Application not found"));
        
        if (!isApplicationApproved(application)) {
            throw new RuntimeException("Application is not in approved status");
        }
        
        // Validate user
        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (!user.getRole().equals(User.Role.ADMIN)) {
            throw new RuntimeException("User not authorized to generate loan offers");
        }
        
        // Check if offer already exists
        if (loanOfferLetterRepository.existsByLoanApplication(application)) {
            throw new RuntimeException("Loan offer already exists for this application");
        }
        
        try {
            // Calculate EMI
            BigDecimal emiAmount = calculateEMI(approvedAmount, interestRate, tenure);
            
            // Generate PDF
            byte[] pdfBytes = generateLoanOfferPDF(application, approvedAmount, interestRate, tenure, emiAmount);
            
            // Create upload directory if it doesn't exist
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            // Generate unique filename
            String fileName = "LoanOffer_" + application.getApplicationId() + "_" + System.currentTimeMillis() + ".pdf";
            Path filePath = uploadPath.resolve(fileName);
            
            // Save PDF file
            Files.write(filePath, pdfBytes);
            
            // Create loan offer letter entity
            LoanOfferLetter offerLetter = new LoanOfferLetter();
            offerLetter.setLoanApplication(application);
            offerLetter.setFileName(fileName);
            offerLetter.setFilePath(filePath.toString());
            offerLetter.setApprovedLoanAmount(approvedAmount);
            offerLetter.setInterestRate(interestRate);
            offerLetter.setLoanTenure(tenure);
            offerLetter.setEmiAmount(emiAmount);
            offerLetter.setProcessingFee(calculateProcessingFee(approvedAmount));
            offerLetter.setTermsAndConditions(getStandardTermsAndConditions());
            offerLetter.setGeneratedBy(user);
            
            // Save offer letter
            offerLetter = loanOfferLetterRepository.save(offerLetter);
            
            // Update application status to final approved
            application.setStatus(LoanApplication.ApplicationStatus.L3_APPROVED);
            application.setUpdatedAt(LocalDateTime.now());
            loanApplicationRepository.save(application);
            
            // Generate EMI schedule
            emiScheduleService.generateEmiSchedule(application, approvedAmount, interestRate, tenure, emiAmount);
            
            return offerLetter;
            
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate loan offer: " + e.getMessage());
        }
    }
    
    private boolean isApplicationApproved(LoanApplication application) {
        return application.getStatus().equals(LoanApplication.ApplicationStatus.L1_APPROVED) ||
               application.getStatus().equals(LoanApplication.ApplicationStatus.L2_APPROVED) ||
               application.getStatus().equals(LoanApplication.ApplicationStatus.PENDING_L3);
    }
    
    private BigDecimal calculateEMI(BigDecimal principal, BigDecimal annualRate, Integer tenure) {
        BigDecimal monthlyRate = annualRate.divide(BigDecimal.valueOf(1200), 10, RoundingMode.HALF_UP);
        Integer months = tenure * 12;
        
        if (monthlyRate.compareTo(BigDecimal.ZERO) == 0) {
            return principal.divide(BigDecimal.valueOf(months), 2, RoundingMode.HALF_UP);
        }
        
        BigDecimal onePlusR = BigDecimal.ONE.add(monthlyRate);
        BigDecimal onePlusRPowN = onePlusR.pow(months);
        
        BigDecimal numerator = principal.multiply(monthlyRate).multiply(onePlusRPowN);
        BigDecimal denominator = onePlusRPowN.subtract(BigDecimal.ONE);
        
        return numerator.divide(denominator, 2, RoundingMode.HALF_UP);
    }
    
    private BigDecimal calculateProcessingFee(BigDecimal loanAmount) {
        // 1% of loan amount, minimum 5000, maximum 50000
        BigDecimal fee = loanAmount.multiply(BigDecimal.valueOf(0.01));
        fee = fee.max(BigDecimal.valueOf(5000));
        fee = fee.min(BigDecimal.valueOf(50000));
        return fee;
    }
    
    private byte[] generateLoanOfferPDF(LoanApplication application, BigDecimal approvedAmount, 
                                       BigDecimal interestRate, Integer tenure, BigDecimal emiAmount) throws IOException {
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);
        
        // Header
        document.add(new Paragraph("SMARTLOAN FINANCIAL SERVICES")
            .setTextAlignment(TextAlignment.CENTER)
            .setFontSize(18)
            .setBold());
        
        document.add(new Paragraph("LOAN OFFER LETTER")
            .setTextAlignment(TextAlignment.CENTER)
            .setFontSize(16)
            .setBold());
        
        document.add(new Paragraph("\n"));
        
        // Date and Application ID
        document.add(new Paragraph("Date: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))));
        document.add(new Paragraph("Application ID: " + application.getApplicationId()));
        document.add(new Paragraph("\n"));
        
        // Applicant Details
        document.add(new Paragraph("Dear " + application.getFirstName() + " " + application.getLastName() + ",")
            .setBold());
        
        document.add(new Paragraph("\n"));
        document.add(new Paragraph("We are pleased to inform you that your loan application has been approved. " +
            "Please find the loan offer details below:"));
        document.add(new Paragraph("\n"));
        
        // Loan Details Table
        Table table = new Table(UnitValue.createPercentArray(new float[]{3, 4}));
        table.setWidth(UnitValue.createPercentValue(100));
        
        addTableRow(table, "Loan Amount", "₹ " + approvedAmount.toString());
        addTableRow(table, "Interest Rate", interestRate.toString() + "% per annum");
        addTableRow(table, "Loan Tenure", tenure.toString() + " years");
        addTableRow(table, "Monthly EMI", "₹ " + emiAmount.toString());
        addTableRow(table, "Processing Fee", "₹ " + calculateProcessingFee(approvedAmount).toString());
        
        document.add(table);
        document.add(new Paragraph("\n"));
        
        // Approval History
        document.add(new Paragraph("Approval History:").setBold());
        List<ApprovalHistory> history = approvalHistoryRepository.findByLoanApplicationIdOrderByCreatedAtAsc(application.getId());
        
        for (ApprovalHistory h : history) {
            document.add(new Paragraph(h.getStage() + " - " + h.getDecision() + " by " + h.getApprovedBy().getName() + 
                " on " + h.getCreatedAt().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"))));
        }
        
        document.add(new Paragraph("\n"));
        document.add(new Paragraph("Terms and Conditions:").setBold());
        document.add(new Paragraph(getStandardTermsAndConditions()));
        
        document.add(new Paragraph("\n"));
        document.add(new Paragraph("Congratulations on your loan approval!"));
        document.add(new Paragraph("\n"));
        document.add(new Paragraph("Sincerely,"));
        document.add(new Paragraph("SmartLoan Financial Services"));
        
        document.close();
        return baos.toByteArray();
    }
    
    private void addTableRow(Table table, String key, String value) {
        table.addCell(new Cell().add(new Paragraph(key).setBold()));
        table.addCell(new Cell().add(new Paragraph(value)));
    }
    
    private String getStandardTermsAndConditions() {
        return "1. The loan is subject to the terms and conditions mentioned in the loan agreement.\n" +
               "2. EMI payments must be made on or before the due date.\n" +
               "3. Late payment charges will be applicable for delayed payments.\n" +
               "4. The loan is secured against the property being purchased.\n" +
               "5. Insurance coverage is mandatory for the loan tenure.";
    }
    
    public LoanOfferLetter getLoanOffer(Long applicationId) {
        LoanApplication application = loanApplicationRepository.findById(applicationId)
            .orElseThrow(() -> new RuntimeException("Application not found"));
        
        return loanOfferLetterRepository.findByLoanApplication(application)
            .orElse(null);
    }
}
