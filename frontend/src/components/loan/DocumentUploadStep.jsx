import React, { useState } from 'react';
import { DocumentIcon, CheckCircleIcon, XCircleIcon } from '@heroicons/react/24/outline';
import api from '../../config/axios';

const DocumentUploadStep = ({ formData, updateFormData }) => {
  const [uploadStatus, setUploadStatus] = useState({});

  const documentTypes = [
    { key: 'itr', label: 'Income Tax Returns (Last 2 Years)', required: true, accept: '.pdf' },
    { key: 'bankStatement', label: 'Bank Statement (Last 6 Months)', required: true, accept: '.pdf' },
    { key: 'aadhaar', label: 'Aadhaar Card', required: true, accept: '.pdf,.jpg,.jpeg,.png' },
    { key: 'pan', label: 'PAN Card', required: true, accept: '.pdf,.jpg,.jpeg,.png' },
    { key: 'photo', label: 'Passport Size Photo', required: true, accept: '.jpg,.jpeg,.png' }
  ];

  const handleFileUpload = async (docType, file) => {
    if (!file) return;

    setUploadStatus(prev => ({ ...prev, [docType]: 'uploading' }));

    try {
      // If we have an application ID, upload to backend
      if (formData.applicationId) {
        const formDataToSend = new FormData();
        formDataToSend.append('file', file);
        formDataToSend.append('documentType', docType);
        formDataToSend.append('description', `${docType} document for application ${formData.applicationId}`);

        await api.post(`/api/documents/upload/${formData.applicationId}`, formDataToSend, {
          headers: {
            'Content-Type': 'multipart/form-data',
          },
        });
      }

      updateFormData('documents', { [docType]: file });
      setUploadStatus(prev => ({ ...prev, [docType]: 'success' }));
    } catch (error) {
      console.error('Error uploading document:', error);
      setUploadStatus(prev => ({ ...prev, [docType]: 'error' }));
    }
  };

  const getStatusIcon = (docType) => {
    const status = uploadStatus[docType];
    const hasFile = formData.documents[docType];

    if (status === 'uploading') {
      return <div className="animate-spin rounded-full h-5 w-5 border-2 border-blue-500 border-t-transparent"></div>;
    }
    
    if (status === 'success' || hasFile) {
      return <CheckCircleIcon className="h-5 w-5 text-green-500" />;
    }
    
    if (status === 'error') {
      return <XCircleIcon className="h-5 w-5 text-red-500" />;
    }
    
    return <DocumentIcon className="h-5 w-5 text-gray-400" />;
  };

  const getStatusText = (docType) => {
    const status = uploadStatus[docType];
    const hasFile = formData.documents[docType];

    if (status === 'uploading') return 'Uploading...';
    if (status === 'success' || hasFile) return 'Uploaded';
    if (status === 'error') return 'Upload failed';
    return 'Not uploaded';
  };

  return (
    <div>
      <h3 className="text-lg font-semibold mb-6">Document Upload</h3>
      <p className="text-gray-600 mb-6">
        Please upload the required documents. All documents should be clear and readable.
      </p>

      <div className="space-y-6">
        {documentTypes.map((doc) => (
          <div key={doc.key} className="border border-gray-200 rounded-lg p-6">
            <div className="flex items-start justify-between mb-4">
              <div className="flex-1">
                <h4 className="font-medium text-gray-900 flex items-center">
                  {doc.label}
                  {doc.required && <span className="text-red-500 ml-1">*</span>}
                </h4>
                <p className="text-sm text-gray-500 mt-1">
                  Accepted formats: {doc.accept.replace(/\./g, '').toUpperCase()}
                </p>
              </div>
              <div className="flex items-center space-x-2 ml-4">
                {getStatusIcon(doc.key)}
                <span className={`text-sm ${
                  uploadStatus[doc.key] === 'success' || formData.documents[doc.key] 
                    ? 'text-green-600' 
                    : uploadStatus[doc.key] === 'error' 
                    ? 'text-red-600' 
                    : 'text-gray-500'
                }`}>
                  {getStatusText(doc.key)}
                </span>
              </div>
            </div>

            <div className="flex items-center space-x-4">
              <label className="cursor-pointer">
                <input
                  type="file"
                  className="hidden"
                  accept={doc.accept}
                  onChange={(e) => handleFileUpload(doc.key, e.target.files[0])}
                  disabled={uploadStatus[doc.key] === 'uploading'}
                />
                <span className={`inline-flex items-center px-4 py-2 border border-gray-300 rounded-md shadow-sm text-sm font-medium text-gray-700 bg-white hover:bg-gray-50 ${
                  uploadStatus[doc.key] === 'uploading' ? 'opacity-50 cursor-not-allowed' : 'cursor-pointer'
                }`}>
                  <DocumentIcon className="h-4 w-4 mr-2" />
                  {formData.documents[doc.key] ? 'Replace File' : 'Choose File'}
                </span>
              </label>

              {formData.documents[doc.key] && (
                <div className="flex items-center text-sm text-gray-600">
                  <span className="truncate max-w-xs">
                    {formData.documents[doc.key].name}
                  </span>
                  <span className="ml-2 text-gray-400">
                    ({(formData.documents[doc.key].size / 1024 / 1024).toFixed(2)} MB)
                  </span>
                </div>
              )}
            </div>

            {uploadStatus[doc.key] === 'error' && (
              <p className="text-red-600 text-sm mt-2">
                Upload failed. Please try again.
              </p>
            )}
          </div>
        ))}
      </div>

      <div className="mt-6 p-4 bg-yellow-50 border border-yellow-200 rounded-lg">
        <h4 className="font-medium text-yellow-800 mb-2">Important Notes:</h4>
        <ul className="text-sm text-yellow-700 space-y-1">
          <li>• Maximum file size: 5MB per document</li>
          <li>• Documents should be clear and all text should be readable</li>
          <li>• Ensure all pages are included for multi-page documents</li>
          <li>• Original documents will be verified during processing</li>
        </ul>
      </div>
    </div>
  );
};

export default DocumentUploadStep;
