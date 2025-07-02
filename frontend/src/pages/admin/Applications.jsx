import React, { useState, useEffect } from 'react';
import Layout from '../../components/Layout';
import DocumentViewer from '../../components/DocumentViewer';
import { motion } from 'framer-motion';
import {
  MagnifyingGlassIcon,
  FunnelIcon,
  EyeIcon,
  DocumentIcon,
  ArrowDownTrayIcon,
  CalendarIcon,
  CurrencyRupeeIcon,
  UserIcon,
  ClockIcon
} from '@heroicons/react/24/outline';
import api from '../../config/axios';

const AdminApplications = () => {
  const [applications, setApplications] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [statusFilter, setStatusFilter] = useState('ALL');
  const [selectedApplication, setSelectedApplication] = useState(null);
  const [showDocuments, setShowDocuments] = useState(false);

  useEffect(() => {
    fetchApplications();
  }, []);

  const fetchApplications = async () => {
    setLoading(true);
    try {
      const response = await api.get('/api/admin/applications');
      setApplications(response.data);
    } catch (error) {
      console.error('Error fetching applications:', error);
      setApplications([]);
    } finally {
      setLoading(false);
    }
  };

  const filteredApplications = applications.filter(app => {
    const matchesSearch = 
      app.applicantName.toLowerCase().includes(searchTerm.toLowerCase()) ||
      app.applicationId.toLowerCase().includes(searchTerm.toLowerCase()) ||
      app.email.toLowerCase().includes(searchTerm.toLowerCase());
    const matchesStatus = statusFilter === 'ALL' || app.status === statusFilter;
    return matchesSearch && matchesStatus;
  });

  const getStatusBadge = (status) => {
    const statusConfig = {
      'SUBMITTED': { color: 'bg-blue-100 text-blue-800', label: 'Submitted' },
      'PENDING_RCPU': { color: 'bg-yellow-100 text-yellow-800', label: 'Pending RCPU' },
      'RCPU_COMPLETED': { color: 'bg-purple-100 text-purple-800', label: 'RCPU Completed' },
      'PENDING_L1': { color: 'bg-orange-100 text-orange-800', label: 'Pending L1' },
      'L1_APPROVED': { color: 'bg-green-100 text-green-800', label: 'L1 Approved' },
      'PENDING_L2': { color: 'bg-orange-100 text-orange-800', label: 'Pending L2' },
      'L2_APPROVED': { color: 'bg-green-100 text-green-800', label: 'L2 Approved' },
      'PENDING_L3': { color: 'bg-red-100 text-red-800', label: 'Pending L3' },
      'L3_APPROVED': { color: 'bg-green-100 text-green-800', label: 'L3 Approved' },
      'REJECTED': { color: 'bg-red-100 text-red-800', label: 'Rejected' },
      'CANCELLED': { color: 'bg-gray-100 text-gray-800', label: 'Cancelled' }
    };

    const config = statusConfig[status] || { color: 'bg-gray-100 text-gray-800', label: status };
    return (
      <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${config.color}`}>
        {config.label}
      </span>
    );
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

  const calculateDaysInSystem = (createdAt) => {
    const today = new Date();
    const created = new Date(createdAt);
    const diffTime = Math.abs(today - created);
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
    return diffDays;
  };

  const handleViewDocuments = (applicationId) => {
    setSelectedApplication(applicationId);
    setShowDocuments(true);
  };

  const getStatusCounts = () => {
    const counts = {};
    applications.forEach(app => {
      counts[app.status] = (counts[app.status] || 0) + 1;
    });
    return counts;
  };

  const statusCounts = getStatusCounts();

  if (loading) {
    return (
      <Layout title="Applications Management">
        <div className="flex items-center justify-center h-64">
          <div className="animate-spin rounded-full h-12 w-12 border-4 border-primary-600 border-t-transparent"></div>
        </div>
      </Layout>
    );
  }

  return (
    <Layout title="Applications Management">
      <div className="space-y-6">
        {/* Header */}
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Applications Management</h1>
          <p className="text-gray-600 mt-1">Comprehensive view of all loan applications in the system</p>
        </div>

        {/* Summary Cards */}
        <div className="grid grid-cols-1 md:grid-cols-4 gap-6">
          <div className="card p-6">
            <div className="flex items-center">
              <div className="flex-shrink-0">
                <UserIcon className="h-8 w-8 text-blue-600" />
              </div>
              <div className="ml-4">
                <p className="text-sm font-medium text-gray-500">Total Applications</p>
                <p className="text-2xl font-semibold text-gray-900">{applications.length}</p>
              </div>
            </div>
          </div>
          
          <div className="card p-6">
            <div className="flex items-center">
              <div className="flex-shrink-0">
                <ClockIcon className="h-8 w-8 text-yellow-600" />
              </div>
              <div className="ml-4">
                <p className="text-sm font-medium text-gray-500">Pending Review</p>
                <p className="text-2xl font-semibold text-gray-900">
                  {(statusCounts['PENDING_RCPU'] || 0) + (statusCounts['PENDING_L1'] || 0) + 
                   (statusCounts['PENDING_L2'] || 0) + (statusCounts['PENDING_L3'] || 0)}
                </p>
              </div>
            </div>
          </div>
          
          <div className="card p-6">
            <div className="flex items-center">
              <div className="flex-shrink-0">
                <CurrencyRupeeIcon className="h-8 w-8 text-green-600" />
              </div>
              <div className="ml-4">
                <p className="text-sm font-medium text-gray-500">Total Loan Value</p>
                <p className="text-2xl font-semibold text-gray-900">
                  {formatCurrency(applications.reduce((sum, app) => sum + (app.loanAmount || 0), 0))}
                </p>
              </div>
            </div>
          </div>
          
          <div className="card p-6">
            <div className="flex items-center">
              <div className="flex-shrink-0">
                <DocumentIcon className="h-8 w-8 text-purple-600" />
              </div>
              <div className="ml-4">
                <p className="text-sm font-medium text-gray-500">Approved Applications</p>
                <p className="text-2xl font-semibold text-gray-900">
                  {(statusCounts['L1_APPROVED'] || 0) + (statusCounts['L2_APPROVED'] || 0) + 
                   (statusCounts['L3_APPROVED'] || 0)}
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
                  placeholder="Search by applicant name, application ID, or email..."
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
                <option value="SUBMITTED">Submitted</option>
                <option value="PENDING_RCPU">Pending RCPU</option>
                <option value="PENDING_L1">Pending L1</option>
                <option value="PENDING_L2">Pending L2</option>
                <option value="PENDING_L3">Pending L3</option>
                <option value="L1_APPROVED">L1 Approved</option>
                <option value="L2_APPROVED">L2 Approved</option>
                <option value="L3_APPROVED">L3 Approved</option>
                <option value="REJECTED">Rejected</option>
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
                    Status
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium uppercase tracking-wider">
                    Documents
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium uppercase tracking-wider">
                    Days in System
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
                    transition={{ delay: index * 0.05 }}
                    className="hover:bg-gray-50"
                  >
                    <td className="px-6 py-4 whitespace-nowrap">
                      <div>
                        <div className="text-sm font-medium text-gray-900">{application.applicationId}</div>
                        <div className="text-sm text-gray-500">{formatDate(application.createdAt)}</div>
                      </div>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <div>
                        <div className="text-sm font-medium text-gray-900">{application.applicantName}</div>
                        <div className="text-sm text-gray-500">{application.email}</div>
                        <div className="text-sm text-gray-500">{application.phoneNumber}</div>
                      </div>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <div>
                        <div className="text-sm font-medium text-gray-900">{formatCurrency(application.loanAmount)}</div>
                        <div className="text-sm text-gray-500">{application.loanPurpose?.replace('_', ' ')}</div>
                        <div className="text-sm text-gray-500">{application.loanTenure} years</div>
                      </div>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      {getStatusBadge(application.status)}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <div className="flex items-center space-x-2">
                        <span className="text-sm text-gray-900">{application.documentCount || 0} files</span>
                        {application.hasRequiredDocuments && (
                          <span className="inline-flex items-center px-2 py-1 rounded-full text-xs font-medium bg-green-100 text-green-800">
                            Complete
                          </span>
                        )}
                      </div>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <div className="flex items-center text-sm text-gray-900">
                        <CalendarIcon className="h-4 w-4 mr-1 text-gray-400" />
                        {calculateDaysInSystem(application.createdAt)} days
                      </div>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm font-medium">
                      <div className="flex items-center space-x-2">
                        <button
                          onClick={() => handleViewDocuments(application.id)}
                          className="inline-flex items-center px-3 py-1 border border-transparent text-sm leading-4 font-medium rounded-md text-blue-700 bg-blue-100 hover:bg-blue-200 transition-colors"
                        >
                          <DocumentIcon className="h-4 w-4 mr-1" />
                          Docs
                        </button>
                      </div>
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

      {/* Document Viewer Modal */}
      <DocumentViewer
        applicationId={selectedApplication}
        isOpen={showDocuments}
        onClose={() => setShowDocuments(false)}
        readOnly={true}
      />
    </Layout>
  );
};

export default AdminApplications;
