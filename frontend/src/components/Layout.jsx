import React, { useState } from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import { motion } from 'framer-motion';
import {
  HomeIcon,
  DocumentTextIcon,
  UserGroupIcon,
  ChartBarIcon,
  CogIcon,
  ArrowLeftStartOnRectangleIcon as LogoutIcon,
  Bars3Icon as MenuIcon,
  XMarkIcon as XIcon,
  BellIcon
} from '@heroicons/react/24/outline';

const Layout = ({ children, title }) => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const [sidebarOpen, setSidebarOpen] = useState(false);

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const getNavigationItems = () => {
    const baseItems = [];

    switch (user?.role) {
      case 'SALES_EXECUTIVE':
        baseItems.push(
          { name: 'New Loan Application', href: '/sales/new-loan', icon: DocumentTextIcon }
        );
        break;
      case 'UNDERWRITER':
        baseItems.push(
          { name: 'RCPU Review', href: '/rcpu/index', icon: DocumentTextIcon }
        );
        break;
      case 'MANAGER_L1':
        baseItems.push(
          { name: 'L1 Approvals', href: '/l1/index', icon: DocumentTextIcon }
        );
        break;
      case 'MANAGER_L2':
        baseItems.push(
          { name: 'L2 Approvals', href: '/l2/index', icon: DocumentTextIcon }
        );
        break;
      case 'ADMIN':
        baseItems.push(
          { name: 'Dashboard', href: '/admin/dashboard', icon: HomeIcon },
          { name: 'Applications', href: '/admin/applications', icon: DocumentTextIcon },
          { name: 'Users', href: '/admin/users', icon: UserGroupIcon },
          { name: 'L3 Approvals', href: '/l3/index', icon: ChartBarIcon },
          { name: 'User Registration', href: '/register', icon: CogIcon }
        );
        break;
    }

    return baseItems;
  };

  const navigationItems = getNavigationItems();

  return (
    <div className="min-h-screen bg-gradient-primary">
      {/* Mobile sidebar */}
      <div className={`fixed inset-0 z-40 lg:hidden ${sidebarOpen ? 'block' : 'hidden'}`}>
        <div className="fixed inset-0 bg-gray-600 bg-opacity-75" onClick={() => setSidebarOpen(false)}></div>
        <motion.div
          initial={{ x: -300 }}
          animate={{ x: sidebarOpen ? 0 : -300 }}
          transition={{ duration: 0.3 }}
          className="relative flex flex-col w-64 glass shadow-2xl"
        >
          <div className="flex items-center justify-between p-4 border-b">
            <h1 className="text-xl font-bold bg-gradient-to-r from-blue-600 to-purple-600 bg-clip-text text-transparent">SmartLoan</h1>
            <button onClick={() => setSidebarOpen(false)}>
              <XIcon className="w-6 h-6" />
            </button>
          </div>
          <nav className="flex-1 px-4 py-6 space-y-2">
            {navigationItems.map((item) => (
              <Link
                key={item.name}
                to={item.href}
                className={`flex items-center px-3 py-2 text-sm font-medium rounded-lg transition-colors ${
                  location.pathname === item.href
                    ? 'bg-primary-100 text-primary-700'
                    : 'text-gray-700 hover:bg-gray-100'
                }`}
                onClick={() => setSidebarOpen(false)}
              >
                <item.icon className="w-5 h-5 mr-3" />
                {item.name}
              </Link>
            ))}
          </nav>
        </motion.div>
      </div>

      {/* Desktop sidebar */}
      <div className="hidden lg:fixed lg:inset-y-0 lg:flex lg:w-64 lg:flex-col">
        <div className="flex flex-col flex-grow glass border-r border-white/20 shadow-2xl">
          <div className="flex items-center px-6 py-4 border-b border-white/20">
            <h1 className="text-xl font-bold bg-gradient-to-r from-blue-600 to-purple-600 bg-clip-text text-transparent">SmartLoan</h1>
          </div>
          <nav className="flex-1 px-4 py-6 space-y-2">
            {navigationItems.map((item) => (
              <Link
                key={item.name}
                to={item.href}
                className={`flex items-center px-4 py-3 text-sm font-semibold rounded-xl transition-all duration-300 hover-lift ${
                  location.pathname === item.href
                    ? 'bg-gradient-to-r from-blue-500 to-purple-600 text-white shadow-lg'
                    : 'text-gray-700 hover:bg-white/20 hover:text-gray-900'
                }`}
              >
                <item.icon className="w-5 h-5 mr-3" />
                {item.name}
              </Link>
            ))}
          </nav>
        </div>
      </div>

      {/* Main content */}
      <div className="lg:pl-64">
        {/* Top navigation */}
        <div className="sticky top-0 z-10 glass border-b border-white/20 shadow-xl">
          <div className="flex items-center justify-between px-4 py-3">
            <div className="flex items-center">
              <button
                className="lg:hidden"
                onClick={() => setSidebarOpen(true)}
              >
                <MenuIcon className="w-6 h-6" />
              </button>
              <h2 className="ml-4 text-lg font-bold text-gray-800 lg:ml-0">
                {title}
              </h2>
            </div>
            <div className="flex items-center space-x-4">
              <button className="p-2 text-gray-400 hover:text-gray-600">
                <BellIcon className="w-6 h-6" />
              </button>
              <div className="flex items-center space-x-3">
                <div className="text-right">
                  <p className="text-sm font-medium text-gray-900">{user?.name}</p>
                  <p className="text-xs text-gray-500">{user?.role?.replace('_', ' ')}</p>
                </div>
                <button
                  onClick={handleLogout}
                  className="p-2 text-gray-400 hover:text-gray-600"
                  title="Logout"
                >
                  <LogoutIcon className="w-5 h-5" />
                </button>
              </div>
            </div>
          </div>
        </div>

        {/* Page content */}
        <main className="p-6 min-h-screen">
          <motion.div
            initial={{ opacity: 0, y: 30 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.6, ease: "easeOut" }}
            className="animate-fade-in"
          >
            {children}
          </motion.div>
        </main>
      </div>
    </div>
  );
};

export default Layout;
