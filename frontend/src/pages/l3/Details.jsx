import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import Layout from '../../components/Layout';
import { motion } from 'framer-motion';
import api from '../../config/axios';
import {
  DocumentIcon,
  CheckCircleIcon,
  XCircleIcon,
  ArrowLeftIcon,
  ArrowDownTrayIcon,
  ShieldCheckIcon,
  EnvelopeIcon,
  PhoneIcon
} from '@heroicons/react/24/outline';

const L3Details = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [application, setApplication] = useState(null);
  const [loading, setLoading] = useState(true);
  const [l3Remarks, setL3Remarks] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const [generatingPdf, setGeneratingPdf] = useState(false);

  useEffect(() => {
    // Mock data - replace with API call
    const mockApplication = {
      id: 'LA001',
      applicantName: 'John Doe',
      dateSubmitted: '2024-06-28',
      rcpuCompletedDate: '2024-06-29',
      l1ApprovedDate: '2024-06-30',
      l2ApprovedDate: '2024-07-01',
      status: 'PENDING_L3',
      personalInfo: {
        firstName: 'John',
        lastName: 'Doe',
        dateOfBirth: '1985-05-15',
        gender: 'MALE',
        panNumber: 'ABCDE1234F',
        aadhaarNumber: '123456789012',
        phoneNumber: '9876543210',
        email: 'john.doe@email.com'
      },
      addressInfo: {
        currentAddress: {
          street: '123 Main Street',
          city: 'Mumbai',
          state: 'Maharashtra',
          pincode: '400001',
          residenceType: 'OWNED'
        }
      },
      financialInfo: {
        employmentType: 'SALARIED',
        monthlyIncome: '75000',
        employer: 'Tech Corp Ltd',
        workExperience: '5',
        loanAmount: '2500000',
        loanPurpose: 'HOME_PURCHASE',
        loanTenure: '20'
      },
      documents: {
        itr: { name: 'ITR_2023.pdf', uploaded: true },
        bankStatement: { name: 'Bank_Statement.pdf', uploaded: true },
        aadhaar: { name: 'Aadhaar.pdf', uploaded: true },
        pan: { name: 'PAN.pdf', uploaded: true },
        photo: { name: 'Photo.jpg', uploaded: true }
      },
      rcpuReport: {
        fileName: 'RCPU_Report_LA001.pdf',
        uploadedDate: '2024-06-29',
        remarks: 'All documents verified. Credit score: 750. Employment verification completed. Property valuation: ₹35,00,000. Recommended for approval with standard terms.',
        reviewer: 'RCPU Officer - Sarah Wilson'
      },
      l1Report: {
        approvedDate: '2024-06-30',
        remarks: 'Application thoroughly reviewed. All documents verified. RCPU report positive. Customer has good credit history and stable employment. Property value adequate. Recommended for L2 approval.',
        reviewer: 'L1 Manager - David Kumar'
      },
      l2Report: {
        approvedDate: '2024-07-01',
        remarks: 'High-quality application. All verifications complete. Risk assessment: LOW. Customer profile excellent. Recommended for final approval with standard interest rate.',
        reviewer: 'L2 Senior Manager - Priya Sharma'
      },
      eligibilityCheck: {
        score: 85,
        status: 'APPROVED',
        maxLoanAmount: 4500000,
        emiToIncomeRatio: 35.2,
        estimatedEmi: 21500
      },
      riskAssessment: {
        category: 'LOW',
        factors: {
          creditScore: 750,
          employmentStability: 'HIGH',
          incomeVerification: 'VERIFIED',
          propertyValuation: 'ADEQUATE',
          debtToIncomeRatio: 35.2
        }
      },
      loanTerms: {
        approvedAmount: 2500000,
        interestRate: 8.5,
        tenure: 20,
        emi: 21500,
        processingFee: 25000,
        totalInterest: 2660000,
        totalAmount: 5160000
      }
    };

    setTimeout(() => {
      setApplication(mockApplication);
      setLoading(false);
    }, 1000);
  }, [id]);

  const handleL3Decision = async (decision) => {
    setSubmitting(true);

    try {
      const response = await api.post(`/api/workflow/l3/${id}/decision`, {
        decision: decision
      });

      if (response.status === 200) {
        if (decision === 'approve') {
          alert('Application approved successfully! Offer letter will be generated.');
        } else {
          alert('Application rejected successfully!');
        }
        navigate('/l3/index');
      }
    } catch (error) {
      console.error('Error processing application:', error);

      let errorMessage = 'Error processing application';

      if (error.response) {
        const status = error.response.status;
        const data = error.response.data;

        switch (status) {
          case 401:
            errorMessage = 'You are not authorized. Please login again.';
            window.location.href = '/login';
            break;
          case 403:
            errorMessage = 'You do not have permission to process this application.';
            break;
          case 404:
            errorMessage = 'Application not found.';
            break;
          case 400:
            errorMessage = data.message || 'Invalid request data.';
            break;
          case 500:
            errorMessage = 'Server error. Please try again later.';
            break;
          default:
            errorMessage = data.message || `Error: ${status}`;
        }
      } else if (error.request) {
        errorMessage = 'Network error. Please check your connection and try again.';
      }

      alert(errorMessage);
    } finally {
      setSubmitting(false);
    }
  };

  const handleGeneratePDF = async () => {
    setGeneratingPdf(true);
    
    try {
      // Simulate PDF generation
      await new Promise(resolve => setTimeout(resolve, 3000));
      
      // Create a mock PDF download
      const pdfContent = `
        LOAN OFFER LETTER
        
        Application ID: ${application.id}
        Applicant: ${application.personalInfo.firstName} ${application.personalInfo.lastName}
        
        Loan Details:
        - Amount: ₹${application.loanTerms.approvedAmount.toLocaleString()}
        - Interest Rate: ${application.loanTerms.interestRate}% p.a.
        - Tenure: ${application.loanTerms.tenure} years
        - EMI: ₹${application.loanTerms.emi.toLocaleString()}
        
        This is a mock PDF for demonstration purposes.
      `;
      
      const blob = new Blob([pdfContent], { type: 'text/plain' });
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `Loan_Offer_Letter_${application.id}.txt`;
      document.body.appendChild(a);
      a.click();
      window.URL.revokeObjectURL(url);
      document.body.removeChild(a);
      
      alert('Loan offer letter generated and downloaded successfully!');
    } catch (error) {
      alert('Error generating PDF');
    } finally {
      setGeneratingPdf(false);
    }
  };

  const formatCurrency = (amount) => {
    return new Intl.NumberFormat('en-IN', {
      style: 'currency',
      currency: 'INR',
      maximumFractionDigits: 0
    }).format(amount);
  };

  const formatDate = (dateString) => {
    return new Date(dateString).toLocaleDateString('en-IN', {
      year: 'numeric',
      month: 'short',
      day: 'numeric'
    });
  };

  if (loading) {
    return (
      <Layout title="L3 Admin Final Approval">
        <div className="flex items-center justify-center h-64">
          <div className="animate-spin rounded-full h-12 w-12 border-4 border-primary-600 border-t-transparent"></div>
        </div>
      </Layout>
    );
  }

  return (
    <Layout title={`L3 Final Approval - ${application.id}`}>
      <div className="space-y-6">
        {/* Header */}
        <div className="flex items-center justify-between">
          <div className="flex items-center space-x-4">
            <button
              onClick={() => navigate('/l3/index')}
              className="p-2 hover:bg-gray-100 rounded-lg"
            >
              <ArrowLeftIcon className="h-5 w-5" />
            </button>
            <div>
              <h1 className="text-2xl font-bold text-gray-900">Application {application.id}</h1>
              <p className="text-gray-600">L3 Admin Final Approval</p>
            </div>
          </div>
          <div className="flex items-center space-x-3">
            <span className="px-3 py-1 rounded-full text-sm font-medium bg-green-100 text-green-800">
              LOW Risk
            </span>
            <span className="status-pending">Pending L3 Final Approval</span>
          </div>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          {/* Main Content */}
          <div className="lg:col-span-2 space-y-6">
            {/* Loan Terms Summary */}
            <div className="card p-6 bg-gradient-to-r from-green-50 to-blue-50 border-green-200">
              <h3 className="text-lg font-semibold mb-4 text-green-800">Proposed Loan Terms</h3>
              <div className="grid grid-cols-2 md:grid-cols-4 gap-4 text-sm">
                <div>
                  <span className="text-green-600">Approved Amount:</span>
                  <p className="font-bold text-lg text-green-800">{formatCurrency(application.loanTerms.approvedAmount)}</p>
                </div>
                <div>
                  <span className="text-green-600">Interest Rate:</span>
                  <p className="font-bold text-lg text-green-800">{application.loanTerms.interestRate}% p.a.</p>
                </div>
                <div>
                  <span className="text-green-600">Tenure:</span>
                  <p className="font-bold text-lg text-green-800">{application.loanTerms.tenure} years</p>
                </div>
                <div>
                  <span className="text-green-600">Monthly EMI:</span>
                  <p className="font-bold text-lg text-green-800">₹{application.loanTerms.emi.toLocaleString()}</p>
                </div>
              </div>
              <div className="mt-4 pt-4 border-t border-green-200 grid grid-cols-3 gap-4 text-sm">
                <div>
                  <span className="text-green-600">Processing Fee:</span>
                  <p className="font-medium">{formatCurrency(application.loanTerms.processingFee)}</p>
                </div>
                <div>
                  <span className="text-green-600">Total Interest:</span>
                  <p className="font-medium">{formatCurrency(application.loanTerms.totalInterest)}</p>
                </div>
                <div>
                  <span className="text-green-600">Total Amount:</span>
                  <p className="font-medium">{formatCurrency(application.loanTerms.totalAmount)}</p>
                </div>
              </div>
            </div>

            {/* Application Summary */}
            <div className="card p-6">
              <h3 className="text-lg font-semibold mb-4">Application Summary</h3>
              <div className="grid grid-cols-2 md:grid-cols-4 gap-4 text-sm">
                <div>
                  <span className="text-gray-500">Applicant:</span>
                  <p className="font-medium">{application.personalInfo.firstName} {application.personalInfo.lastName}</p>
                </div>
                <div>
                  <span className="text-gray-500">Requested Amount:</span>
                  <p className="font-medium">{formatCurrency(application.financialInfo.loanAmount)}</p>
                </div>
                <div>
                  <span className="text-gray-500">Monthly Income:</span>
                  <p className="font-medium">₹{parseInt(application.financialInfo.monthlyIncome).toLocaleString()}</p>
                </div>
                <div>
                  <span className="text-gray-500">Eligibility Score:</span>
                  <p className="font-medium text-green-600">{application.eligibilityCheck.score}/100</p>
                </div>
              </div>
            </div>

            {/* Approval History */}
            <div className="card p-6">
              <h3 className="text-lg font-semibold mb-4">Approval History</h3>
              <div className="space-y-4">
                {/* L2 Report */}
                <div className="border border-green-200 rounded-lg p-4 bg-green-50">
                  <div className="flex items-center justify-between mb-2">
                    <div className="flex items-center space-x-2">
                      <CheckCircleIcon className="h-5 w-5 text-green-600" />
                      <span className="font-medium text-green-900">L2 Senior Manager Approved</span>
                    </div>
                    <span className="text-sm text-green-700">{formatDate(application.l2Report.approvedDate)}</span>
                  </div>
                  <p className="text-sm text-green-800 ml-7">{application.l2Report.remarks}</p>
                  <p className="text-xs text-green-600 ml-7 mt-1">- {application.l2Report.reviewer}</p>
                </div>

                {/* L1 Report */}
                <div className="border border-blue-200 rounded-lg p-4 bg-blue-50">
                  <div className="flex items-center justify-between mb-2">
                    <div className="flex items-center space-x-2">
                      <CheckCircleIcon className="h-5 w-5 text-blue-600" />
                      <span className="font-medium text-blue-900">L1 Manager Approved</span>
                    </div>
                    <span className="text-sm text-blue-700">{formatDate(application.l1Report.approvedDate)}</span>
                  </div>
                  <p className="text-sm text-blue-800 ml-7">{application.l1Report.remarks}</p>
                  <p className="text-xs text-blue-600 ml-7 mt-1">- {application.l1Report.reviewer}</p>
                </div>

                {/* RCPU Report */}
                <div className="border border-purple-200 rounded-lg p-4 bg-purple-50">
                  <div className="flex items-center justify-between mb-2">
                    <div className="flex items-center space-x-2">
                      <DocumentIcon className="h-5 w-5 text-purple-600" />
                      <span className="font-medium text-purple-900">RCPU Completed</span>
                    </div>
                    <span className="text-sm text-purple-700">{formatDate(application.rcpuReport.uploadedDate)}</span>
                  </div>
                  <p className="text-sm text-purple-800 ml-7">{application.rcpuReport.remarks}</p>
                  <p className="text-xs text-purple-600 ml-7 mt-1">- {application.rcpuReport.reviewer}</p>
                </div>
              </div>
            </div>

            {/* Risk Assessment */}
            <div className="card p-6">
              <h3 className="text-lg font-semibold mb-4 flex items-center">
                <ShieldCheckIcon className="h-5 w-5 mr-2" />
                Final Risk Assessment
              </h3>
              <div className="grid grid-cols-2 md:grid-cols-3 gap-4 text-sm">
                <div>
                  <span className="text-gray-500">Credit Score:</span>
                  <p className="font-medium text-green-600">{application.riskAssessment.factors.creditScore}</p>
                </div>
                <div>
                  <span className="text-gray-500">Employment Stability:</span>
                  <p className="font-medium">{application.riskAssessment.factors.employmentStability}</p>
                </div>
                <div>
                  <span className="text-gray-500">Income Verification:</span>
                  <p className="font-medium">{application.riskAssessment.factors.incomeVerification}</p>
                </div>
                <div>
                  <span className="text-gray-500">Property Valuation:</span>
                  <p className="font-medium">{application.riskAssessment.factors.propertyValuation}</p>
                </div>
                <div>
                  <span className="text-gray-500">Debt-to-Income:</span>
                  <p className="font-medium">{application.riskAssessment.factors.debtToIncomeRatio}%</p>
                </div>
                <div>
                  <span className="text-gray-500">Overall Risk:</span>
                  <p className="font-medium text-green-600">{application.riskAssessment.category}</p>
                </div>
              </div>
            </div>
          </div>

          {/* L3 Decision Panel */}
          <div className="space-y-6">
            {/* PDF Generation */}
            <div className="card p-6">
              <h3 className="text-lg font-semibold mb-4">Loan Offer Letter</h3>
              
              <div className="space-y-4">
                <motion.button
                  whileHover={{ scale: 1.02 }}
                  whileTap={{ scale: 0.98 }}
                  onClick={handleGeneratePDF}
                  disabled={generatingPdf}
                  className="w-full btn-primary disabled:opacity-50 disabled:cursor-not-allowed"
                >
                  {generatingPdf ? (
                    <div className="flex items-center justify-center">
                      <div className="animate-spin rounded-full h-4 w-4 border-2 border-white border-t-transparent mr-2"></div>
                      Generating PDF...
                    </div>
                  ) : (
                    <>
                      <ArrowDownTrayIcon className="h-4 w-4 mr-2" />
                      Generate Offer Letter PDF
                    </>
                  )}
                </motion.button>
                
                <div className="text-sm text-gray-600">
                  <p>• Loan terms and conditions</p>
                  <p>• EMI schedule</p>
                  <p>• Legal terms and signatures</p>
                </div>
              </div>
            </div>

            {/* L3 Remarks */}
            <div className="card p-6">
              <h3 className="text-lg font-semibold mb-4">L3 Admin Decision</h3>
              
              <div className="space-y-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    L3 Final Remarks *
                  </label>
                  <textarea
                    rows={6}
                    className="input-field"
                    placeholder="Enter your final remarks and decision rationale..."
                    value={l3Remarks}
                    onChange={(e) => setL3Remarks(e.target.value)}
                  />
                </div>
              </div>
            </div>

            {/* Decision Actions */}
            <div className="card p-6">
              <h3 className="text-lg font-semibold mb-4">Final Actions</h3>
              <div className="space-y-3">
                <motion.button
                  whileHover={{ scale: 1.02 }}
                  whileTap={{ scale: 0.98 }}
                  onClick={() => handleL3Decision('approve')}
                  disabled={!l3Remarks || submitting}
                  className="w-full btn-success disabled:opacity-50 disabled:cursor-not-allowed"
                >
                  {submitting ? (
                    <div className="flex items-center justify-center">
                      <div className="animate-spin rounded-full h-4 w-4 border-2 border-white border-t-transparent mr-2"></div>
                      Processing...
                    </div>
                  ) : (
                    <>
                      <CheckCircleIcon className="h-4 w-4 mr-2" />
                      Final Approve & Send Offer
                    </>
                  )}
                </motion.button>
                
                <motion.button
                  whileHover={{ scale: 1.02 }}
                  whileTap={{ scale: 0.98 }}
                  onClick={() => handleL3Decision('reject')}
                  disabled={!l3Remarks || submitting}
                  className="w-full btn-danger disabled:opacity-50 disabled:cursor-not-allowed"
                >
                  <XCircleIcon className="h-4 w-4 mr-2" />
                  Reject Application
                </motion.button>
              </div>
            </div>

            {/* Communication Actions */}
            <div className="card p-6">
              <h3 className="text-lg font-semibold mb-4">Communication</h3>
              <div className="space-y-3">
                <button className="w-full btn-secondary">
                  <EnvelopeIcon className="h-4 w-4 mr-2" />
                  Send Email Notification
                </button>
                <button className="w-full btn-secondary">
                  <PhoneIcon className="h-4 w-4 mr-2" />
                  Schedule Call
                </button>
              </div>
            </div>

            {/* Application Timeline */}
            <div className="card p-6">
              <h3 className="text-lg font-semibold mb-4">Application Timeline</h3>
              <div className="space-y-3">
                <div className="flex items-center space-x-3">
                  <div className="w-3 h-3 bg-green-500 rounded-full"></div>
                  <div>
                    <p className="text-sm font-medium">Application Submitted</p>
                    <p className="text-xs text-gray-500">{formatDate(application.dateSubmitted)}</p>
                  </div>
                </div>
                <div className="flex items-center space-x-3">
                  <div className="w-3 h-3 bg-green-500 rounded-full"></div>
                  <div>
                    <p className="text-sm font-medium">RCPU Completed</p>
                    <p className="text-xs text-gray-500">{formatDate(application.rcpuCompletedDate)}</p>
                  </div>
                </div>
                <div className="flex items-center space-x-3">
                  <div className="w-3 h-3 bg-green-500 rounded-full"></div>
                  <div>
                    <p className="text-sm font-medium">L1 Approved</p>
                    <p className="text-xs text-gray-500">{formatDate(application.l1ApprovedDate)}</p>
                  </div>
                </div>
                <div className="flex items-center space-x-3">
                  <div className="w-3 h-3 bg-green-500 rounded-full"></div>
                  <div>
                    <p className="text-sm font-medium">L2 Approved</p>
                    <p className="text-xs text-gray-500">{formatDate(application.l2ApprovedDate)}</p>
                  </div>
                </div>
                <div className="flex items-center space-x-3">
                  <div className="w-3 h-3 bg-yellow-500 rounded-full"></div>
                  <div>
                    <p className="text-sm font-medium">L3 Review (Current)</p>
                    <p className="text-xs text-gray-500">In Progress</p>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </Layout>
  );
};

export default L3Details;
