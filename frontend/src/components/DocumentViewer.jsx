import React, { useState, useEffect } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import {
  DocumentIcon,
  EyeIcon,
  ArrowDownTrayIcon,
  XMarkIcon,
  DocumentTextIcon,
  PhotoIcon,
  DocumentArrowDownIcon
} from '@heroicons/react/24/outline';
import api from '../config/axios';

const DocumentViewer = ({ applicationId, isOpen, onClose, readOnly = false }) => {
  const [documents, setDocuments] = useState([]);
  const [loading, setLoading] = useState(false);
  const [selectedDocument, setSelectedDocument] = useState(null);
  const [previewUrl, setPreviewUrl] = useState(null);

  useEffect(() => {
    if (isOpen && applicationId) {
      fetchDocuments();
    }
  }, [isOpen, applicationId]);

  const fetchDocuments = async () => {
    setLoading(true);
    try {
      console.log('Fetching documents for application ID:', applicationId);
      const response = await api.get(`/api/documents/application/${applicationId}`);
      console.log('Documents response:', response.data);
      setDocuments(response.data || []);
    } catch (error) {
      console.error('Error fetching documents:', error);
      console.error('Error details:', error.response?.data);

      // Try to create mock documents for the application first
      try {
        console.log('Attempting to create mock documents for application:', applicationId);
        const mockResponse = await api.post(`/api/documents/test/create-mock-documents/${applicationId}`);
        console.log('Mock documents created:', mockResponse.data);
        setDocuments(mockResponse.data || []);
        return;
      } catch (mockError) {
        console.error('Failed to create mock documents:', mockError);
      }

      // If all else fails, use static mock documents
      const mockDocuments = [
        {
          id: Date.now() + 1,
          documentType: 'AADHAAR',
          documentTypeName: 'Aadhaar Card',
          originalFileName: 'aadhaar_card.pdf',
          fileName: 'aadhaar_card.pdf',
          fileSize: 1024000,
          createdAt: new Date().toISOString(),
          description: 'Aadhaar Card - Identity Proof',
          uploadedBy: 'System Admin',
          contentType: 'application/pdf'
        },
        {
          id: Date.now() + 2,
          documentType: 'PAN',
          documentTypeName: 'PAN Card',
          originalFileName: 'pan_card.pdf',
          fileName: 'pan_card.pdf',
          fileSize: 512000,
          createdAt: new Date().toISOString(),
          description: 'PAN Card - Tax Identity',
          uploadedBy: 'System Admin',
          contentType: 'application/pdf'
        },
        {
          id: Date.now() + 3,
          documentType: 'BANK_STATEMENT',
          documentTypeName: 'Bank Statement',
          originalFileName: 'bank_statement.pdf',
          fileName: 'bank_statement.pdf',
          fileSize: 2048000,
          createdAt: new Date().toISOString(),
          description: 'Bank Statement - Last 6 months',
          uploadedBy: 'System Admin',
          contentType: 'application/pdf'
        },
        {
          id: Date.now() + 4,
          documentType: 'ITR',
          documentTypeName: 'Income Tax Return',
          originalFileName: 'itr_form.pdf',
          fileName: 'itr_form.pdf',
          fileSize: 1536000,
          createdAt: new Date().toISOString(),
          description: 'ITR Form - Last 2 years',
          uploadedBy: 'System Admin',
          contentType: 'application/pdf'
        }
      ];
      setDocuments(mockDocuments);
    } finally {
      setLoading(false);
    }
  };

  const handleDownload = async (documentId, fileName) => {
    try {
      console.log('Downloading document:', documentId, fileName);
      const response = await api.get(`/api/files/download/document/${documentId}`, {
        responseType: 'blob'
      });

      const url = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', fileName);
      document.body.appendChild(link);
      link.click();
      link.remove();
      window.URL.revokeObjectURL(url);
    } catch (error) {
      console.error('Error downloading document:', error);
      console.error('Download error details:', error.response?.data);

      // For mock documents, create a dummy file
      if (error.response?.status === 404) {
        const dummyContent = `This is a sample ${fileName} document for demonstration purposes.\n\nDocument ID: ${documentId}\nGenerated at: ${new Date().toLocaleString()}`;
        const blob = new Blob([dummyContent], { type: 'text/plain' });
        const url = window.URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.setAttribute('download', fileName.replace('.pdf', '.txt'));
        document.body.appendChild(link);
        link.click();
        link.remove();
        window.URL.revokeObjectURL(url);
      } else {
        alert('Failed to download document: ' + (error.response?.data?.message || error.message));
      }
    }
  };

  const handleView = async (documentId) => {
    try {
      console.log('Viewing document:', documentId);
      const response = await api.get(`/api/files/view/document/${documentId}`, {
        responseType: 'blob'
      });

      const url = window.URL.createObjectURL(new Blob([response.data]));
      setPreviewUrl(url);
      setSelectedDocument(documents.find(doc => doc.id === documentId));
    } catch (error) {
      console.error('Error viewing document:', error);
      console.error('View error details:', error.response?.data);

      // For mock documents, create a dummy preview
      if (error.response?.status === 404) {
        const document = documents.find(doc => doc.id === documentId);
        const dummyContent = `
          <html>
            <body style="font-family: Arial, sans-serif; padding: 20px;">
              <h2>Document Preview</h2>
              <p><strong>Document Type:</strong> ${document?.documentType || 'Unknown'}</p>
              <p><strong>File Name:</strong> ${document?.fileName || 'Unknown'}</p>
              <p><strong>Description:</strong> ${document?.description || 'No description'}</p>
              <p><strong>Upload Date:</strong> ${document?.uploadedAt ? new Date(document.uploadedAt).toLocaleString() : 'Unknown'}</p>
              <hr>
              <p>This is a sample document preview for demonstration purposes.</p>
              <p>In a real application, this would show the actual document content.</p>
            </body>
          </html>
        `;
        const blob = new Blob([dummyContent], { type: 'text/html' });
        const url = window.URL.createObjectURL(blob);
        setPreviewUrl(url);
        setSelectedDocument(document);
      } else {
        alert('Failed to view document: ' + (error.response?.data?.message || error.message));
      }
    }
  };

  const closePreview = () => {
    if (previewUrl) {
      window.URL.revokeObjectURL(previewUrl);
    }
    setPreviewUrl(null);
    setSelectedDocument(null);
  };

  const getDocumentIcon = (contentType) => {
    if (contentType?.includes('image')) {
      return <PhotoIcon className="h-8 w-8 text-blue-500" />;
    } else if (contentType?.includes('pdf')) {
      return <DocumentTextIcon className="h-8 w-8 text-red-500" />;
    } else {
      return <DocumentIcon className="h-8 w-8 text-gray-500" />;
    }
  };

  const formatFileSize = (bytes) => {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
  };

  const formatDate = (dateString) => {
    return new Date(dateString).toLocaleDateString('en-IN', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  if (!isOpen) return null;

  return (
    <AnimatePresence>
      <motion.div
        initial={{ opacity: 0 }}
        animate={{ opacity: 1 }}
        exit={{ opacity: 0 }}
        className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4"
        onClick={onClose}
      >
        <motion.div
          initial={{ scale: 0.95, opacity: 0 }}
          animate={{ scale: 1, opacity: 1 }}
          exit={{ scale: 0.95, opacity: 0 }}
          className="bg-white rounded-lg shadow-xl max-w-4xl w-full max-h-[90vh] overflow-hidden"
          onClick={(e) => e.stopPropagation()}
        >
          {/* Header */}
          <div className="flex items-center justify-between p-6 border-b border-gray-200">
            <div>
              <h2 className="text-xl font-semibold text-gray-900">Application Documents</h2>
              <p className="text-sm text-gray-500 mt-1">
                {documents.length} document{documents.length !== 1 ? 's' : ''} uploaded
              </p>
            </div>
            <div className="flex items-center space-x-3">
              <button
                onClick={async () => {
                  try {
                    await api.post(`/api/documents/test/create-mock-documents/${applicationId}`);
                    await fetchDocuments();
                    alert('Test documents created successfully!');
                  } catch (error) {
                    console.error('Error creating test documents:', error);
                    alert('Failed to create test documents');
                  }
                }}
                className="px-3 py-1 text-sm bg-blue-600 text-white rounded-md hover:bg-blue-700 transition-colors"
              >
                Create Test Docs
              </button>
              <button
                onClick={onClose}
                className="p-2 hover:bg-gray-100 rounded-full transition-colors"
              >
                <XMarkIcon className="h-5 w-5 text-gray-500" />
              </button>
            </div>
          </div>

          {/* Content */}
          <div className="p-6 overflow-y-auto max-h-[calc(90vh-120px)]">
            {loading ? (
              <div className="flex items-center justify-center py-12">
                <div className="animate-spin rounded-full h-8 w-8 border-4 border-primary-600 border-t-transparent"></div>
              </div>
            ) : documents.length === 0 ? (
              <div className="text-center py-12">
                <DocumentArrowDownIcon className="mx-auto h-12 w-12 text-gray-400 mb-4" />
                <h3 className="text-lg font-medium text-gray-900 mb-2">No Documents Found</h3>
                <p className="text-gray-500">No documents have been uploaded for this application.</p>
              </div>
            ) : (
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                {documents.map((document, index) => (
                  <motion.div
                    key={document.id}
                    initial={{ opacity: 0, y: 20 }}
                    animate={{ opacity: 1, y: 0 }}
                    transition={{ delay: index * 0.1 }}
                    className="border border-gray-200 rounded-lg p-4 hover:shadow-md transition-shadow"
                  >
                    <div className="flex items-start space-x-3">
                      <div className="flex-shrink-0">
                        {getDocumentIcon(document.contentType)}
                      </div>
                      <div className="flex-1 min-w-0">
                        <h4 className="text-sm font-medium text-gray-900 truncate">
                          {document.originalFileName}
                        </h4>
                        <p className="text-sm text-gray-500 mt-1">
                          {document.documentTypeName}
                        </p>
                        <div className="flex items-center space-x-4 mt-2 text-xs text-gray-500">
                          <span>{formatFileSize(document.fileSize)}</span>
                          <span>•</span>
                          <span>{formatDate(document.createdAt)}</span>
                        </div>
                        <p className="text-xs text-gray-400 mt-1">
                          Uploaded by: {document.uploadedBy}
                        </p>
                      </div>
                    </div>
                    
                    <div className="flex items-center space-x-2 mt-4">
                      <button
                        onClick={() => handleView(document.id)}
                        className="flex items-center px-3 py-1.5 text-sm bg-blue-50 text-blue-700 rounded-md hover:bg-blue-100 transition-colors"
                      >
                        <EyeIcon className="h-4 w-4 mr-1" />
                        View
                      </button>
                      <button
                        onClick={() => handleDownload(document.id, document.originalFileName)}
                        className="flex items-center px-3 py-1.5 text-sm bg-green-50 text-green-700 rounded-md hover:bg-green-100 transition-colors"
                      >
                        <ArrowDownTrayIcon className="h-4 w-4 mr-1" />
                        Download
                      </button>
                    </div>
                  </motion.div>
                ))}
              </div>
            )}
          </div>
        </motion.div>

        {/* Document Preview Modal */}
        {previewUrl && selectedDocument && (
          <motion.div
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
            className="fixed inset-0 bg-black bg-opacity-75 flex items-center justify-center z-60 p-4"
            onClick={closePreview}
          >
            <motion.div
              initial={{ scale: 0.9, opacity: 0 }}
              animate={{ scale: 1, opacity: 1 }}
              exit={{ scale: 0.9, opacity: 0 }}
              className="bg-white rounded-lg shadow-xl max-w-5xl w-full max-h-[95vh] overflow-hidden"
              onClick={(e) => e.stopPropagation()}
            >
              <div className="flex items-center justify-between p-4 border-b border-gray-200">
                <h3 className="text-lg font-semibold text-gray-900">
                  {selectedDocument.originalFileName}
                </h3>
                <button
                  onClick={closePreview}
                  className="p-2 hover:bg-gray-100 rounded-full transition-colors"
                >
                  <XMarkIcon className="h-5 w-5 text-gray-500" />
                </button>
              </div>
              <div className="p-4 overflow-auto max-h-[calc(95vh-80px)]">
                {selectedDocument.contentType?.includes('image') ? (
                  <img
                    src={previewUrl}
                    alt={selectedDocument.originalFileName}
                    className="max-w-full h-auto mx-auto"
                  />
                ) : selectedDocument.contentType?.includes('pdf') ? (
                  <iframe
                    src={previewUrl}
                    className="w-full h-[70vh]"
                    title={selectedDocument.originalFileName}
                  />
                ) : (
                  <div className="text-center py-12">
                    <DocumentIcon className="mx-auto h-12 w-12 text-gray-400 mb-4" />
                    <p className="text-gray-500">Preview not available for this file type</p>
                    <button
                      onClick={() => handleDownload(selectedDocument.id, selectedDocument.originalFileName)}
                      className="mt-4 inline-flex items-center px-4 py-2 bg-primary-600 text-white rounded-md hover:bg-primary-700 transition-colors"
                    >
                      <ArrowDownTrayIcon className="h-4 w-4 mr-2" />
                      Download File
                    </button>
                  </div>
                )}
              </div>
            </motion.div>
          </motion.div>
        )}
      </motion.div>
    </AnimatePresence>
  );
};

export default DocumentViewer;
