package com.portfolio.LOC.service;

import com.portfolio.LOC.entity.LoanApplication;
import com.portfolio.LOC.entity.EmiSchedule;
import com.portfolio.LOC.entity.User;
import com.portfolio.LOC.entity.ApprovalHistory;
import com.portfolio.LOC.repository.LoanApplicationRepository;
import com.portfolio.LOC.repository.EmiScheduleRepository;
import com.portfolio.LOC.repository.UserRepository;
import com.portfolio.LOC.repository.ApprovalHistoryRepository;
import com.portfolio.LOC.dto.LoanApplicationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AdminDashboardService {
    
    @Autowired
    private LoanApplicationRepository loanApplicationRepository;
    
    @Autowired
    private EmiScheduleRepository emiScheduleRepository;
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ApprovalHistoryRepository approvalHistoryRepository;

    public Map<String, Object> getDashboardData() {
        Map<String, Object> dashboardData = new HashMap<>();
        
        // Summary Statistics
        dashboardData.put("summary", getSummaryStatistics());
        
        // Application Status Distribution
        dashboardData.put("stageDistribution", getApplicationStatusDistribution());

        // Monthly Trends
        dashboardData.put("monthlyTrend", getMonthlyTrends());
        
        // Loan Purpose Distribution
        dashboardData.put("loanPurposeDistribution", getLoanPurposeDistribution());
        
        // EMI Performance
        dashboardData.put("emiPerformance", getEmiPerformance());

        // EMI Delays (for frontend chart)
        dashboardData.put("emiDelays", getEmiDelays());

        // Recent Activities
        dashboardData.put("recentActivities", getRecentActivities());

        // Recent Applications (for frontend table)
        dashboardData.put("recentApplications", getRecentApplicationsForDashboard());

        // NPA Analysis
        dashboardData.put("npaAnalysis", getNpaAnalysis());
        
        return dashboardData;
    }

    // Get all loan applications for admin
    public List<LoanApplicationResponse> getAllApplications() {
        return loanApplicationRepository.findAll()
            .stream()
            .map(application -> {
                // Load approval history for each application
                List<ApprovalHistory> history = approvalHistoryRepository.findByLoanApplicationIdOrderByCreatedAtAsc(application.getId());
                application.setApprovalHistory(history);
                return new LoanApplicationResponse(application);
            })
            .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
            .collect(Collectors.toList());
    }

    // Get all users for admin
    public List<Map<String, Object>> getAllUsers() {
        return userRepository.findAll()
            .stream()
            .map(this::convertUserToMap)
            .collect(Collectors.toList());
    }

    private Map<String, Object> convertUserToMap(User user) {
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("id", user.getId());
        userMap.put("name", user.getName());
        userMap.put("email", user.getEmail());
        userMap.put("role", user.getRole().toString());
        userMap.put("isActive", user.getIsActive());
        userMap.put("createdAt", user.getCreatedAt());
        userMap.put("stage", getRoleStage(user.getRole()));
        return userMap;
    }

    private String getRoleStage(User.Role role) {
        switch (role) {
            case SALES_EXECUTIVE: return "Application Submission";
            case UNDERWRITER: return "RCPU Review";
            case MANAGER_L1: return "L1 Approval";
            case MANAGER_L2: return "L2 Approval";
            case ADMIN: return "L3 Final Approval & Admin";
            default: return "Unknown";
        }
    }
    
    private Map<String, Object> getSummaryStatistics() {
        Map<String, Object> summary = new HashMap<>();
        
        List<LoanApplication> allApplications = loanApplicationRepository.findAll();
        
        summary.put("totalApplications", allApplications.size());
        summary.put("totalApplicationsChange", 12.5); // Mock change percentage
        summary.put("approvedApplications", allApplications.stream()
            .filter(app -> isApproved(app.getStatus()))
            .count());
        summary.put("approvedApplicationsChange", 8.3); // Mock change percentage
        summary.put("rejectedApplications", allApplications.stream()
            .filter(app -> app.getStatus() == LoanApplication.ApplicationStatus.REJECTED)
            .count());
        summary.put("rejectedApplicationsChange", 2.8); // Mock change percentage
        summary.put("pendingApplications", allApplications.stream()
            .filter(app -> isPending(app.getStatus()))
            .count());
        summary.put("pendingApplicationsChange", -5.1); // Mock change percentage
        
        BigDecimal totalLoanAmount = allApplications.stream()
            .filter(app -> isApproved(app.getStatus()))
            .map(LoanApplication::getLoanAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        summary.put("totalLoanAmount", totalLoanAmount);
        summary.put("totalLoanAmountChange", 15.2); // Mock change percentage

        BigDecimal averageLoanAmount = allApplications.stream()
            .filter(app -> isApproved(app.getStatus()))
            .map(LoanApplication::getLoanAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add)
            .divide(BigDecimal.valueOf(Math.max(1, allApplications.stream()
                .filter(app -> isApproved(app.getStatus()))
                .count())), 2, BigDecimal.ROUND_HALF_UP);
        summary.put("averageLoanAmount", averageLoanAmount);
        summary.put("averageLoanAmountChange", 7.5); // Mock change percentage
        
        return summary;
    }
    
    private List<Map<String, Object>> getApplicationStatusDistribution() {
        List<Map<String, Object>> distribution = new ArrayList<>();

        Map<LoanApplication.ApplicationStatus, Long> statusCounts = loanApplicationRepository.findAll()
            .stream()
            .collect(Collectors.groupingBy(
                LoanApplication::getStatus,
                Collectors.counting()
            ));

        // Map status to stage names and colors to match frontend expectations
        String[] colors = {"#3B82F6", "#8B5CF6", "#F59E0B", "#EF4444", "#10B981", "#059669"};
        int colorIndex = 0;

        for (Map.Entry<LoanApplication.ApplicationStatus, Long> entry : statusCounts.entrySet()) {
            Map<String, Object> statusData = new HashMap<>();
            statusData.put("stage", getStageNameFromStatus(entry.getKey()));
            statusData.put("count", entry.getValue());
            statusData.put("color", colors[colorIndex % colors.length]);
            distribution.add(statusData);
            colorIndex++;
        }

        return distribution;
    }

    private String getStageNameFromStatus(LoanApplication.ApplicationStatus status) {
        switch (status) {
            case SUBMITTED: return "Submitted";
            case PENDING_RCPU: return "RCPU";
            case PENDING_L1: return "L1";
            case PENDING_L2: return "L2";
            case PENDING_L3: return "L3";
            case L1_APPROVED:
            case L2_APPROVED:
            case L3_APPROVED: return "Approved";
            case REJECTED: return "Rejected";
            default: return status.toString();
        }
    }

    private String getShortMonthName(String yearMonth) {
        String[] parts = yearMonth.split("-");
        if (parts.length == 2) {
            int month = Integer.parseInt(parts[1]);
            String[] monthNames = {"Jan", "Feb", "Mar", "Apr", "May", "Jun",
                                 "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
            return monthNames[month - 1];
        }
        return yearMonth;
    }

    private String getPurposeName(LoanApplication.LoanPurpose purpose) {
        switch (purpose) {
            case HOME_PURCHASE: return "Home Purchase";
            case HOME_CONSTRUCTION: return "Home Construction";
            case HOME_RENOVATION: return "Home Renovation";
            case PLOT_PURCHASE: return "Plot Purchase";
            default: return purpose.toString();
        }
    }
    
    private List<Map<String, Object>> getMonthlyTrends() {
        List<Map<String, Object>> trends = new ArrayList<>();
        
        LocalDateTime sixMonthsAgo = LocalDateTime.now().minusMonths(6);
        List<LoanApplication> recentApplications = loanApplicationRepository.findAll()
            .stream()
            .filter(app -> app.getCreatedAt().isAfter(sixMonthsAgo))
            .collect(Collectors.toList());
        
        Map<String, List<LoanApplication>> monthlyGroups = recentApplications.stream()
            .collect(Collectors.groupingBy(app -> 
                app.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM"))
            ));
        
        for (Map.Entry<String, List<LoanApplication>> entry : monthlyGroups.entrySet()) {
            Map<String, Object> monthData = new HashMap<>();
            monthData.put("month", getShortMonthName(entry.getKey()));
            monthData.put("applications", entry.getValue().size());
            monthData.put("approved", entry.getValue().stream()
                .filter(app -> isApproved(app.getStatus()))
                .count());
            monthData.put("amount", entry.getValue().stream()
                .filter(app -> isApproved(app.getStatus()))
                .map(LoanApplication::getLoanAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
            trends.add(monthData);
        }
        
        return trends.stream()
            .sorted((a, b) -> ((String) a.get("month")).compareTo((String) b.get("month")))
            .collect(Collectors.toList());
    }
    
    private List<Map<String, Object>> getLoanPurposeDistribution() {
        List<Map<String, Object>> distribution = new ArrayList<>();
        
        Map<LoanApplication.LoanPurpose, Long> purposeCounts = loanApplicationRepository.findAll()
            .stream()
            .filter(app -> isApproved(app.getStatus()))
            .collect(Collectors.groupingBy(
                LoanApplication::getLoanPurpose,
                Collectors.counting()
            ));
        
        String[] colors = {"#3B82F6", "#8B5CF6", "#F59E0B", "#EF4444", "#6B7280"};
        int colorIndex = 0;

        for (Map.Entry<LoanApplication.LoanPurpose, Long> entry : purposeCounts.entrySet()) {
            Map<String, Object> purposeData = new HashMap<>();
            purposeData.put("purpose", getPurposeName(entry.getKey()));
            purposeData.put("value", entry.getValue());
            purposeData.put("color", colors[colorIndex % colors.length]);
            distribution.add(purposeData);
            colorIndex++;
        }
        
        return distribution;
    }
    
    private Map<String, Object> getEmiPerformance() {
        Map<String, Object> performance = new HashMap<>();
        
        List<EmiSchedule> allEmis = emiScheduleRepository.findAll();
        
        long totalEmis = allEmis.size();
        long paidEmis = allEmis.stream()
            .filter(emi -> emi.getStatus() == EmiSchedule.EmiStatus.PAID)
            .count();
        long overdueEmis = allEmis.stream()
            .filter(emi -> emi.getStatus() == EmiSchedule.EmiStatus.OVERDUE)
            .count();
        
        performance.put("totalEmis", totalEmis);
        performance.put("paidEmis", paidEmis);
        performance.put("overdueEmis", overdueEmis);
        performance.put("collectionRate", totalEmis > 0 ? (double) paidEmis / totalEmis * 100 : 0);
        
        BigDecimal totalOutstanding = allEmis.stream()
            .filter(emi -> emi.getStatus() != EmiSchedule.EmiStatus.PAID)
            .map(EmiSchedule::getEmiAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        performance.put("totalOutstanding", totalOutstanding);
        
        return performance;
    }
    
    private List<Map<String, Object>> getRecentActivities() {
        List<Map<String, Object>> activities = new ArrayList<>();
        
        List<LoanApplication> recentApplications = loanApplicationRepository.findAll()
            .stream()
            .sorted((a, b) -> b.getUpdatedAt().compareTo(a.getUpdatedAt()))
            .limit(10)
            .collect(Collectors.toList());
        
        for (LoanApplication app : recentApplications) {
            Map<String, Object> activity = new HashMap<>();
            activity.put("applicationId", app.getApplicationId());
            activity.put("applicantName", app.getFirstName() + " " + app.getLastName());
            activity.put("status", app.getStatus().toString());
            activity.put("loanAmount", app.getLoanAmount());
            activity.put("updatedAt", app.getUpdatedAt());
            activities.add(activity);
        }
        
        return activities;
    }
    
    private Map<String, Object> getNpaAnalysis() {
        Map<String, Object> npaData = new HashMap<>();
        
        LocalDate thirtyDaysAgo = LocalDate.now().minusDays(30);
        LocalDate ninetyDaysAgo = LocalDate.now().minusDays(90);
        
        List<EmiSchedule> overdueEmis = emiScheduleRepository.findOverdueEmis(LocalDate.now());
        
        long npa30 = overdueEmis.stream()
            .filter(emi -> emi.getDueDate().isBefore(thirtyDaysAgo))
            .count();
        
        long npa90 = overdueEmis.stream()
            .filter(emi -> emi.getDueDate().isBefore(ninetyDaysAgo))
            .count();
        
        npaData.put("totalOverdue", overdueEmis.size());
        npaData.put("npa30Days", npa30);
        npaData.put("npa90Days", npa90);
        
        BigDecimal npaAmount = overdueEmis.stream()
            .map(EmiSchedule::getEmiAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        npaData.put("npaAmount", npaAmount);
        
        return npaData;
    }
    
    private boolean isApproved(LoanApplication.ApplicationStatus status) {
        return status == LoanApplication.ApplicationStatus.L1_APPROVED ||
               status == LoanApplication.ApplicationStatus.L2_APPROVED ||
               status == LoanApplication.ApplicationStatus.L3_APPROVED;
    }
    
    private boolean isPending(LoanApplication.ApplicationStatus status) {
        return status == LoanApplication.ApplicationStatus.SUBMITTED ||
               status == LoanApplication.ApplicationStatus.PENDING_RCPU ||
               status == LoanApplication.ApplicationStatus.PENDING_L1 ||
               status == LoanApplication.ApplicationStatus.PENDING_L2 ||
               status == LoanApplication.ApplicationStatus.PENDING_L3;
    }

    private List<Map<String, Object>> getEmiDelays() {
        // Mock EMI delay data for the chart
        List<Map<String, Object>> emiDelays = new ArrayList<>();
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun"};
        int[] onTimeValues = {95, 92, 94, 89, 91, 93};
        int[] delayedValues = {5, 8, 6, 11, 9, 7};

        for (int i = 0; i < months.length; i++) {
            Map<String, Object> monthData = new HashMap<>();
            monthData.put("month", months[i]);
            monthData.put("onTime", onTimeValues[i]);
            monthData.put("delayed", delayedValues[i]);
            emiDelays.add(monthData);
        }

        return emiDelays;
    }

    private List<Map<String, Object>> getRecentApplicationsForDashboard() {
        List<Map<String, Object>> recentApps = new ArrayList<>();

        List<LoanApplication> applications = loanApplicationRepository.findAll()
            .stream()
            .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
            .limit(4)
            .collect(Collectors.toList());

        for (LoanApplication app : applications) {
            Map<String, Object> appData = new HashMap<>();
            appData.put("id", app.getApplicationId());
            appData.put("applicant", app.getFirstName() + " " + app.getLastName());
            appData.put("amount", app.getLoanAmount());
            appData.put("status", app.getStatus().toString());
            appData.put("date", app.getCreatedAt().toLocalDate().toString());
            recentApps.add(appData);
        }

        return recentApps;
    }

    public Map<String, Object> createTestL3Application() {
        // Create a test application with high loan amount that should go to L3
        LoanApplication testApp = new LoanApplication();
        testApp.setApplicationId("LA" + System.currentTimeMillis());
        testApp.setFirstName("Test");
        testApp.setLastName("Applicant");
        testApp.setEmail("test@example.com");
        testApp.setPhoneNumber("9999999999");
        testApp.setDateOfBirth(LocalDate.of(1990, 1, 1));
        testApp.setGender(LoanApplication.Gender.MALE);
        testApp.setMaritalStatus(LoanApplication.MaritalStatus.SINGLE);
        testApp.setPanNumber("ABCDE1234F");
        testApp.setAadhaarNumber("123456789012");
        testApp.setCurrentStreet("Test Street");
        testApp.setCurrentCity("Test City");
        testApp.setCurrentState("Test State");
        testApp.setCurrentPincode("123456");
        testApp.setSameAsCurrent(true);
        testApp.setEmploymentType(LoanApplication.EmploymentType.SALARIED);
        testApp.setEmployer("Test Company");
        testApp.setMonthlyIncome(BigDecimal.valueOf(200000));
        testApp.setWorkExperience(5);
        testApp.setLoanAmount(BigDecimal.valueOf(7500000)); // 75 lakhs - should go to L3
        testApp.setLoanTenure(20);
        testApp.setLoanPurpose(LoanApplication.LoanPurpose.HOME_PURCHASE);
        testApp.setResidenceType(LoanApplication.ResidenceType.OWNED);
        testApp.setHasCoApplicant(false);
        testApp.setStatus(LoanApplication.ApplicationStatus.PENDING_L3);
        testApp.setEligibilityScore(85);
        testApp.setEstimatedEmi(BigDecimal.valueOf(65000));
        testApp.setCreatedAt(LocalDateTime.now());
        testApp.setUpdatedAt(LocalDateTime.now());

        // Find a user to associate with the application
        User submittedBy = userRepository.findByEmail("sales@smartloan.com")
            .orElse(userRepository.findAll().stream().findFirst().orElse(null));
        testApp.setSubmittedBy(submittedBy);

        LoanApplication savedApp = loanApplicationRepository.save(testApp);

        Map<String, Object> result = new HashMap<>();
        result.put("message", "Test L3 application created successfully");
        result.put("applicationId", savedApp.getApplicationId());
        result.put("id", savedApp.getId());
        result.put("loanAmount", savedApp.getLoanAmount());
        result.put("status", savedApp.getStatus().toString());

        return result;
    }
}
