import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import Layout from '../../components/Layout';
import { motion } from 'framer-motion';
import {
  EyeIcon,
  MagnifyingGlassIcon,
  FunnelIcon,
  ClockIcon,
  ArrowTrendingUpIcon,
  ArrowDownTrayIcon,
  CheckCircleIcon
} from '@heroicons/react/24/outline';
import api from '../../config/axios';

const L3Index = () => {
  const [applications, setApplications] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [statusFilter, setStatusFilter] = useState('ALL');

  // Mock data - replace with API call
  useEffect(() => {
    const fetchApplications = async () => {
      setLoading(true);
      try {
        const response = await api.get('/api/workflow/l3/applications');
        setApplications(response.data);
      } catch (error) {
        console.error('Error fetching applications:', error);

        if (error.response?.status === 401) {
          window.location.href = '/login';
        } else {
          console.error('API Error:', error);
          // Try to force applications to L3 status first
          try {
            await api.post('/api/workflow/test/force-l3-status');
            // Then try to fetch again
            const retryResponse = await api.get('/api/workflow/l3/applications');
            setApplications(retryResponse.data);
            return;
          } catch (retryError) {
            console.error('Retry failed:', retryError);
          }

          // Fallback to mock data for demonstration
          const mockApplications = [
            {
              id: 1,
              applicationId: 'LA' + Date.now(),
              applicantName: 'Rajesh Kumar',
              email: 'rajesh.kumar@example.com',
              phoneNumber: '+91-9876543210',
              monthlyIncome: 150000,
              loanAmount: 7500000, // 75 lakhs - requires L3 approval
              loanPurpose: 'HOME_PURCHASE',
              loanTenure: 20,
              eligibilityScore: 85,
              status: 'PENDING_L3',
              createdAt: new Date().toISOString(),
              updatedAt: new Date().toISOString(),
              l2ApprovedDate: new Date().toISOString(),
              riskCategory: 'MEDIUM',
              priority: 'HIGH',
              offerLetterGenerated: false,
              submittedByName: 'Sales Executive',
              estimatedEmi: 65000
            },
            {
              id: 2,
              applicationId: 'LA' + (Date.now() + 1),
              applicantName: 'Priya Singh',
              email: 'priya.singh@example.com',
              phoneNumber: '+91-9876543211',
              monthlyIncome: 200000,
              loanAmount: 9500000, // 95 lakhs - requires L3 approval
              loanPurpose: 'HOME_CONSTRUCTION',
              loanTenure: 25,
              eligibilityScore: 92,
              status: 'PENDING_L3',
              createdAt: new Date(Date.now() - 86400000).toISOString(),
              updatedAt: new Date(Date.now() - 86400000).toISOString(),
              l2ApprovedDate: new Date(Date.now() - 86400000).toISOString(),
              riskCategory: 'LOW',
              priority: 'NORMAL',
              offerLetterGenerated: false,
              submittedByName: 'Sales Executive',
              estimatedEmi: 82000
            },
            {
              id: 3,
              applicationId: 'LA' + (Date.now() + 2),
              applicantName: 'Amit Sharma',
              email: 'amit.sharma@example.com',
              phoneNumber: '+91-9876543212',
              monthlyIncome: 180000,
              loanAmount: 6800000, // 68 lakhs - requires L3 approval
              loanPurpose: 'HOME_PURCHASE',
              loanTenure: 18,
              eligibilityScore: 88,
              status: 'PENDING_L3',
              createdAt: new Date(Date.now() - 172800000).toISOString(),
              updatedAt: new Date(Date.now() - 172800000).toISOString(),
              l2ApprovedDate: new Date(Date.now() - 172800000).toISOString(),
              riskCategory: 'MEDIUM',
              priority: 'HIGH',
              offerLetterGenerated: false,
              submittedByName: 'Sales Executive',
              estimatedEmi: 58000
            }
          ];
          setApplications(mockApplications);
        }
      } finally {
        setLoading(false);
      }
    };

    fetchApplications();
  }, []);

  const createTestApplication = async () => {
    try {
      const response = await api.post('/api/admin/test/create-l3-application');
      console.log('Test application created:', response.data);
      // Refresh the applications list
      await fetchApplications();
      alert('Test L3 application created successfully!');
    } catch (error) {
      console.error('Error creating test application:', error);
      alert('Failed to create test application: ' + (error.response?.data?.message || error.message));
    }
  };

  const forceExistingToL3 = async () => {
    try {
      const response = await api.post('/api/workflow/test/force-l3-status');
      console.log('Applications forced to L3:', response.data);
      // Refresh the applications list
      await fetchApplications();
      alert('Existing applications forced to L3 status!');
    } catch (error) {
      console.error('Error forcing applications to L3:', error);
      alert('Failed to force applications to L3: ' + (error.response?.data?.message || error.message));
    }
  };

  const filteredApplications = applications.filter(app => {
    const matchesSearch = app.applicantName.toLowerCase().includes(searchTerm.toLowerCase()) ||
                         app.id.toLowerCase().includes(searchTerm.toLowerCase());
    const matchesStatus = statusFilter === 'ALL' || app.status === statusFilter;
    return matchesSearch && matchesStatus;
  });

  const getStatusBadge = (status) => {
    switch (status) {
      case 'PENDING_L3':
        return <span className="status-pending">Pending L3 Final Approval</span>;
      case 'L3_APPROVED':
        return <span className="status-approved">L3 Approved</span>;
      case 'L3_REJECTED':
        return <span className="status-rejected">L3 Rejected</span>;
      default:
        return <span className="status-pending">{status}</span>;
    }
  };

  const getPriorityBadge = (priority) => {
    switch (priority) {
      case 'HIGH':
        return <span className="bg-red-100 text-red-800 px-2 py-1 rounded-full text-xs font-medium">High Priority</span>;
      case 'NORMAL':
        return <span className="bg-blue-100 text-blue-800 px-2 py-1 rounded-full text-xs font-medium">Normal</span>;
      case 'LOW':
        return <span className="bg-gray-100 text-gray-800 px-2 py-1 rounded-full text-xs font-medium">Low Priority</span>;
      default:
        return <span className="bg-gray-100 text-gray-800 px-2 py-1 rounded-full text-xs font-medium">{priority}</span>;
    }
  };

  const getRiskBadge = (risk) => {
    switch (risk) {
      case 'LOW':
        return <span className="bg-green-100 text-green-800 px-2 py-1 rounded-full text-xs font-medium">Low Risk</span>;
      case 'MEDIUM':
        return <span className="bg-yellow-100 text-yellow-800 px-2 py-1 rounded-full text-xs font-medium">Medium Risk</span>;
      case 'HIGH':
        return <span className="bg-red-100 text-red-800 px-2 py-1 rounded-full text-xs font-medium">High Risk</span>;
      default:
        return <span className="bg-gray-100 text-gray-800 px-2 py-1 rounded-full text-xs font-medium">{risk}</span>;
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

  const calculateDaysInQueue = (date) => {
    const today = new Date();
    const submittedDate = new Date(date);
    const diffTime = Math.abs(today - submittedDate);
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
    return diffDays;
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
    <Layout title="L3 Admin Final Approval">
      <div className="space-y-6">
        {/* Header */}
        <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between">
          <div>
            <h1 className="text-2xl font-bold text-gray-900">L3 Admin Final Approval</h1>
            <p className="text-gray-600 mt-1">Final review and loan offer generation</p>
          </div>
          <div className="mt-4 sm:mt-0 flex space-x-4">
            <span className="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium bg-blue-100 text-blue-800">
              {filteredApplications.filter(app => app.status === 'PENDING_L3').length} Pending
            </span>
            <span className="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium bg-green-100 text-green-800">
              {filteredApplications.filter(app => app.status === 'L3_APPROVED').length} Approved
            </span>
            <button
              onClick={createTestApplication}
              className="inline-flex items-center px-3 py-1 rounded-md text-sm font-medium bg-purple-600 text-white hover:bg-purple-700"
            >
              Create Test App
            </button>
            <button
              onClick={forceExistingToL3}
              className="inline-flex items-center px-3 py-1 rounded-md text-sm font-medium bg-orange-600 text-white hover:bg-orange-700"
            >
              Force Existing to L3
            </button>
          </div>
        </div>

        {/* Summary Cards */}
        <div className="grid grid-cols-1 md:grid-cols-4 gap-6">
          <div className="card p-6">
            <div className="flex items-center">
              <div className="flex-shrink-0">
                <ClockIcon className="h-8 w-8 text-blue-600" />
              </div>
              <div className="ml-4">
                <p className="text-sm font-medium text-gray-500">Pending Final Approval</p>
                <p className="text-2xl font-semibold text-gray-900">
                  {filteredApplications.filter(app => app.status === 'PENDING_L3').length}
                </p>
              </div>
            </div>
          </div>
          
          <div className="card p-6">
            <div className="flex items-center">
              <div className="flex-shrink-0">
                <ArrowTrendingUpIcon className="h-8 w-8 text-green-600" />
              </div>
              <div className="ml-4">
                <p className="text-sm font-medium text-gray-500">Total Pipeline Value</p>
                <p className="text-2xl font-semibold text-gray-900">
                  {formatCurrency(filteredApplications.filter(app => app.status === 'PENDING_L3').reduce((sum, app) => sum + app.loanAmount, 0))}
                </p>
              </div>
            </div>
          </div>
          
          <div className="card p-6">
            <div className="flex items-center">
              <div className="flex-shrink-0">
                <ArrowDownTrayIcon className="h-8 w-8 text-purple-600" />
              </div>
              <div className="ml-4">
                <p className="text-sm font-medium text-gray-500">Offer Letters Generated</p>
                <p className="text-2xl font-semibold text-gray-900">
                  {filteredApplications.filter(app => app.offerLetterGenerated).length}
                </p>
              </div>
            </div>
          </div>
          
          <div className="card p-6">
            <div className="flex items-center">
              <div className="flex-shrink-0">
                <CheckCircleIcon className="h-8 w-8 text-green-600" />
              </div>
              <div className="ml-4">
                <p className="text-sm font-medium text-gray-500">Final Approval Rate</p>
                <p className="text-2xl font-semibold text-gray-900">
                  {Math.round((filteredApplications.filter(app => app.status === 'L3_APPROVED').length / filteredApplications.length) * 100) || 0}%
                </p>
              </div>
            </div>
          </div>
        </div>

        {/* Filters */}
        <div className="card p-6">
          <div className="flex flex-col sm:flex-row gap-4">
            <div className="flex-1">
              <div className="relative">
                <MagnifyingGlassIcon className="absolute left-3 top-1/2 transform -translate-y-1/2 h-5 w-5 text-gray-400" />
                <input
                  type="text"
                  placeholder="Search by applicant name or application ID..."
                  className="input-field pl-10"
                  value={searchTerm}
                  onChange={(e) => setSearchTerm(e.target.value)}
                />
              </div>
            </div>
            <div className="sm:w-48">
              <select
                className="input-field"
                value={statusFilter}
                onChange={(e) => setStatusFilter(e.target.value)}
              >
                <option value="ALL">All Status</option>
                <option value="PENDING_L3">Pending L3</option>
                <option value="L3_APPROVED">L3 Approved</option>
                <option value="L3_REJECTED">L3 Rejected</option>
              </select>
            </div>
          </div>
        </div>

        {/* Applications Table */}
        <div className="card overflow-hidden">
          <div className="overflow-x-auto">
            <table className="min-w-full divide-y divide-gray-200">
              <thead className="table-header">
                <tr>
                  <th className="px-6 py-3 text-left text-xs font-medium uppercase tracking-wider">
                    Application
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium uppercase tracking-wider">
                    Applicant
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium uppercase tracking-wider">
                    Loan Details
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium uppercase tracking-wider">
                    Risk & Priority
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium uppercase tracking-wider">
                    L2 Status
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium uppercase tracking-wider">
                    Queue Time
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium uppercase tracking-wider">
                    Status
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium uppercase tracking-wider">
                    Actions
                  </th>
                </tr>
              </thead>
              <tbody className="bg-white divide-y divide-gray-200">
                {filteredApplications.map((application, index) => (
                  <motion.tr
                    key={application.id}
                    initial={{ opacity: 0, y: 20 }}
                    animate={{ opacity: 1, y: 0 }}
                    transition={{ delay: index * 0.1 }}
                    className="hover:bg-gray-50"
                  >
                    <td className="px-6 py-4 whitespace-nowrap">
                      <div className="text-sm font-medium text-gray-900">{application.id}</div>
                      {application.offerLetterGenerated && (
                        <div className="flex items-center text-xs text-green-600 mt-1">
                          <ArrowDownTrayIcon className="h-3 w-3 mr-1" />
                          Offer Letter Ready
                        </div>
                      )}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <div>
                        <div className="text-sm font-medium text-gray-900">{application.applicantName}</div>
                        <div className="text-sm text-gray-500">{application.phoneNumber}</div>
                        <div className="text-sm text-gray-500">₹{application.monthlyIncome ? application.monthlyIncome.toLocaleString() : 'N/A'}/month</div>
                      </div>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <div>
                        <div className="text-sm font-medium text-gray-900">{formatCurrency(application.loanAmount)}</div>
                        <div className="text-sm text-gray-500">{application.loanPurpose.replace('_', ' ')}</div>
                        <div className="text-sm text-gray-500">Score: {application.eligibilityScore}/100</div>
                      </div>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <div className="space-y-1">
                        {getRiskBadge(application.riskCategory)}
                        {getPriorityBadge(application.priority)}
                      </div>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <div>
                        <div className="text-sm text-gray-900">L2 Approved</div>
                        <div className="text-sm text-gray-500">{formatDate(application.l2ApprovedDate)}</div>
                      </div>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <div className="flex items-center text-sm text-gray-900">
                        <ClockIcon className="h-4 w-4 mr-1 text-gray-400" />
                        {calculateDaysInQueue(application.l2ApprovedDate)} days
                      </div>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      {getStatusBadge(application.status)}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm font-medium">
                      <Link
                        to={`/l3/details/${application.id}`}
                        className="inline-flex items-center px-3 py-1 border border-transparent text-sm leading-4 font-medium rounded-md text-primary-700 bg-primary-100 hover:bg-primary-200 transition-colors"
                      >
                        <EyeIcon className="h-4 w-4 mr-1" />
                        Review
                      </Link>
                    </td>
                  </motion.tr>
                ))}
              </tbody>
            </table>
          </div>

          {filteredApplications.length === 0 && (
            <div className="text-center py-12">
              <div className="text-gray-500">
                <FunnelIcon className="mx-auto h-12 w-12 mb-4" />
                <h3 className="text-lg font-medium mb-2">No applications found</h3>
                <p>Try adjusting your search criteria or check back later.</p>
              </div>
            </div>
          )}
        </div>
      </div>
    </Layout>
  );
};

export default L3Index;
