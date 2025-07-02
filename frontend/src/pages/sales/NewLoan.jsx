import React, { useState, useEffect } from 'react';
import Layout from '../../components/Layout';
import { motion } from 'framer-motion';
import FinancialInfoStep from '../../components/loan/FinancialInfoStep';
import DocumentUploadStep from '../../components/loan/DocumentUploadStep';
import CoApplicantStep from '../../components/loan/CoApplicantStep';
import EligibilityCheckStep from '../../components/loan/EligibilityCheckStep';
import api from '../../config/axios';
import { useAuth } from '../../contexts/AuthContext';
import { useNavigate } from 'react-router-dom';
// Step Components
const PersonalInfoStep = ({ formData, updateFormData }) => (
  <div>
    <h3 className="text-lg font-semibold mb-6">Personal Information</h3>
    <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
      <div>
        <label className="block text-sm font-medium text-gray-700 mb-1">First Name *</label>
        <input
          type="text"
          className="input-field"
          value={formData.personalInfo.firstName}
          onChange={(e) => updateFormData('personalInfo', { firstName: e.target.value })}
          placeholder="Enter first name"
        />
      </div>
      <div>
        <label className="block text-sm font-medium text-gray-700 mb-1">Last Name *</label>
        <input
          type="text"
          className="input-field"
          value={formData.personalInfo.lastName}
          onChange={(e) => updateFormData('personalInfo', { lastName: e.target.value })}
          placeholder="Enter last name"
        />
      </div>
      <div>
        <label className="block text-sm font-medium text-gray-700 mb-1">Date of Birth *</label>
        <input
          type="date"
          className="input-field"
          value={formData.personalInfo.dateOfBirth}
          onChange={(e) => updateFormData('personalInfo', { dateOfBirth: e.target.value })}
        />
      </div>
      <div>
        <label className="block text-sm font-medium text-gray-700 mb-1">Gender *</label>
        <select
          className="input-field"
          value={formData.personalInfo.gender}
          onChange={(e) => updateFormData('personalInfo', { gender: e.target.value })}
        >
          <option value="">Select Gender</option>
          <option value="MALE">Male</option>
          <option value="FEMALE">Female</option>
          <option value="OTHER">Other</option>
        </select>
      </div>
      <div>
        <label className="block text-sm font-medium text-gray-700 mb-1">PAN Number *</label>
        <input
          type="text"
          className="input-field"
          value={formData.personalInfo.panNumber}
          onChange={(e) => updateFormData('personalInfo', { panNumber: e.target.value.toUpperCase() })}
          placeholder="ABCDE1234F"
          maxLength="10"
        />
      </div>
      <div>
        <label className="block text-sm font-medium text-gray-700 mb-1">Aadhaar Number *</label>
        <input
          type="text"
          className="input-field"
          value={formData.personalInfo.aadhaarNumber}
          onChange={(e) => {
            // Remove all non-digits and limit to 12 digits
            const value = e.target.value.replace(/\D/g, '').slice(0, 12);
            updateFormData('personalInfo', { aadhaarNumber: value });
          }}
          placeholder="123456789012"
          maxLength="12"
        />
        <p className="text-xs text-gray-500 mt-1">Enter 12-digit Aadhaar number</p>
      </div>
      <div>
        <label className="block text-sm font-medium text-gray-700 mb-1">Phone Number *</label>
        <input
          type="tel"
          className="input-field"
          value={formData.personalInfo.phoneNumber}
          onChange={(e) => {
            // Remove all non-digits and limit to 10 digits
            const value = e.target.value.replace(/\D/g, '').slice(0, 10);
            updateFormData('personalInfo', { phoneNumber: value });
          }}
          placeholder="9876543210"
          maxLength="10"
        />
        <p className="text-xs text-gray-500 mt-1">Enter 10-digit mobile number</p>
      </div>
      <div>
        <label className="block text-sm font-medium text-gray-700 mb-1">Email Address *</label>
        <input
          type="email"
          className="input-field"
          value={formData.personalInfo.email}
          onChange={(e) => updateFormData('personalInfo', { email: e.target.value })}
          placeholder="john@example.com"
        />
      </div>
      <div>
        <label className="block text-sm font-medium text-gray-700 mb-1">Marital Status *</label>
        <select
          className="input-field"
          value={formData.personalInfo.maritalStatus}
          onChange={(e) => updateFormData('personalInfo', { maritalStatus: e.target.value })}
        >
          <option value="">Select Marital Status</option>
          <option value="SINGLE">Single</option>
          <option value="MARRIED">Married</option>
          <option value="DIVORCED">Divorced</option>
          <option value="WIDOWED">Widowed</option>
        </select>
      </div>
    </div>
  </div>
);

