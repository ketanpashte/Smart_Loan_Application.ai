import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import Layout from '../../components/Layout';
import DocumentViewer from '../../components/DocumentViewer';
import { motion } from 'framer-motion';
import api from '../../config/axios';
import {
  DocumentIcon,
  CheckCircleIcon,
  XCircleIcon,
  ArrowLeftIcon,
  ArrowDownTrayIcon,
  EyeIcon
} from '@heroicons/react/24/outline';

const L1Details = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [application, setApplication] = useState(null);
  const [loading, setLoading] = useState(true);
  const [l1Remarks, setL1Remarks] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const [showDocuments, setShowDocuments] = useState(false);

  useEffect(() => {
    // Mock data - replace with API call
    const mockApplication = {
      id: 'LA001',
      applicantName: 'John Doe',
      dateSubmitted: '2024-06-28',
      rcpuCompletedDate: '2024-06-29',
      status: 'PENDING_L1',
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
        remarks: 'All documents verified. Credit score: 750. Employment verification completed. Property valuation pending. Recommended for approval with standard terms.',
        reviewer: 'RCPU Officer - Sarah Wilson'
      },
      eligibilityCheck: {
        score: 85,
        status: 'APPROVED',
        maxLoanAmount: 4500000,
        emiToIncomeRatio: 35.2,
        estimatedEmi: 21500
      }
    };

    setTimeout(() => {
      setApplication(mockApplication);
      setLoading(false);
    }, 1000);
  }, [id]);

  const handleL1Decision = async (decision) => {
    setSubmitting(true);

    try {
      const response = await api.post(`/api/workflow/l1/${id}/decision`, {
        decision: decision
      });

      if (response.status === 200) {
        if (decision === 'approve') {
          alert('Application approved successfully!');
        } else {
          alert('Application rejected successfully!');
        }
        navigate('/l1/index');
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

  if (loading) {
    return (
      <Layout title="L1 Manager Review">
        <div className="flex items-center justify-center h-64">
          <div className="animate-spin rounded-full h-12 w-12 border-4 border-primary-600 border-t-transparent"></div>
        </div>
      </Layout>
    );
  }

  return (
    <Layout title={`L1 Review - ${application.id}`}>
      <div className="space-y-6">
        {/* Header */}
        <div className="flex items-center justify-between">
          <div className="flex items-center space-x-4">
            <button
              onClick={() => navigate('/l1/index')}
              className="p-2 hover:bg-gray-100 rounded-lg"
            >
              <ArrowLeftIcon className="h-5 w-5" />
            </button>
            <div>
              <h1 className="text-2xl font-bold text-gray-900">Application {application.id}</h1>
              <p className="text-gray-600">L1 Manager Review</p>
            </div>
          </div>
          <span className="status-pending">Pending L1 Review</span>
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
                  <span className="text-gray-500">Purpose:</span>
                  <p className="font-medium">{application.financialInfo.loanPurpose.replace('_', ' ')}</p>
                </div>
              </div>
            </div>

            {/* RCPU Report */}
            <div className="card p-6">
              <h3 className="text-lg font-semibold mb-4">RCPU Report</h3>
              <div className="space-y-4">
                <div className="flex items-center justify-between p-4 bg-green-50 border border-green-200 rounded-lg">
                  <div className="flex items-center space-x-3">
                    <DocumentIcon className="h-6 w-6 text-green-600" />
                    <div>
                      <p className="font-medium text-green-900">{application.rcpuReport.fileName}</p>
                      <p className="text-sm text-green-700">
                        Uploaded on {formatDate(application.rcpuReport.uploadedDate)} by {application.rcpuReport.reviewer}
                      </p>
                    </div>
                  </div>
                  <button className="flex items-center space-x-1 text-green-700 hover:text-green-800">
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

            {/* Personal Information */}
            <div className="card p-6">
              <h3 className="text-lg font-semibold mb-4">Personal Information</h3>
              <div className="grid grid-cols-2 gap-4 text-sm">
                <div>
                  <span className="text-gray-500">Name:</span>
                  <p className="font-medium">{application.personalInfo.firstName} {application.personalInfo.lastName}</p>
                </div>
                <div>
                  <span className="text-gray-500">Date of Birth:</span>
                  <p className="font-medium">{formatDate(application.personalInfo.dateOfBirth)}</p>
                </div>
                <div>
                  <span className="text-gray-500">PAN:</span>
                  <p className="font-medium">{application.personalInfo.panNumber}</p>
                </div>
                <div>
                  <span className="text-gray-500">Phone:</span>
                  <p className="font-medium">{application.personalInfo.phoneNumber}</p>
                </div>
                <div>
                  <span className="text-gray-500">Email:</span>
                  <p className="font-medium">{application.personalInfo.email}</p>
                </div>
                <div>
                  <span className="text-gray-500">Employment:</span>
                  <p className="font-medium">{application.financialInfo.employmentType} at {application.financialInfo.employer}</p>
                </div>
              </div>
            </div>

            {/* Documents */}
            <div className="card p-6">
              <div className="flex items-center justify-between mb-4">
                <h3 className="text-lg font-semibold">Submitted Documents</h3>
                <button
                  onClick={() => setShowDocuments(true)}
                  className="inline-flex items-center px-4 py-2 bg-blue-600 text-white text-sm font-medium rounded-md hover:bg-blue-700 transition-colors"
                >
                  <EyeIcon className="h-4 w-4 mr-2" />
                  View All Documents
                </button>
              </div>
              <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
                {Object.entries(application.documents).map(([key, doc]) => (
                  <div key={key} className="flex items-center justify-between p-3 border border-gray-200 rounded-lg">
                    <div className="flex items-center space-x-3">
                      <DocumentIcon className="h-5 w-5 text-gray-400" />
                      <div>
                        <p className="font-medium">{doc.name}</p>
                        <p className="text-sm text-gray-500">{key.toUpperCase()}</p>
                      </div>
                    </div>
                    <CheckCircleIcon className="h-5 w-5 text-green-500" />
                  </div>
                ))}
              </div>
            </div>
          </div>

          {/* L1 Decision Panel */}
          <div className="space-y-6">
            {/* L1 Remarks */}
            <div className="card p-6">
              <h3 className="text-lg font-semibold mb-4">L1 Manager Decision</h3>
              
              <div className="space-y-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    L1 Remarks *
                  </label>
                  <textarea
                    rows={6}
                    className="input-field"
                    placeholder="Enter your remarks and decision rationale..."
                    value={l1Remarks}
                    onChange={(e) => setL1Remarks(e.target.value)}
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
                  onClick={() => handleL1Decision('approve')}
                  disabled={!l1Remarks || submitting}
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
                      Approve & Forward to L2
                    </>
                  )}
                </motion.button>
                
                <motion.button
                  whileHover={{ scale: 1.02 }}
                  whileTap={{ scale: 0.98 }}
                  onClick={() => handleL1Decision('reject')}
                  disabled={!l1Remarks || submitting}
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
                  <div className="w-3 h-3 bg-yellow-500 rounded-full"></div>
                  <div>
                    <p className="text-sm font-medium">L1 Review (Current)</p>
                    <p className="text-xs text-gray-500">In Progress</p>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Document Viewer Modal */}
      <DocumentViewer
        applicationId={id}
        isOpen={showDocuments}
        onClose={() => setShowDocuments(false)}
        readOnly={true}
      />
    </Layout>
  );
};

export default L1Details;
