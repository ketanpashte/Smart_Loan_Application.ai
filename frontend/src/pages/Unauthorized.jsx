import React from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import { ExclamationTriangleIcon } from '@heroicons/react/24/outline';

const Unauthorized = () => {
  const { getDashboardRoute } = useAuth();

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50 py-12 px-4 sm:px-6 lg:px-8">
      <div className="max-w-md w-full space-y-8 text-center">
        <div>
          <ExclamationTriangleIcon className="mx-auto h-16 w-16 text-red-500" />
          <h2 className="mt-6 text-3xl font-bold text-gray-900">
            Access Denied
          </h2>
          <p className="mt-2 text-sm text-gray-600">
            You don't have permission to access this page.
          </p>
        </div>
        
        <div className="space-y-4">
          <Link
            to={getDashboardRoute()}
            className="w-full btn-primary py-3 text-lg font-medium"
          >
            Go to Dashboard
          </Link>
          
          <Link
            to="/login"
            className="w-full btn-secondary py-3 text-lg font-medium"
          >
            Back to Login
          </Link>
        </div>
      </div>
    </div>
  );
};

export default Unauthorized;
