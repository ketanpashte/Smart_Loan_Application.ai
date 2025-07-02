import React from 'react';

const FinancialInfoStep = ({ formData, updateFormData }) => (
  <div>
    <h3 className="text-lg font-semibold mb-6">Financial Information</h3>
    <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
      <div>
        <label className="block text-sm font-medium text-gray-700 mb-1">Employment Type *</label>
        <select
          className="input-field"
          value={formData.financialInfo.employmentType}
          onChange={(e) => updateFormData('financialInfo', { employmentType: e.target.value })}
        >
          <option value="">Select Employment Type</option>
          <option value="SALARIED">Salaried</option>
          <option value="SELF_EMPLOYED">Self Employed</option>
          <option value="BUSINESS">Business</option>
          <option value="PROFESSIONAL">Professional</option>
        </select>
      </div>
      
      <div>
        <label className="block text-sm font-medium text-gray-700 mb-1">Monthly Income *</label>
        <input
          type="number"
          className="input-field"
          value={formData.financialInfo.monthlyIncome}
          onChange={(e) => updateFormData('financialInfo', { monthlyIncome: e.target.value })}
          placeholder="50000"
          min="0"
        />
      </div>
      
      <div>
        <label className="block text-sm font-medium text-gray-700 mb-1">Employer/Company *</label>
        <input
          type="text"
          className="input-field"
          value={formData.financialInfo.employer}
          onChange={(e) => updateFormData('financialInfo', { employer: e.target.value })}
          placeholder="Company name"
        />
      </div>
      
      <div>
        <label className="block text-sm font-medium text-gray-700 mb-1">Work Experience (Years) *</label>
        <input
          type="number"
          className="input-field"
          value={formData.financialInfo.workExperience}
          onChange={(e) => updateFormData('financialInfo', { workExperience: e.target.value })}
          placeholder="5"
          min="0"
          max="50"
        />
      </div>
      
      <div>
        <label className="block text-sm font-medium text-gray-700 mb-1">Loan Amount Required *</label>
        <input
          type="number"
          className="input-field"
          value={formData.financialInfo.loanAmount}
          onChange={(e) => updateFormData('financialInfo', { loanAmount: e.target.value })}
          placeholder="1000000"
          min="50000"
          max="10000000"
        />
      </div>
      
      <div>
        <label className="block text-sm font-medium text-gray-700 mb-1">Loan Purpose *</label>
        <select
          className="input-field"
          value={formData.financialInfo.loanPurpose}
          onChange={(e) => updateFormData('financialInfo', { loanPurpose: e.target.value })}
        >
          <option value="">Select Purpose</option>
          <option value="HOME_PURCHASE">Home Purchase</option>
          <option value="HOME_CONSTRUCTION">Home Construction</option>
          <option value="HOME_RENOVATION">Home Renovation</option>
          <option value="PLOT_PURCHASE">Plot Purchase</option>
          <option value="BALANCE_TRANSFER">Balance Transfer</option>
          <option value="TOP_UP">Top Up</option>
        </select>
      </div>
      
      <div>
        <label className="block text-sm font-medium text-gray-700 mb-1">Loan Tenure (Years) *</label>
        <select
          className="input-field"
          value={formData.financialInfo.loanTenure}
          onChange={(e) => updateFormData('financialInfo', { loanTenure: e.target.value })}
        >
          <option value="">Select Tenure</option>
          <option value="5">5 Years</option>
          <option value="10">10 Years</option>
          <option value="15">15 Years</option>
          <option value="20">20 Years</option>
          <option value="25">25 Years</option>
          <option value="30">30 Years</option>
        </select>
      </div>
    </div>
    
    {/* Loan Details Summary */}
    {formData.financialInfo.loanAmount && formData.financialInfo.loanTenure && (
      <div className="mt-6 p-4 bg-blue-50 rounded-lg">
        <h4 className="font-medium text-blue-900 mb-2">Loan Summary</h4>
        <div className="grid grid-cols-2 md:grid-cols-4 gap-4 text-sm">
          <div>
            <span className="text-blue-600">Loan Amount:</span>
            <p className="font-medium">₹{parseInt(formData.financialInfo.loanAmount).toLocaleString()}</p>
          </div>
          <div>
            <span className="text-blue-600">Tenure:</span>
            <p className="font-medium">{formData.financialInfo.loanTenure} Years</p>
          </div>
          <div>
            <span className="text-blue-600">Est. Interest Rate:</span>
            <p className="font-medium">8.5% p.a.</p>
          </div>
          <div>
            <span className="text-blue-600">Est. EMI:</span>
            <p className="font-medium">
              ₹{Math.round((parseInt(formData.financialInfo.loanAmount) * 0.085 * Math.pow(1.085, parseInt(formData.financialInfo.loanTenure))) / (12 * (Math.pow(1.085, parseInt(formData.financialInfo.loanTenure)) - 1))).toLocaleString()}
            </p>
          </div>
        </div>
      </div>
    )}
  </div>
);

export default FinancialInfoStep;
