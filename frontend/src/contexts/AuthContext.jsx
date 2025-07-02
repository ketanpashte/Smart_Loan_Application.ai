import React, { createContext, useContext, useState, useEffect } from 'react';
import api from '../config/axios';

const AuthContext = createContext();

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  // Set up authentication
  useEffect(() => {
    const token = localStorage.getItem('token');
    if (token) {
      // Verify token validity
      verifyToken(token);
    } else {
      setLoading(false);
    }
  }, []);

  const verifyToken = async (token) => {
    try {
      const response = await api.get('/api/auth/verify');
      const { id, name, email, role } = response.data;

      // Create user object from response data
      const userData = {
        id: id,
        name: name,
        email: email,
        role: role
      };

      setUser(userData);
    } catch (error) {
      console.error('Token verification failed:', error);
      logout();
    } finally {
      setLoading(false);
    }
  };

  const login = async (email, password) => {
    try {
      const response = await api.post('/api/auth/login', { email, password });
      const { token, userId, name, email: userEmail, role } = response.data;

      // Create user object from response data
      const userData = {
        id: userId,
        name: name,
        email: userEmail,
        role: role
      };

      localStorage.setItem('token', token);
      setUser(userData);

      return { success: true, user: userData };
    } catch (error) {
      console.error('Login failed:', error);
      return {
        success: false,
        error: error.response?.data?.message || 'Login failed'
      };
    }
  };

  const logout = () => {
    localStorage.removeItem('token');
    setUser(null);
  };

  const register = async (userData) => {
    try {
      const response = await api.post('/api/auth/register', userData);
      return { success: true, data: response.data };
    } catch (error) {
      console.error('Registration failed:', error);
      return {
        success: false,
        error: error.response?.data?.message || 'Registration failed'
      };
    }
  };

  const hasRole = (role) => {
    return user?.role === role;
  };

  const hasAnyRole = (roles) => {
    return roles.includes(user?.role);
  };

  const getDashboardRoute = () => {
    if (!user) return '/login';
    
    switch (user.role) {
      case 'SALES_EXECUTIVE':
        return '/sales/new-loan';
      case 'UNDERWRITER':
        return '/rcpu/index';
      case 'MANAGER_L1':
        return '/l1/index';
      case 'MANAGER_L2':
        return '/l2/index';
      case 'ADMIN':
        return '/admin/dashboard';
      default:
        return '/login';
    }
  };

  const value = {
    user,
    login,
    logout,
    register,
    hasRole,
    hasAnyRole,
    getDashboardRoute,
    loading
  };

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
};