const AddressInfoStep = ({ formData, updateFormData }) => (
  <div>
    <h3 className="text-lg font-semibold mb-6">Address Information</h3>
    <div className="space-y-6">
      <div>
        <h4 className="font-medium text-gray-900 mb-4">Current Address</h4>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div className="md:col-span-2">
            <label className="block text-sm font-medium text-gray-700 mb-1">Street Address *</label>
            <input
              type="text"
              className="input-field"
              value={formData.addressInfo.currentAddress.street}
              onChange={(e) => updateFormData('addressInfo', {
                currentAddress: { ...formData.addressInfo.currentAddress, street: e.target.value }
              })}
              placeholder="Enter street address"
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">City *</label>
            <input
              type="text"
              className="input-field"
              value={formData.addressInfo.currentAddress.city}
              onChange={(e) => updateFormData('addressInfo', {
                currentAddress: { ...formData.addressInfo.currentAddress, city: e.target.value }
              })}
              placeholder="Enter city"
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">State *</label>
            <input
              type="text"
              className="input-field"
              value={formData.addressInfo.currentAddress.state}
              onChange={(e) => updateFormData('addressInfo', {
                currentAddress: { ...formData.addressInfo.currentAddress, state: e.target.value }
              })}
              placeholder="Enter state"
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Pincode *</label>
            <input
              type="text"
              className="input-field"
              value={formData.addressInfo.currentAddress.pincode}
              onChange={(e) => updateFormData('addressInfo', {
                currentAddress: { ...formData.addressInfo.currentAddress, pincode: e.target.value }
              })}
              placeholder="123456"
              maxLength="6"
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Residence Type *</label>
            <select
              className="input-field"
              value={formData.addressInfo.currentAddress.residenceType}
              onChange={(e) => updateFormData('addressInfo', {
                currentAddress: { ...formData.addressInfo.currentAddress, residenceType: e.target.value }
              })}
            >
              <option value="">Select Type</option>
              <option value="OWNED">Owned</option>
              <option value="RENTED">Rented</option>
              <option value="FAMILY_OWNED">Family Owned</option>
            </select>
          </div>
        </div>
      </div>

      <div>
        <div className="flex items-center mb-4">
          <input
            type="checkbox"
            id="sameAsCurrent"
            className="mr-2"
            checked={formData.addressInfo.permanentAddress.sameAsCurrent}
            onChange={(e) => updateFormData('addressInfo', {
              permanentAddress: { ...formData.addressInfo.permanentAddress, sameAsCurrent: e.target.checked }
            })}
          />
          <label htmlFor="sameAsCurrent" className="text-sm font-medium text-gray-700">
            Permanent address same as current address
          </label>
        </div>

        {!formData.addressInfo.permanentAddress.sameAsCurrent && (
          <div>
            <h4 className="font-medium text-gray-900 mb-4">Permanent Address</h4>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div className="md:col-span-2">
                <label className="block text-sm font-medium text-gray-700 mb-1">Street Address *</label>
                <input
                  type="text"
                  className="input-field"
                  value={formData.addressInfo.permanentAddress.street}
                  onChange={(e) => updateFormData('addressInfo', {
                    permanentAddress: { ...formData.addressInfo.permanentAddress, street: e.target.value }
                  })}
                  placeholder="Enter street address"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">City *</label>
                <input
                  type="text"
                  className="input-field"
                  value={formData.addressInfo.permanentAddress.city}
                  onChange={(e) => updateFormData('addressInfo', {
                    permanentAddress: { ...formData.addressInfo.permanentAddress, city: e.target.value }
                  })}
                  placeholder="Enter city"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">State *</label>
                <input
                  type="text"
                  className="input-field"
                  value={formData.addressInfo.permanentAddress.state}
                  onChange={(e) => updateFormData('addressInfo', {
                    permanentAddress: { ...formData.addressInfo.permanentAddress, state: e.target.value }
                  })}
                  placeholder="Enter state"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Pincode *</label>
                <input
                  type="text"
                  className="input-field"
                  value={formData.addressInfo.permanentAddress.pincode}
                  onChange={(e) => updateFormData('addressInfo', {
                    permanentAddress: { ...formData.addressInfo.permanentAddress, pincode: e.target.value }
                  })}
                  placeholder="123456"
                  maxLength="6"
                />
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  </div>
);

const NewLoan = () => {
  const { user, loading } = useAuth();
  const navigate = useNavigate();
  const [currentStep, setCurrentStep] = useState(1);
  const [formData, setFormData] = useState({
    // Personal Info
    personalInfo: {
      firstName: '',
      lastName: '',
      dateOfBirth: '',
      gender: '',
      panNumber: '',
      aadhaarNumber: '',
      phoneNumber: '',
      email: '',
      maritalStatus: ''
    },
    // Address Info
    addressInfo: {
      currentAddress: {
        street: '',
        city: '',
        state: '',
        pincode: '',
        residenceType: ''
      },
      permanentAddress: {
        street: '',
        city: '',
        state: '',
        pincode: '',
        sameAsCurrent: false
      }
    },
    // Financial Info
    financialInfo: {
      employmentType: '',
      monthlyIncome: '',
      employer: '',
      workExperience: '',
      loanAmount: '',
      loanPurpose: '',
      loanTenure: ''
    },
    // Documents
    documents: {
      itr: null,
      bankStatement: null,
      aadhaar: null,
      pan: null,
      photo: null
    },
    // Co-Applicant
    coApplicant: {
      hasCoApplicant: false,
      personalInfo: {},
      financialInfo: {},
      documents: {}
    }
  });

  const [submitting, setSubmitting] = useState(false);
  const [eligibilityResult, setEligibilityResult] = useState(null);

  // Check authentication
  useEffect(() => {
    if (!loading && !user) {
      navigate('/login');
    }
  }, [user, loading, navigate]);

  // Show loading while checking authentication
  if (loading) {
    return (
      <Layout>
        <div className="flex justify-center items-center h-64">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
        </div>
      </Layout>
    );
  }

  // Redirect if not authenticated
  if (!user) {
    return null;
  }

  const steps = [
    { id: 1, name: 'Personal Info' },
    { id: 2, name: 'Address Info' },
    { id: 3, name: 'Financial Info' },
    { id: 4, name: 'Documents' },
    { id: 5, name: 'Co-Applicant' },
    { id: 6, name: 'Eligibility Check' }
  ];

  const updateFormData = (section, data) => {
    setFormData(prev => ({
      ...prev,
      [section]: { ...prev[section], ...data }
    }));
  };

  // Function to populate form with sample data for testing
  const fillSampleData = () => {
    setFormData({
      personalInfo: {
        firstName: 'John',
        lastName: 'Doe',
        dateOfBirth: '1990-01-15',
        gender: 'MALE',
        maritalStatus: 'MARRIED',
        panNumber: 'ABCDE1234F',
        aadhaarNumber: '123456789012',
        phoneNumber: '9876543210',
        email: 'john.doe@example.com'
      },
      addressInfo: {
        currentAddress: {
          street: '123 Main Street, Apartment 4B',
          city: 'Mumbai',
          state: 'Maharashtra',
          pincode: '400001',
          residenceType: 'RENTED'
        },
        permanentAddress: {
          street: '456 Home Street',
          city: 'Pune',
          state: 'Maharashtra',
          pincode: '411001',
          sameAsCurrent: false
        }
      },
      financialInfo: {
        employmentType: 'SALARIED',
        monthlyIncome: '75000',
        employer: 'Tech Solutions Pvt Ltd',
        workExperience: '5',
        loanAmount: '2500000',
        loanPurpose: 'HOME_PURCHASE',
        loanTenure: '20'
      },
      documents: {
        itr: null,
        bankStatement: null,
        aadhaar: null,
        pan: null,
        photo: null
      },
      coApplicant: {
        hasCoApplicant: false,
        personalInfo: {},
        financialInfo: {},
        documents: {}
      }
    });
  };

  const nextStep = () => {
    if (currentStep < steps.length) {
      setCurrentStep(currentStep + 1);
    }
  };

  const prevStep = () => {
    if (currentStep > 1) {
      setCurrentStep(currentStep - 1);
    }
  };

  const validateForm = () => {
    const errors = [];

    // Personal Info validation
    if (!formData.personalInfo.firstName) errors.push('First Name is required');
    if (!formData.personalInfo.lastName) errors.push('Last Name is required');
    if (!formData.personalInfo.dateOfBirth) errors.push('Date of Birth is required');
    if (!formData.personalInfo.gender) errors.push('Gender is required');
    if (!formData.personalInfo.maritalStatus) errors.push('Marital Status is required');

    // PAN Number validation
    if (!formData.personalInfo.panNumber) {
      errors.push('PAN Number is required');
    } else if (!/^[A-Z]{5}[0-9]{4}[A-Z]{1}$/.test(formData.personalInfo.panNumber)) {
      errors.push('PAN Number must be in format: ABCDE1234F');
    }

    // Aadhaar Number validation
    if (!formData.personalInfo.aadhaarNumber) {
      errors.push('Aadhaar Number is required');
    } else if (!/^\d{12}$/.test(formData.personalInfo.aadhaarNumber.replace(/\s/g, ''))) {
      errors.push('Aadhaar Number must be 12 digits');
    }

    // Phone Number validation
    if (!formData.personalInfo.phoneNumber) {
      errors.push('Phone Number is required');
    } else if (!/^\d{10}$/.test(formData.personalInfo.phoneNumber)) {
      errors.push('Phone Number must be 10 digits');
    }

    if (!formData.personalInfo.email) errors.push('Email is required');

    // Address Info validation
    if (!formData.addressInfo.currentAddress.street) errors.push('Current Address is required');
    if (!formData.addressInfo.currentAddress.city) errors.push('Current City is required');
    if (!formData.addressInfo.currentAddress.state) errors.push('Current State is required');
    if (!formData.addressInfo.currentAddress.pincode) errors.push('Current Pincode is required');
    if (!formData.addressInfo.currentAddress.residenceType) errors.push('Residence Type is required');

    // Financial Info validation
    if (!formData.financialInfo.employmentType) errors.push('Employment Type is required');
    if (!formData.financialInfo.monthlyIncome) errors.push('Monthly Income is required');
    if (!formData.financialInfo.employer) errors.push('Employer is required');
    if (!formData.financialInfo.workExperience) errors.push('Work Experience is required');
    if (!formData.financialInfo.loanAmount) errors.push('Loan Amount is required');
    if (!formData.financialInfo.loanPurpose) errors.push('Loan Purpose is required');
    if (!formData.financialInfo.loanTenure) errors.push('Loan Tenure is required');

    return errors;
  };

  const handleSubmit = async () => {
    // Validate form before submission
    const validationErrors = validateForm();
    if (validationErrors.length > 0) {
      alert('Please fill in all required fields:\n' + validationErrors.join('\n'));
      return;
    }

    setSubmitting(true);
    try {
      // Helper function to convert empty strings to null
      const emptyToNull = (value) => value === '' ? null : value;

      // Transform form data to match backend DTO structure
      const transformedData = {
        personalInfo: {
          firstName: formData.personalInfo.firstName,
          lastName: formData.personalInfo.lastName,
          dateOfBirth: formData.personalInfo.dateOfBirth,
          gender: emptyToNull(formData.personalInfo.gender),
          maritalStatus: emptyToNull(formData.personalInfo.maritalStatus),
          panNumber: formData.personalInfo.panNumber,
          aadhaarNumber: formData.personalInfo.aadhaarNumber,
          phoneNumber: formData.personalInfo.phoneNumber,
          email: formData.personalInfo.email
        },
        addressInfo: {
          currentAddress: formData.addressInfo.currentAddress.street,
          currentCity: formData.addressInfo.currentAddress.city,
          currentState: formData.addressInfo.currentAddress.state,
          currentPincode: formData.addressInfo.currentAddress.pincode,
          residenceType: emptyToNull(formData.addressInfo.currentAddress.residenceType),
          permanentAddress: formData.addressInfo.permanentAddress.sameAsCurrent
            ? formData.addressInfo.currentAddress.street
            : formData.addressInfo.permanentAddress.street,
          permanentCity: formData.addressInfo.permanentAddress.sameAsCurrent
            ? formData.addressInfo.currentAddress.city
            : formData.addressInfo.permanentAddress.city,
          permanentState: formData.addressInfo.permanentAddress.sameAsCurrent
            ? formData.addressInfo.currentAddress.state
            : formData.addressInfo.permanentAddress.state,
          permanentPincode: formData.addressInfo.permanentAddress.sameAsCurrent
            ? formData.addressInfo.currentAddress.pincode
            : formData.addressInfo.permanentAddress.pincode,
          sameAsCurrent: formData.addressInfo.permanentAddress.sameAsCurrent
        },
        financialInfo: {
          employmentType: emptyToNull(formData.financialInfo.employmentType),
          monthlyIncome: parseFloat(formData.financialInfo.monthlyIncome) || 0,
          employer: formData.financialInfo.employer,
          workExperience: parseInt(formData.financialInfo.workExperience) || 0,
          loanAmount: parseFloat(formData.financialInfo.loanAmount) || 0,
          loanPurpose: emptyToNull(formData.financialInfo.loanPurpose),
          loanTenure: parseInt(formData.financialInfo.loanTenure) || 0
        },
        coApplicant: {
          hasCoApplicant: formData.coApplicant.hasCoApplicant,
          firstName: formData.coApplicant.firstName || null,
          lastName: formData.coApplicant.lastName || null,
          dateOfBirth: formData.coApplicant.dateOfBirth || null,
          panNumber: formData.coApplicant.panNumber || null,
          phoneNumber: formData.coApplicant.phoneNumber || null,
          email: formData.coApplicant.email || null,
          monthlyIncome: formData.coApplicant.monthlyIncome ? parseFloat(formData.coApplicant.monthlyIncome) : null,
          employer: formData.coApplicant.employer || null
        }
      };

      console.log('Submitting loan application:', transformedData);
      console.log('Current token:', localStorage.getItem('token') ? 'present' : 'missing');
      console.log('Current user:', user);

      // Submit loan application using configured api instance
      const response = await api.post('/api/loans/submit', transformedData);

      console.log('Response:', response.data);

      if (response.status === 200) {
        alert(`Loan application submitted successfully! Application ID: ${response.data.applicationId}`);
        // Reset form or redirect
        // You could also navigate to a success page or reset the form here
      }
    } catch (error) {
      console.error('Error submitting application:', error);

      let errorMessage = 'Error submitting application';

      if (error.response) {
        // Server responded with error status
        const status = error.response.status;
        const data = error.response.data;

        switch (status) {
          case 401:
            errorMessage = 'You are not authorized. Please login again.';
            // Redirect to login
            window.location.href = '/login';
            break;
          case 400:
            errorMessage = data.message || 'Invalid application data. Please check your inputs.';
            break;
          case 403:
            errorMessage = 'You do not have permission to submit loan applications.';
            break;
          case 500:
            errorMessage = 'Server error. Please try again later.';
            break;
          default:
            errorMessage = data.message || `Error: ${status}`;
        }
      } else if (error.request) {
        // Network error
        errorMessage = 'Network error. Please check your connection and try again.';
      } else {
        // Other error
        errorMessage = error.message || 'An unexpected error occurred.';
      }

      alert(errorMessage);
    } finally {
      setSubmitting(false);
    }
  };

  const renderCurrentStep = () => {
    switch (currentStep) {
      case 1:
        return <PersonalInfoStep formData={formData} updateFormData={updateFormData} />;
      case 2:
        return <AddressInfoStep formData={formData} updateFormData={updateFormData} />;
      case 3:
        return <FinancialInfoStep formData={formData} updateFormData={updateFormData} />;
      case 4:
        return <DocumentUploadStep formData={formData} updateFormData={updateFormData} />;
      case 5:
        return <CoApplicantStep formData={formData} updateFormData={updateFormData} />;
      case 6:
        return <EligibilityCheckStep formData={formData} eligibilityResult={eligibilityResult} setEligibilityResult={setEligibilityResult} />;
      default:
        return <PersonalInfoStep formData={formData} updateFormData={updateFormData} />;
    }
  };

  return (
    <Layout title="New Loan Application">
      <div className="max-w-4xl mx-auto">
        {/* Progress Steps */}
        <div className="mb-8">
          <div className="flex items-center justify-between">
            {steps.map((step, index) => (
              <div key={step.id} className="flex items-center">
                <div
                  className={`flex items-center justify-center w-10 h-10 rounded-full border-2 ${
                    currentStep >= step.id
                      ? 'bg-primary-600 border-primary-600 text-white'
                      : 'border-gray-300 text-gray-500'
                  }`}
                >
                  {step.id}
                </div>
                <div className="ml-3">
                  <p
                    className={`text-sm font-medium ${
                      currentStep >= step.id ? 'text-primary-600' : 'text-gray-500'
                    }`}
                  >
                    {step.name}
                  </p>
                </div>
                {index < steps.length - 1 && (
                  <div
                    className={`w-16 h-0.5 ml-4 ${
                      currentStep > step.id ? 'bg-primary-600' : 'bg-gray-300'
                    }`}
                  />
                )}
              </div>
            ))}
          </div>
        </div>

        {/* Sample Data Button for Testing */}
        <div className="mb-4 text-center">
          <button
            type="button"
            onClick={fillSampleData}
            className="px-4 py-2 bg-blue-500 text-white rounded-md hover:bg-blue-600 text-sm"
          >
            Fill Sample Data (For Testing)
          </button>
        </div>

        {/* Form Content */}
        <motion.div
          key={currentStep}
          initial={{ opacity: 0, x: 20 }}
          animate={{ opacity: 1, x: 0 }}
          exit={{ opacity: 0, x: -20 }}
          transition={{ duration: 0.3 }}
          className="card p-8"
        >
          {renderCurrentStep()}

          {/* Navigation Buttons */}
          <div className="flex justify-between mt-8 pt-6 border-t">
            <button
              onClick={prevStep}
              disabled={currentStep === 1}
              className="btn-secondary disabled:opacity-50 disabled:cursor-not-allowed"
            >
              Previous
            </button>

            <div className="flex space-x-4">
              {currentStep === steps.length ? (
                <motion.button
                  whileHover={{ scale: 1.02 }}
                  whileTap={{ scale: 0.98 }}
                  onClick={handleSubmit}
                  disabled={submitting}
                  className="btn-primary disabled:opacity-50 disabled:cursor-not-allowed"
                >
                  {submitting ? (
                    <div className="flex items-center">
                      <div className="animate-spin rounded-full h-4 w-4 border-2 border-white border-t-transparent mr-2"></div>
                      Submitting...
                    </div>
                  ) : (
                    'Submit Application'
                  )}
                </motion.button>
              ) : (
                <button
                  onClick={nextStep}
                  className="btn-primary"
                >
                  Next
                </button>
              )}
            </div>
          </div>
        </motion.div>
      </div>
    </Layout>
  );
};

export default NewLoan;
