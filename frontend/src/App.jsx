import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './contexts/AuthContext';
import ProtectedRoute from './components/ProtectedRoute';

// Auth Components
import Login from './pages/auth/Login';
import Register from './pages/auth/Register';
import Unauthorized from './pages/Unauthorized';

// Dashboard Components
import AdminDashboard from './pages/admin/Dashboard';
import AdminApplications from './pages/admin/Applications';
import AdminUsers from './pages/admin/Users';

// Sales Components
import NewLoan from './pages/sales/NewLoan';

// RCPU Components
import RcpuIndex from './pages/rcpu/Index';
import RcpuDetails from './pages/rcpu/Details';

// L1 Manager Components
import L1Index from './pages/l1/Index';
import L1Details from './pages/l1/Details';

// L2 Manager Components
import L2Index from './pages/l2/Index';
import L2Details from './pages/l2/Details';

// L3 Admin Components
import L3Index from './pages/l3/Index';
import L3Details from './pages/l3/Details';

function App() {
  return (
    <AuthProvider>
      <Router>
        <div className="min-h-screen bg-gray-50">
          <Routes>
            {/* Public Routes */}
            <Route path="/login" element={<Login />} />
            <Route path="/unauthorized" element={<Unauthorized />} />

            {/* Admin Routes */}
            <Route path="/admin/dashboard" element={
              <ProtectedRoute allowedRoles={['ADMIN']}>
                <AdminDashboard />
              </ProtectedRoute>
            } />
            <Route path="/admin/applications" element={
              <ProtectedRoute allowedRoles={['ADMIN']}>
                <AdminApplications />
              </ProtectedRoute>
            } />
            <Route path="/admin/users" element={
              <ProtectedRoute allowedRoles={['ADMIN']}>
                <AdminUsers />
              </ProtectedRoute>
            } />
            <Route path="/register" element={
              <ProtectedRoute allowedRoles={['ADMIN']}>
                <Register />
              </ProtectedRoute>
            } />

            {/* Sales Executive Routes */}
            <Route path="/sales/new-loan" element={
              <ProtectedRoute allowedRoles={['SALES_EXECUTIVE']}>
                <NewLoan />
              </ProtectedRoute>
            } />

            {/* RCPU/Underwriter Routes */}
            <Route path="/rcpu/index" element={
              <ProtectedRoute allowedRoles={['UNDERWRITER']}>
                <RcpuIndex />
              </ProtectedRoute>
            } />
            <Route path="/rcpu/details/:id" element={
              <ProtectedRoute allowedRoles={['UNDERWRITER']}>
                <RcpuDetails />
              </ProtectedRoute>
            } />

            {/* L1 Manager Routes */}
            <Route path="/l1/index" element={
              <ProtectedRoute allowedRoles={['MANAGER_L1']}>
                <L1Index />
              </ProtectedRoute>
            } />
            <Route path="/l1/details/:id" element={
              <ProtectedRoute allowedRoles={['MANAGER_L1']}>
                <L1Details />
              </ProtectedRoute>
            } />

            {/* L2 Manager Routes */}
            <Route path="/l2/index" element={
              <ProtectedRoute allowedRoles={['MANAGER_L2']}>
                <L2Index />
              </ProtectedRoute>
            } />
            <Route path="/l2/details/:id" element={
              <ProtectedRoute allowedRoles={['MANAGER_L2']}>
                <L2Details />
              </ProtectedRoute>
            } />

            {/* L3 Admin Routes */}
            <Route path="/l3/index" element={
              <ProtectedRoute allowedRoles={['ADMIN']}>
                <L3Index />
              </ProtectedRoute>
            } />
            <Route path="/l3/details/:id" element={
              <ProtectedRoute allowedRoles={['ADMIN']}>
                <L3Details />
              </ProtectedRoute>
            } />

            {/* Default redirect */}
            <Route path="/" element={<Navigate to="/login" replace />} />
          </Routes>
        </div>
      </Router>
    </AuthProvider>
  );
}

export default App;
