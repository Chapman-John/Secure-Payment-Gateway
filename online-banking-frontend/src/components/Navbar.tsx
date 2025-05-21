import { FC } from 'react';
import { Link, useLocation } from 'react-router-dom';
import NotificationBell from './NotificationBell';

const Navbar: FC = () => {
  const location = useLocation();
  const isActive = (path: string): boolean => location.pathname === path;

  // In a real app, get this from your auth context
  const userId = 1; // Replace with actual user ID from context

  return (
    <>
      {/* Spacer div */}
      <div className="h-20" />

      {/* Navbar */}
      <nav className="fixed top-0 left-0 right-0 w-full bg-gradient-to-r from-blue-800 to-blue-600 border-b-4 border-blue-900 shadow-lg z-50">
        <div className="max-w-full mx-auto px-8">
          <div className="flex justify-between items-center h-20">
            {/* Logo */}
            <div className="flex-shrink-0 mr-10">
              <span className="text-3xl font-bold text-white tracking-tight">ModernBank</span>
            </div>

            {/* Navigation Links */}
            <div className="flex space-x-4">
              <Link
                to="/"
                className={`px-6 py-3 rounded-md text-base font-medium transition-all duration-200 ${isActive('/')
                  ? 'bg-white text-blue-800'
                  : 'text-white hover:bg-blue-700'
                  }`}
              >
                Dashboard
              </Link>
              <Link
                to="/accounts"
                className={`px-6 py-3 rounded-md text-base font-medium transition-all duration-200 ${isActive('/accounts')
                  ? 'bg-white text-blue-800'
                  : 'text-white hover:bg-blue-700'
                  }`}
              >
                Accounts
              </Link>
              <Link
                to="/transactions"
                className={`px-6 py-3 rounded-md text-base font-medium transition-all duration-200 ${isActive('/transactions')
                  ? 'bg-white text-blue-800'
                  : 'text-white hover:bg-blue-700'
                  }`}
              >
                Transactions
              </Link>
            </div>


            <div className="flex items-center ml-4">
              <NotificationBell userId={userId} />

              {/* User profile dropdown */}
              <div className="relative ml-3">
                <button className="bg-blue-700 text-white p-2 rounded-full">
                  <span className="sr-only">Open user menu</span>
                  <span className="text-sm font-medium">JD</span>
                </button>
                {/* Dropdown menu would go here */}
              </div>
            </div>
          </div>
        </div>
      </nav>
    </>
  );
};

export default Navbar;