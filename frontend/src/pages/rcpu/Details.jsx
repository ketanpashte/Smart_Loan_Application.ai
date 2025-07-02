import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import Layout from '../../components/Layout';
import { motion } from 'framer-motion';
import api from '../../config/axios';
import {
  DocumentIcon,
  ArrowUpTrayIcon,
  CheckCircleIcon,
  XCircleIcon,
  ArrowLeftIcon
} from '@heroicons/react/24/outline';

const RCPUDetails = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [application, setApplication] = useState(null);
  const [loading, setLoading] = useState(true);
  const [rcpuFile, setRcpuFile] = useState(null);
  const [rcpuRemarks, setRcpuRemarks] = useState('');
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    // Mock data - replace with API call
    const mockApplication = {
      id: 'LA001',
      applicantName: 'John Doe',
      dateSubmitted: '2024-06-28',
      status: 'PENDING_RCPU',
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
      }
    };

    setTimeout(() => {
      setApplication(mockApplication);
      setLoading(false);
    }, 1000);
  }, [id]);

  const handleRcpuSubmit = async (action) => {
    setSubmitting(true);

    try {
      const response = await api.post(`/api/workflow/rcpu/${id}/decision`, {
        decision: action
      });

      if (response.status === 200) {
        if (action === 'approve') {
          alert('Application forwarded to L1 Manager successfully!');
        } else {
          alert('Application rejected successfully!');
        }
        navigate('/rcpu/index');
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

  if (loading) {
    return (
      <Layout title="RCPU Review">
        <div className="flex items-center justify-center h-64">
          <div className="animate-spin rounded-full h-12 w-12 border-4 border-primary-600 border-t-transparent"></div>
        </div>
      </Layout>
    );
  }

  return (
    <Layout title={`RCPU Review - ${application.id}`}>
      <div className="space-y-6">
        {/* Header */}
        <div className="flex items-center justify-between">
          <div className="flex items-center space-x-4">
            <button
              onClick={() => navigate('/rcpu/index')}
              className="p-2 hover:bg-gray-100 rounded-lg"
            >
              <ArrowLeftIcon className="h-5 w-5" />
            </button>
            <div>
              <h1 className="text-2xl font-bold text-gray-900">Application {application.id}</h1>
              <p className="text-gray-600">RCPU Review and Processing</p>
            </div>
          </div>
          <span className="status-pending">Pending RCPU</span>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          {/* Main Content */}
          <div className="lg:col-span-2 space-y-6">
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
                  <p className="font-medium">{new Date(application.personalInfo.dateOfBirth).toLocaleDateString()}</p>
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
                  <span className="text-gray-500">Gender:</span>
                  <p className="font-medium">{application.personalInfo.gender}</p>
                </div>
              </div>
            </div>

            {/* Financial Information */}
            <div className="card p-6">
              <h3 className="text-lg font-semibold mb-4">Financial Information</h3>
              <div className="grid grid-cols-2 gap-4 text-sm">
                <div>
                  <span className="text-gray-500">Employment:</span>
                  <p className="font-medium">{application.financialInfo.employmentType}</p>
                </div>
                <div>
                  <span className="text-gray-500">Monthly Income:</span>
                  <p className="font-medium">₹{parseInt(application.financialInfo.monthlyIncome).toLocaleString()}</p>
                </div>
                <div>
                  <span className="text-gray-500">Employer:</span>
                  <p className="font-medium">{application.financialInfo.employer}</p>
                </div>
                <div>
                  <span className="text-gray-500">Experience:</span>
                  <p className="font-medium">{application.financialInfo.workExperience} years</p>
                </div>
                <div>
                  <span className="text-gray-500">Loan Amount:</span>
                  <p className="font-medium">{formatCurrency(application.financialInfo.loanAmount)}</p>
                </div>
                <div>
                  <span className="text-gray-500">Tenure:</span>
                  <p className="font-medium">{application.financialInfo.loanTenure} years</p>
                </div>
              </div>
            </div>

            {/* Documents */}
            <div className="card p-6">
              <h3 className="text-lg font-semibold mb-4">Submitted Documents</h3>
              <div className="space-y-3">
                {Object.entries(application.documents).map(([key, doc]) => (
                  <div key={key} className="flex items-center justify-between p-3 border border-gray-200 rounded-lg">
                    <div className="flex items-center space-x-3">
                      <DocumentIcon className="h-5 w-5 text-gray-400" />
                      <div>
                        <p className="font-medium">{doc.name}</p>
                        <p className="text-sm text-gray-500">{key.toUpperCase()}</p>
                      </div>
                    </div>
                    <div className="flex items-center space-x-2">
                      <CheckCircleIcon className="h-5 w-5 text-green-500" />
                      <button className="text-primary-600 hover:text-primary-700 text-sm font-medium">
                        View
                      </button>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          </div>

          {/* RCPU Actions */}
          <div className="space-y-6">
            {/* RCPU Report Upload */}
            <div className="card p-6">
              <h3 className="text-lg font-semibold mb-4">RCPU Report</h3>
              
              <div className="space-y-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Upload RCPU Report *
                  </label>
                  <div className="border-2 border-dashed border-gray-300 rounded-lg p-4 text-center">
                    <input
                      type="file"
                      accept=".pdf"
                      onChange={(e) => setRcpuFile(e.target.files[0])}
                      className="hidden"
                      id="rcpu-upload"
                    />
                    <label htmlFor="rcpu-upload" className="cursor-pointer">
                      <ArrowUpTrayIcon className="mx-auto h-8 w-8 text-gray-400 mb-2" />
                      <p className="text-sm text-gray-600">
                        {rcpuFile ? rcpuFile.name : 'Click to upload RCPU report (PDF only)'}
                      </p>
                    </label>
                  </div>
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    RCPU Remarks *
                  </label>
                  <textarea
                    rows={4}
                    className="input-field"
                    placeholder="Enter your remarks about the application..."
                    value={rcpuRemarks}
                    onChange={(e) => setRcpuRemarks(e.target.value)}
                  />
                </div>
              </div>
            </div>

            {/* Actions */}
            <div className="card p-6">
              <h3 className="text-lg font-semibold mb-4">Actions</h3>
              <div className="space-y-3">
                <motion.button
                  whileHover={{ scale: 1.02 }}
                  whileTap={{ scale: 0.98 }}
                  onClick={() => handleRcpuSubmit('approve')}
                  disabled={!rcpuFile || !rcpuRemarks || submitting}
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
                      Forward to L1 Manager
                    </>
                  )}
                </motion.button>
                
                <motion.button
                  whileHover={{ scale: 1.02 }}
                  whileTap={{ scale: 0.98 }}
                  onClick={() => handleRcpuSubmit('reject')}
                  disabled={!rcpuRemarks || submitting}
                  className="w-full btn-danger disabled:opacity-50 disabled:cursor-not-allowed"
                >
                  <XCircleIcon className="h-4 w-4 mr-2" />
                  Reject Application
                </motion.button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </Layout>
  );
};

export default RCPUDetails;
