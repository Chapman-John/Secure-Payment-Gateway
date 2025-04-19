import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { accountService, Account } from '../services/api';
import { ArrowUpIcon, ArrowDownIcon, CreditCardIcon, ChartBarIcon, BanknotesIcon, ArrowPathIcon } from '@heroicons/react/24/outline';

const Dashboard = () => {
  const [accounts, setAccounts] = useState<Account[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchAccounts = async () => {
      try {
        const response = await accountService.getAccounts();
        setAccounts(response.data || []);
      } catch (error) {
        setError('Failed to load accounts');
      } finally {
        setLoading(false);
      }
    };
    fetchAccounts();
  }, []);

  const totalBalance = accounts.reduce((sum, account) => sum + (account.balance || 0), 0);

  if (error) {
    return (
      <div className="flex justify-center items-center h-64">
        <p className="text-red-600">{error}</p>
      </div>
    );
  }

  if (loading) {
    return (
      <div className="flex justify-center items-center h-64">
        <ArrowPathIcon className="h-8 w-8 text-primary-600 animate-spin" />
      </div>
    );
  }

  return (
    <div className="max-w-7xl mx-auto">
      {/* Quick Stats */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-6">
        <div className="bg-white rounded-lg shadow-sm p-4 border border-gray-100">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gray-500">Total Balance</p>
              <p className="text-2xl font-semibold text-gray-900">
                ${totalBalance.toLocaleString('en-US', { minimumFractionDigits: 2 })}
              </p>
            </div>
            <div className="bg-primary-50 p-3 rounded-full">
              <BanknotesIcon className="h-6 w-6 text-primary-600" />
            </div>
          </div>
        </div>

        <div className="bg-white rounded-lg shadow-sm p-4 border border-gray-100">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gray-500">Active Accounts</p>
              <p className="text-2xl font-semibold text-gray-900">{accounts.length}</p>
            </div>
            <div className="bg-green-50 p-3 rounded-full">
              <CreditCardIcon className="h-6 w-6 text-green-600" />
            </div>
          </div>
        </div>

        <div className="bg-white rounded-lg shadow-sm p-4 border border-gray-100">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gray-500">Monthly Spending</p>
              <p className="text-2xl font-semibold text-gray-900">$2,450.85</p>
            </div>
            <div className="bg-purple-50 p-3 rounded-full">
              <ChartBarIcon className="h-6 w-6 text-purple-600" />
            </div>
          </div>
        </div>
      </div>

      {/* Quick Actions */}
      <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-6">
        <button className="flex flex-col items-center justify-center p-4 bg-white rounded-lg shadow-sm border border-gray-100 hover:border-primary-100 transition-colors">
          <div className="bg-primary-50 p-2 rounded-full mb-2">
            <ArrowUpIcon className="h-5 w-5 text-primary-600" />
          </div>
          <span className="text-sm font-medium text-gray-900">Send</span>
        </button>

        <button className="flex flex-col items-center justify-center p-4 bg-white rounded-lg shadow-sm border border-gray-100 hover:border-primary-100 transition-colors">
          <div className="bg-primary-50 p-2 rounded-full mb-2">
            <ArrowDownIcon className="h-5 w-5 text-primary-600" />
          </div>
          <span className="text-sm font-medium text-gray-900">Request</span>
        </button>

        <button className="flex flex-col items-center justify-center p-4 bg-white rounded-lg shadow-sm border border-gray-100 hover:border-primary-100 transition-colors">
          <div className="bg-primary-50 p-2 rounded-full mb-2">
            <CreditCardIcon className="h-5 w-5 text-primary-600" />
          </div>
          <span className="text-sm font-medium text-gray-900">Cards</span>
        </button>

        <button className="flex flex-col items-center justify-center p-4 bg-white rounded-lg shadow-sm border border-gray-100 hover:border-primary-100 transition-colors">
          <div className="bg-primary-50 p-2 rounded-full mb-2">
            <BanknotesIcon className="h-5 w-5 text-primary-600" />
          </div>
          <span className="text-sm font-medium text-gray-900">Payments</span>
        </button>
      </div>

      {/* Accounts List */}
      <div className="bg-white rounded-lg shadow-sm border border-gray-100 overflow-hidden">
        <div className="px-4 py-3 border-b border-gray-100">
          <div className="flex justify-between items-center">
            <h2 className="text-lg font-medium text-gray-900">Your Accounts</h2>
            <Link to="/accounts" className="text-sm font-medium text-primary-600 hover:text-primary-700">
              View All
            </Link>
          </div>
        </div>
        <div className="divide-y divide-gray-100">
          {accounts.map((account) => (
            <div key={account.id} className="p-4 hover:bg-gray-50 transition-colors">
              <div className="flex justify-between items-center">
                <div>
                  <p className="font-medium text-gray-900">{account.accountType || 'Account'}</p>
                  <p className="text-sm text-gray-500">
                    {account.accountNumber ? `****${account.accountNumber.slice(-4)}` : 'No account number'}
                  </p>
                </div>
                <div className="text-right">
                  <p className="font-medium text-gray-900">
                    ${(account.balance || 0).toLocaleString('en-US', { minimumFractionDigits: 2 })}
                  </p>
                  <p className="text-sm text-gray-500">Available</p>
                </div>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};

export default Dashboard; 