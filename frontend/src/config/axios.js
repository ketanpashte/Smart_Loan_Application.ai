import axios from 'axios';

// Create axios instance with base configuration
const api = axios.create({
  baseURL: '', // Use relative URLs to work with Vite proxy
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor to add auth token
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    console.log('Request interceptor - URL:', config.url, 'Token:', token ? 'present' : 'missing');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
      console.log('Authorization header set:', config.headers.Authorization.substring(0, 20) + '...');
    }
    return config;
  },
  (error) => {
    console.error('Request interceptor error:', error);
    return Promise.reject(error);
  }
);

// Response interceptor to handle errors
api.interceptors.response.use(
  (response) => {
    console.log('Response interceptor - Status:', response.status, 'URL:', response.config.url);
    return response;
  },
  (error) => {
    console.error('Response interceptor error:', {
      status: error.response?.status,
      data: error.response?.data,
      url: error.config?.url
    });
    if (error.response?.status === 401) {
      console.log('401 Unauthorized - removing token and redirecting');
      // Token expired or invalid
      localStorage.removeItem('token');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export default api;
