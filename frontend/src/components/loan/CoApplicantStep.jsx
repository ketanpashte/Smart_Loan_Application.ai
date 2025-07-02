import React from 'react';
import { motion } from 'framer-motion';

const CoApplicantStep = ({ formData, updateFormData }) => {
  const handleCoApplicantToggle = (hasCoApplicant) => {
    updateFormData('coApplicant', { 
      hasCoApplicant,
      personalInfo: hasCoApplicant ? formData.coApplicant.personalInfo : {},
      financialInfo: hasCoApplicant ? formData.coApplicant.financialInfo : {},
      documents: hasCoApplicant ? formData.coApplicant.documents : {}
    });
  };

  const updateCoApplicantData = (section, data) => {
    updateFormData('coApplicant', {
      ...formData.coApplicant,
      [section]: { ...formData.coApplicant[section], ...data }
    });
  };

  return (
    <div>
      <h3 className="text-lg font-semibold mb-6">Co-Applicant Information</h3>
      
      <div className="mb-6">
        <p className="text-gray-600 mb-4">
          Adding a co-applicant can improve your loan eligibility and may help you get better terms.
        </p>
        
        <div className="flex items-center space-x-6">
          <label className="flex items-center">
            <input
              type="radio"
              name="hasCoApplicant"
              checked={!formData.coApplicant.hasCoApplicant}
              onChange={() => handleCoApplicantToggle(false)}
              className="mr-2"
            />
            <span>No Co-Applicant</span>
          </label>
          <label className="flex items-center">
            <input
              type="radio"
              name="hasCoApplicant"
              checked={formData.coApplicant.hasCoApplicant}
              onChange={() => handleCoApplicantToggle(true)}
              className="mr-2"
            />
            <span>Add Co-Applicant</span>
          </label>
        </div>
      </div>

      {formData.coApplicant.hasCoApplicant && (
        <motion.div
          initial={{ opacity: 0, height: 0 }}
          animate={{ opacity: 1, height: 'auto' }}
          exit={{ opacity: 0, height: 0 }}
          transition={{ duration: 0.3 }}
          className="space-y-8"
        >
          {/* Personal Information */}
          <div className="border border-gray-200 rounded-lg p-6">
            <h4 className="font-medium text-gray-900 mb-4">Personal Information</h4>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">First Name *</label>
                <input
                  type="text"
                  className="input-field"
                  value={formData.coApplicant.personalInfo.firstName || ''}
                  onChange={(e) => updateCoApplicantData('personalInfo', { firstName: e.target.value })}
                  placeholder="Enter first name"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Last Name *</label>
                <input
                  type="text"
                  className="input-field"
                  value={formData.coApplicant.personalInfo.lastName || ''}
                  onChange={(e) => updateCoApplicantData('personalInfo', { lastName: e.target.value })}
                  placeholder="Enter last name"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Date of Birth *</label>
                <input
                  type="date"
                  className="input-field"
                  value={formData.coApplicant.personalInfo.dateOfBirth || ''}
                  onChange={(e) => updateCoApplicantData('personalInfo', { dateOfBirth: e.target.value })}
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Relationship *</label>
                <select
                  className="input-field"
                  value={formData.coApplicant.personalInfo.relationship || ''}
                  onChange={(e) => updateCoApplicantData('personalInfo', { relationship: e.target.value })}
                >
                  <option value="">Select Relationship</option>
                  <option value="SPOUSE">Spouse</option>
                  <option value="FATHER">Father</option>
                  <option value="MOTHER">Mother</option>
                  <option value="BROTHER">Brother</option>
                  <option value="SISTER">Sister</option>
                  <option value="SON">Son</option>
                  <option value="DAUGHTER">Daughter</option>
                </select>
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">PAN Number *</label>
                <input
                  type="text"
                  className="input-field"
                  value={formData.coApplicant.personalInfo.panNumber || ''}
                  onChange={(e) => updateCoApplicantData('personalInfo', { panNumber: e.target.value.toUpperCase() })}
                  placeholder="ABCDE1234F"
                  maxLength="10"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Phone Number *</label>
                <input
                  type="tel"
                  className="input-field"
                  value={formData.coApplicant.personalInfo.phoneNumber || ''}
                  onChange={(e) => updateCoApplicantData('personalInfo', { phoneNumber: e.target.value })}
                  placeholder="9876543210"
                  maxLength="10"
                />
              </div>
            </div>
          </div>

          {/* Financial Information */}
          <div className="border border-gray-200 rounded-lg p-6">
            <h4 className="font-medium text-gray-900 mb-4">Financial Information</h4>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Employment Type *</label>
                <select
                  className="input-field"
                  value={formData.coApplicant.financialInfo.employmentType || ''}
                  onChange={(e) => updateCoApplicantData('financialInfo', { employmentType: e.target.value })}
                >
                  <option value="">Select Employment Type</option>
                  <option value="SALARIED">Salaried</option>
                  <option value="SELF_EMPLOYED">Self Employed</option>
                  <option value="BUSINESS">Business</option>
                  <option value="HOMEMAKER">Homemaker</option>
                  <option value="RETIRED">Retired</option>
                </select>
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Monthly Income</label>
                <input
                  type="number"
                  className="input-field"
                  value={formData.coApplicant.financialInfo.monthlyIncome || ''}
                  onChange={(e) => updateCoApplicantData('financialInfo', { monthlyIncome: e.target.value })}
                  placeholder="25000"
                  min="0"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Employer/Company</label>
                <input
                  type="text"
                  className="input-field"
                  value={formData.coApplicant.financialInfo.employer || ''}
                  onChange={(e) => updateCoApplicantData('financialInfo', { employer: e.target.value })}
                  placeholder="Company name"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Work Experience (Years)</label>
                <input
                  type="number"
                  className="input-field"
                  value={formData.coApplicant.financialInfo.workExperience || ''}
                  onChange={(e) => updateCoApplicantData('financialInfo', { workExperience: e.target.value })}
                  placeholder="3"
                  min="0"
                  max="50"
                />
              </div>
            </div>
          </div>

          {/* Benefits of Co-Applicant */}
          <div className="bg-green-50 border border-green-200 rounded-lg p-4">
            <h4 className="font-medium text-green-800 mb-2">Benefits of Adding Co-Applicant:</h4>
            <ul className="text-sm text-green-700 space-y-1">
              <li>• Higher loan eligibility based on combined income</li>
              <li>• Better interest rates and loan terms</li>
              <li>• Shared responsibility for loan repayment</li>
              <li>• Tax benefits for both applicants</li>
            </ul>
          </div>
        </motion.div>
      )}
    </div>
  );
};

export default CoApplicantStep;
