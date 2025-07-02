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
  ShieldCheckIcon
} from '@heroicons/react/24/outline';

const L2Details = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [application, setApplication] = useState(null);
  const [loading, setLoading] = useState(true);
  const [l2Remarks, setL2Remarks] = useState('');
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    // Mock data - replace with API call
    const mockApplication = {
      id: 'LA001',
      applicantName: 'John Doe',
      dateSubmitted: '2024-06-28',
      rcpuCompletedDate: '2024-06-29',
      l1ApprovedDate: '2024-06-30',
      status: 'PENDING_L2',
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
      }
    };

    setTimeout(() => {
      setApplication(mockApplication);
      setLoading(false);
    }, 1000);
  }, [id]);

  const handleL2Decision = async (decision) => {
    setSubmitting(true);

    try {
      const response = await api.post(`/api/workflow/l2/${id}/decision`, {
        decision: decision
      });

      if (response.status === 200) {
        if (decision === 'approve') {
          alert('Application approved successfully!');
        } else {
          alert('Application rejected successfully!');
        }
        navigate('/l2/index');
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

  const getRiskColor = (category) => {
    switch (category) {
      case 'LOW': return 'text-green-600 bg-green-100';
      case 'MEDIUM': return 'text-yellow-600 bg-yellow-100';
      case 'HIGH': return 'text-red-600 bg-red-100';
      default: return 'text-gray-600 bg-gray-100';
    }
  };

  if (loading) {
    return (
      <Layout title="L2 Senior Manager Review">
        <div className="flex items-center justify-center h-64">
          <div className="animate-spin rounded-full h-12 w-12 border-4 border-primary-600 border-t-transparent"></div>
        </div>
      </Layout>
    );
  }

  return (
    <Layout title={`L2 Review - ${application.id}`}>
      <div className="space-y-6">
        {/* Header */}
        <div className="flex items-center justify-between">
          <div className="flex items-center space-x-4">
            <button
              onClick={() => navigate('/l2/index')}
              className="p-2 hover:bg-gray-100 rounded-lg"
            >
              <ArrowLeftIcon className="h-5 w-5" />
            </button>
            <div>
              <h1 className="text-2xl font-bold text-gray-900">Application {application.id}</h1>
              <p className="text-gray-600">L2 Senior Manager Review</p>
            </div>
          </div>
          <div className="flex items-center space-x-3">
            <span className={`px-3 py-1 rounded-full text-sm font-medium ${getRiskColor(application.riskAssessment.category)}`}>
              {application.riskAssessment.category} Risk
            </span>
            <span className="status-pending">Pending L2 Review</span>
          </div>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          {/* Main Content */}
          <div className="lg:col-span-2 space-y-6">
            {/* Application Summary */}
            <div className="card p-6">
              <h3 className="text-lg font-semibold mb-4">Application Summary</h3>
              <div className="grid grid-cols-2 md:grid-cols-4 gap-4 text-sm">
                <div>
                  <span className="text-gray-500">Applicant:</span>
                  <p className="font-medium">{application.personalInfo.firstName} {application.personalInfo.lastName}</p>
                </div>
                <div>
                  <span className="text-gray-500">Loan Amount:</span>
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

            {/* Risk Assessment */}
            <div className="card p-6">
              <h3 className="text-lg font-semibold mb-4 flex items-center">
                <ShieldCheckIcon className="h-5 w-5 mr-2" />
                Risk Assessment
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
                  <p className={`font-medium ${application.riskAssessment.category === 'LOW' ? 'text-green-600' : application.riskAssessment.category === 'MEDIUM' ? 'text-yellow-600' : 'text-red-600'}`}>
                    {application.riskAssessment.category}
                  </p>
                </div>
              </div>
            </div>

            {/* L1 Manager Report */}
            <div className="card p-6">
              <h3 className="text-lg font-semibold mb-4">L1 Manager Report</h3>
              <div className="space-y-4">
                <div className="flex items-center justify-between p-4 bg-green-50 border border-green-200 rounded-lg">
                  <div className="flex items-center space-x-3">
                    <CheckCircleIcon className="h-6 w-6 text-green-600" />
                    <div>
                      <p className="font-medium text-green-900">L1 Approved</p>
                      <p className="text-sm text-green-700">
                        Approved on {formatDate(application.l1Report.approvedDate)} by {application.l1Report.reviewer}
                      </p>
                    </div>
                  </div>
                </div>
                
                <div>
                  <h4 className="font-medium text-gray-900 mb-2">L1 Manager Remarks:</h4>
                  <p className="text-gray-700 bg-gray-50 p-4 rounded-lg">{application.l1Report.remarks}</p>
                </div>
              </div>
            </div>

            {/* RCPU Report */}
            <div className="card p-6">
              <h3 className="text-lg font-semibold mb-4">RCPU Report</h3>
              <div className="space-y-4">
                <div className="flex items-center justify-between p-4 bg-blue-50 border border-blue-200 rounded-lg">
                  <div className="flex items-center space-x-3">
                    <DocumentIcon className="h-6 w-6 text-blue-600" />
                    <div>
                      <p className="font-medium text-blue-900">{application.rcpuReport.fileName}</p>
                      <p className="text-sm text-blue-700">
                        Uploaded on {formatDate(application.rcpuReport.uploadedDate)} by {application.rcpuReport.reviewer}
                      </p>
                    </div>
                  </div>
                  <button className="flex items-center space-x-1 text-blue-700 hover:text-blue-800">
                    <ArrowDownTrayIcon className="h-4 w-4" />
                    <span className="text-sm font-medium">Download</span>
                  </button>
                </div>
                
                <div>
                  <h4 className="font-medium text-gray-900 mb-2">RCPU Remarks:</h4>
                  <p className="text-gray-700 bg-gray-50 p-4 rounded-lg">{application.rcpuReport.remarks}</p>
                </div>
              </div>
            </div>

            {/* Eligibility Analysis */}
            <div className="card p-6">
              <h3 className="text-lg font-semibold mb-4">Eligibility Analysis</h3>
              <div className="grid grid-cols-2 md:grid-cols-4 gap-4 text-sm">
                <div>
                  <span className="text-gray-500">Eligibility Score:</span>
                  <p className="font-medium text-green-600">{application.eligibilityCheck.score}/100</p>
                </div>
                <div>
                  <span className="text-gray-500">Max Loan Amount:</span>
                  <p className="font-medium">{formatCurrency(application.eligibilityCheck.maxLoanAmount)}</p>
                </div>
                <div>
                  <span className="text-gray-500">EMI/Income Ratio:</span>
                  <p className="font-medium">{application.eligibilityCheck.emiToIncomeRatio}%</p>
                </div>
                <div>
                  <span className="text-gray-500">Estimated EMI:</span>
                  <p className="font-medium">₹{application.eligibilityCheck.estimatedEmi.toLocaleString()}</p>
                </div>
              </div>
            </div>
          </div>

          {/* L2 Decision Panel */}
          <div className="space-y-6">
            {/* L2 Remarks */}
            <div className="card p-6">
              <h3 className="text-lg font-semibold mb-4">L2 Senior Manager Decision</h3>
              
              <div className="space-y-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    L2 Remarks *
                  </label>
                  <textarea
                    rows={6}
                    className="input-field"
                    placeholder="Enter your remarks and decision rationale..."
                    value={l2Remarks}
                    onChange={(e) => setL2Remarks(e.target.value)}
                  />
                </div>
              </div>
            </div>

            {/* Decision Actions */}
            <div className="card p-6">
              <h3 className="text-lg font-semibold mb-4">Actions</h3>
              <div className="space-y-3">
                <motion.button
                  whileHover={{ scale: 1.02 }}
                  whileTap={{ scale: 0.98 }}
                  onClick={() => handleL2Decision('approve')}
                  disabled={!l2Remarks || submitting}
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
                      Approve & Forward to L3
                    </>
                  )}
                </motion.button>
                
                <motion.button
                  whileHover={{ scale: 1.02 }}
                  whileTap={{ scale: 0.98 }}
                  onClick={() => handleL2Decision('reject')}
                  disabled={!l2Remarks || submitting}
                  className="w-full btn-danger disabled:opacity-50 disabled:cursor-not-allowed"
                >
                  <XCircleIcon className="h-4 w-4 mr-2" />
                  Reject Application
                </motion.button>
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
                  <div className="w-3 h-3 bg-yellow-500 rounded-full"></div>
                  <div>
                    <p className="text-sm font-medium">L2 Review (Current)</p>
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

export default L2Details;
