import { useEffect, useState } from 'react'
import { accountService, Account } from '../services/api'

// Define account type colors
const accountTypeStyles = {
  CHECKING: { bg: 'bg-green-100', text: 'text-green-800' },
  SAVINGS: { bg: 'bg-blue-100', text: 'text-blue-800' },
  CREDIT: { bg: 'bg-purple-100', text: 'text-purple-800' },
  DEFAULT: { bg: 'bg-gray-100', text: 'text-gray-800' }
}

function AccountsList() {
  const [accounts, setAccounts] = useState<Account[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    accountService.getAccounts()
      .then(response => {
        if (response.data) {
          setAccounts(response.data)
        }
      })
      .catch(err => {
        console.error('Error fetching accounts:', err)
        setError('Failed to load accounts')
      })
      .finally(() => setLoading(false))
  }, [])

  const getAccountTypeStyle = (type: string) => {
    const style = accountTypeStyles[type as keyof typeof accountTypeStyles] || accountTypeStyles.DEFAULT
    return `${style.bg} ${style.text}`
  }

  const formatAccountType = (type: string = '') => {
    return type.charAt(0).toUpperCase() + type.slice(1).toLowerCase()
  }

  if (loading) {
    return (
      <div className="flex justify-center items-center h-64">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
      </div>
    )
  }

  if (error) {
    return (
      <div className="container mx-auto px-8 py-6">
        <div className="text-red-600 text-center py-8">{error}</div>
      </div>
    )
  }

  return (
    <div className="container mx-auto px-8 py-6">
      <h1 className="text-3xl font-bold text-gray-800 mb-8">Your Accounts</h1>
      <div className="grid gap-6">
        {accounts.length === 0 ? (
          <p className="text-gray-600 text-center py-8">No accounts found</p>
        ) : (
          accounts.map(account => (
            <div key={account.id} className="bg-white rounded-xl shadow-lg overflow-hidden">
              <div className="p-6">
                <div className="flex justify-between items-start mb-4">
                  <div>
                    <span className={`inline-block px-3 py-1 rounded-full text-sm font-medium mb-2 ${getAccountTypeStyle(account.accountType)}`}>
                      {formatAccountType(account.accountType)}
                    </span>
                    <h2 className="text-xl font-semibold text-gray-900">
                      {formatAccountType(account.accountType)} Account
                      <span className="text-gray-500 text-base ml-2">
                        ••••{account.accountNumber?.slice(-4)}
                      </span>
                    </h2>
                  </div>
                  <div className="text-right">
                    <p className="text-2xl font-bold text-gray-900">
                      ${(account.balance || 0).toLocaleString('en-US', { minimumFractionDigits: 2 })}
                    </p>
                    <p className="text-sm text-gray-500">Available Balance</p>
                  </div>
                </div>
                <div className="flex gap-4 mt-6">
                  <button className="flex-1 bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700 transition-colors">
                    View Details
                  </button>
                  <button className="flex-1 bg-blue-50 text-blue-600 px-4 py-2 rounded-lg hover:bg-blue-100 transition-colors">
                    Transfer Money
                  </button>
                </div>
              </div>
            </div>
          ))
        )}
      </div>
    </div>
  )
}

export default AccountsList 