import React, { useState } from 'react';
import { motion } from 'framer-motion';
import { CheckCircleIcon, XCircleIcon, ExclamationTriangleIcon } from '@heroicons/react/24/outline';

const EligibilityCheckStep = ({ formData, eligibilityResult, setEligibilityResult }) => {
  const [checking, setChecking] = useState(false);

  const runEligibilityCheck = async () => {
    setChecking(true);
    
    // Simulate AI eligibility check
    await new Promise(resolve => setTimeout(resolve, 3000));
    
    const income = parseInt(formData.financialInfo.monthlyIncome) || 0;
    const loanAmount = parseInt(formData.financialInfo.loanAmount) || 0;
    const coApplicantIncome = formData.coApplicant.hasCoApplicant 
      ? parseInt(formData.coApplicant.financialInfo.monthlyIncome) || 0 
      : 0;
    
    const totalIncome = income + coApplicantIncome;
    const emi = Math.round((loanAmount * 0.085 * Math.pow(1.085, parseInt(formData.financialInfo.loanTenure))) / (12 * (Math.pow(1.085, parseInt(formData.financialInfo.loanTenure)) - 1)));
    const emiToIncomeRatio = (emi / totalIncome) * 100;
    
    let status = 'APPROVED';
    let score = 85;
    let maxLoanAmount = totalIncome * 60; // 60x monthly income
    
    if (emiToIncomeRatio > 50) {
      status = 'REJECTED';
      score = 45;
    } else if (emiToIncomeRatio > 40) {
      status = 'CONDITIONAL';
      score = 65;
      maxLoanAmount = totalIncome * 40;
    }
    
    const result = {
      status,
      score,
      maxLoanAmount,
      recommendedLoanAmount: Math.min(loanAmount, maxLoanAmount),
      emiToIncomeRatio: emiToIncomeRatio.toFixed(1),
      estimatedEmi: emi,
      factors: {
        income: totalIncome >= 30000 ? 'GOOD' : totalIncome >= 20000 ? 'AVERAGE' : 'POOR',
        employment: formData.financialInfo.employmentType === 'SALARIED' ? 'GOOD' : 'AVERAGE',
        experience: parseInt(formData.financialInfo.workExperience) >= 2 ? 'GOOD' : 'AVERAGE',
        coApplicant: formData.coApplicant.hasCoApplicant ? 'GOOD' : 'AVERAGE'
      },
      recommendations: []
    };
    
    // Add recommendations
    if (emiToIncomeRatio > 40) {
      result.recommendations.push('Consider reducing loan amount to improve eligibility');
    }
    if (!formData.coApplicant.hasCoApplicant && totalIncome < 50000) {
      result.recommendations.push('Adding a co-applicant can improve your eligibility');
    }
    if (parseInt(formData.financialInfo.workExperience) < 2) {
      result.recommendations.push('Higher work experience can improve loan terms');
    }
    
    setEligibilityResult(result);
    setChecking(false);
  };

  const getStatusColor = (status) => {
    switch (status) {
      case 'APPROVED': return 'text-green-600';
      case 'CONDITIONAL': return 'text-yellow-600';
      case 'REJECTED': return 'text-red-600';
      default: return 'text-gray-600';
    }
  };

  const getStatusIcon = (status) => {
    switch (status) {
      case 'APPROVED': return <CheckCircleIcon className="h-8 w-8 text-green-500" />;
      case 'CONDITIONAL': return <ExclamationTriangleIcon className="h-8 w-8 text-yellow-500" />;
      case 'REJECTED': return <XCircleIcon className="h-8 w-8 text-red-500" />;
      default: return null;
    }
  };

  const getFactorIcon = (factor) => {
    switch (factor) {
      case 'GOOD': return <CheckCircleIcon className="h-5 w-5 text-green-500" />;
      case 'AVERAGE': return <ExclamationTriangleIcon className="h-5 w-5 text-yellow-500" />;
      case 'POOR': return <XCircleIcon className="h-5 w-5 text-red-500" />;
      default: return null;
    }
  };

  return (
    <div>
      <h3 className="text-lg font-semibold mb-6">Eligibility Check</h3>
      
      {!eligibilityResult && !checking && (
        <div className="text-center py-8">
          <p className="text-gray-600 mb-6">
            Run an AI-powered eligibility check to see your loan approval chances and get personalized recommendations.
          </p>
          <motion.button
            whileHover={{ scale: 1.02 }}
            whileTap={{ scale: 0.98 }}
            onClick={runEligibilityCheck}
            className="btn-primary px-8 py-3 text-lg"
          >
            Run Eligibility Check
          </motion.button>
        </div>
      )}

      {checking && (
        <div className="text-center py-8">
          <div className="animate-spin rounded-full h-12 w-12 border-4 border-primary-600 border-t-transparent mx-auto mb-4"></div>
          <p className="text-gray-600">Analyzing your application...</p>
          <p className="text-sm text-gray-500 mt-2">This may take a few moments</p>
        </div>
      )}

      {eligibilityResult && (
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          className="space-y-6"
        >
          {/* Main Result */}
          <div className={`border-2 rounded-lg p-6 ${
            eligibilityResult.status === 'APPROVED' ? 'border-green-200 bg-green-50' :
            eligibilityResult.status === 'CONDITIONAL' ? 'border-yellow-200 bg-yellow-50' :
            'border-red-200 bg-red-50'
          }`}>
            <div className="flex items-center justify-between mb-4">
              <div className="flex items-center space-x-3">
                {getStatusIcon(eligibilityResult.status)}
                <div>
                  <h4 className={`text-xl font-semibold ${getStatusColor(eligibilityResult.status)}`}>
                    {eligibilityResult.status === 'APPROVED' ? 'Congratulations!' :
                     eligibilityResult.status === 'CONDITIONAL' ? 'Conditional Approval' :
                     'Application Needs Review'}
                  </h4>
                  <p className="text-gray-600">
                    Eligibility Score: {eligibilityResult.score}/100
                  </p>
                </div>
              </div>
              <div className="text-right">
                <p className="text-sm text-gray-500">Max Loan Amount</p>
                <p className="text-lg font-semibold">₹{eligibilityResult.maxLoanAmount.toLocaleString()}</p>
              </div>
            </div>
            
            <div className="grid grid-cols-2 md:grid-cols-4 gap-4 text-sm">
              <div>
                <span className="text-gray-600">Requested Amount:</span>
                <p className="font-medium">₹{parseInt(formData.financialInfo.loanAmount).toLocaleString()}</p>
              </div>
              <div>
                <span className="text-gray-600">Recommended Amount:</span>
                <p className="font-medium">₹{eligibilityResult.recommendedLoanAmount.toLocaleString()}</p>
              </div>
              <div>
                <span className="text-gray-600">EMI to Income Ratio:</span>
                <p className="font-medium">{eligibilityResult.emiToIncomeRatio}%</p>
              </div>
              <div>
                <span className="text-gray-600">Estimated EMI:</span>
                <p className="font-medium">₹{eligibilityResult.estimatedEmi.toLocaleString()}</p>
              </div>
            </div>
          </div>

          {/* Factors Analysis */}
          <div className="card p-6">
            <h4 className="font-semibold mb-4">Eligibility Factors</h4>
            <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
              <div className="flex items-center space-x-2">
                {getFactorIcon(eligibilityResult.factors.income)}
                <div>
                  <p className="text-sm font-medium">Income</p>
                  <p className="text-xs text-gray-500">{eligibilityResult.factors.income}</p>
                </div>
              </div>
              <div className="flex items-center space-x-2">
                {getFactorIcon(eligibilityResult.factors.employment)}
                <div>
                  <p className="text-sm font-medium">Employment</p>
                  <p className="text-xs text-gray-500">{eligibilityResult.factors.employment}</p>
                </div>
              </div>
              <div className="flex items-center space-x-2">
                {getFactorIcon(eligibilityResult.factors.experience)}
                <div>
                  <p className="text-sm font-medium">Experience</p>
                  <p className="text-xs text-gray-500">{eligibilityResult.factors.experience}</p>
                </div>
              </div>
              <div className="flex items-center space-x-2">
                {getFactorIcon(eligibilityResult.factors.coApplicant)}
                <div>
                  <p className="text-sm font-medium">Co-Applicant</p>
                  <p className="text-xs text-gray-500">{eligibilityResult.factors.coApplicant}</p>
                </div>
              </div>
            </div>
          </div>

          {/* Recommendations */}
          {eligibilityResult.recommendations.length > 0 && (
            <div className="card p-6">
              <h4 className="font-semibold mb-4">Recommendations</h4>
              <ul className="space-y-2">
                {eligibilityResult.recommendations.map((rec, index) => (
                  <li key={index} className="flex items-start space-x-2">
                    <span className="text-blue-500 mt-1">•</span>
                    <span className="text-gray-700">{rec}</span>
                  </li>
                ))}
              </ul>
            </div>
          )}

          <div className="text-center">
            <button
              onClick={runEligibilityCheck}
              className="btn-secondary"
            >
              Re-run Eligibility Check
            </button>
          </div>
        </motion.div>
      )}
    </div>
  );
};

export default EligibilityCheckStep;
