import { useEffect, useState } from 'react';
import { accountService, Account } from '../services/api';
import { ArrowPathIcon } from '@heroicons/react/24/outline';

const Accounts = () => {
  const [accounts, setAccounts] = useState<Account[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchAccounts = async () => {
      try {
        const response = await accountService.getAccounts();
        setAccounts(response.data || []);
      } finally {
        setLoading(false);
      }
    };
    fetchAccounts();
  }, []);

  if (loading) {
    return (
      <div className="flex justify-center items-center h-64">
        <ArrowPathIcon className="h-8 w-8 text-primary-600 animate-spin" />
      </div>
    );
  }

  return (
    <div className="max-w-7xl mx-auto">
      <h1 className="text-2xl font-semibold text-gray-900 mb-6">Your Accounts</h1>
      <div className="bg-white rounded-lg shadow-sm border border-gray-100 overflow-hidden">
        {accounts.map((account) => (
          <div key={account.id} className="p-6 border-b border-gray-100 last:border-0">
            <div className="flex justify-between items-start">
              <div>
                <h2 className="text-lg font-medium text-gray-900">{account.accountType || 'Account'}</h2>
                <p className="text-sm text-gray-500 mt-1">
                  {account.accountNumber ? `****${account.accountNumber.slice(-4)}` : 'No account number'}
                </p>
              </div>
              <div className="text-right">
                <p className="text-xl font-semibold text-gray-900">
                  ${(account.balance || 0).toLocaleString('en-US', { minimumFractionDigits: 2 })}
                </p>
                <p className="text-sm text-gray-500 mt-1">Available Balance</p>
              </div>
            </div>
            <div className="mt-4 flex space-x-3">
              <button className="text-sm font-medium text-primary-600 hover:text-primary-700">
                View Details
              </button>
              <button className="text-sm font-medium text-primary-600 hover:text-primary-700">
                Transfer Money
              </button>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default Accounts; 