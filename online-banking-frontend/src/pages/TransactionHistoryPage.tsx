import { useState, useEffect } from 'react';
import {
    MagnifyingGlassIcon,
    FunnelIcon,
    ArrowDownTrayIcon,
    ExclamationTriangleIcon,
    TagIcon,
    ChevronLeftIcon,
    ChevronRightIcon
} from '@heroicons/react/24/outline';
import api from '../services/api';

interface Transaction {
    id: number;
    amount: number;
    timestamp: string;
    description: string;
    transactionType: string;
    status: string;
    category?: string;
    merchantName?: string;
    referenceNumber: string;
    isDisputed: boolean;
    isRecurring: boolean;
    sender?: { accountHolderName: string };
    recipient?: { accountHolderName: string };
}

interface TransactionFilters {
    search: string;
    type: string;
    status: string;
    category: string;
    startDate: string;
    endDate: string;
    minAmount: string;
    maxAmount: string;
}

const TransactionHistoryPage = () => {
    const [transactions, setTransactions] = useState<Transaction[]>([]);
    const [loading, setLoading] = useState(true);
    const [currentPage, setCurrentPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const [totalElements, setTotalElements] = useState(0);
    const [showFilters, setShowFilters] = useState(false);
    const [selectedTransaction, setSelectedTransaction] = useState<Transaction | null>(null);
    const [showDisputeModal, setShowDisputeModal] = useState(false);
    const [disputeReason, setDisputeReason] = useState('');

    // In a real app, get this from auth context
    const accountId = 1;

    const [filters, setFilters] = useState<TransactionFilters>({
        search: '',
        type: '',
        status: '',
        category: '',
        startDate: '',
        endDate: '',
        minAmount: '',
        maxAmount: ''
    });

    const categories = [
        'GROCERIES', 'DINING', 'GAS', 'SHOPPING', 'BILLS', 'ENTERTAINMENT',
        'TRANSPORTATION', 'TRANSFER', 'ATM', 'OTHER'
    ];

    const transactionTypes = ['TRANSFER', 'DEPOSIT', 'WITHDRAWAL'];
    const statuses = ['PENDING', 'COMPLETED', 'FAILED', 'FLAGGED', 'DISPUTED'];

    useEffect(() => {
        fetchTransactions();
    }, [currentPage, filters]);

    const fetchTransactions = async () => {
        setLoading(true);
        try {
            const params = new URLSearchParams({
                page: currentPage.toString(),
                size: '10',
                sort: 'timestamp,desc'
            });

            // Add filters to params
            Object.entries(filters).forEach(([key, value]) => {
                if (value) {
                    params.append(key, value);
                }
            });

            const response = await api.get(`/transactions/account/${accountId}?${params}`);
            const data = response.data;

            setTransactions(data.content || []);
            setTotalPages(data.totalPages || 0);
            setTotalElements(data.totalElements || 0);
        } catch (error) {
            console.error('Error fetching transactions:', error);
        } finally {
            setLoading(false);
        }
    };

    const handleFilterChange = (key: keyof TransactionFilters, value: string) => {
        setFilters(prev => ({ ...prev, [key]: value }));
        setCurrentPage(0); // Reset to first page when filtering
    };

    const clearFilters = () => {
        setFilters({
            search: '',
            type: '',
            status: '',
            category: '',
            startDate: '',
            endDate: '',
            minAmount: '',
            maxAmount: ''
        });
        setCurrentPage(0);
    };

    const exportTransactions = async (format: 'csv' | 'json') => {
        try {
            const params = new URLSearchParams();
            if (filters.startDate) params.append('startDate', filters.startDate);
            if (filters.endDate) params.append('endDate', filters.endDate);
            params.append('format', format);

            const response = await api.get(`/transactions/account/${accountId}/export?${params}`, {
                responseType: 'blob'
            });

            const blob = new Blob([response.data]);
            const url = window.URL.createObjectURL(blob);
            const link = document.createElement('a');
            link.href = url;
            link.download = `transactions.${format}`;
            link.click();
            window.URL.revokeObjectURL(url);
        } catch (error) {
            console.error('Error exporting transactions:', error);
        }
    };

    const categorizeTransaction = async (transactionId: number, category: string) => {
        try {
            await api.post(`/transactions/${transactionId}/categorize`, { category });
            fetchTransactions(); // Refresh the list
        } catch (error) {
            console.error('Error categorizing transaction:', error);
        }
    };

    const openDisputeModal = (transaction: Transaction) => {
        setSelectedTransaction(transaction);
        setShowDisputeModal(true);
    };

    const submitDispute = async () => {
        if (!selectedTransaction || !disputeReason.trim()) return;

        try {
            await api.post(`/transactions/${selectedTransaction.id}/dispute`, {
                reason: disputeReason
            });
            setShowDisputeModal(false);
            setDisputeReason('');
            setSelectedTransaction(null);
            fetchTransactions(); // Refresh the list
        } catch (error) {
            console.error('Error disputing transaction:', error);
        }
    };

    const getStatusColor = (status: string) => {
        switch (status) {
            case 'COMPLETED': return 'bg-green-100 text-green-800';
            case 'PENDING': return 'bg-yellow-100 text-yellow-800';
            case 'FAILED': return 'bg-red-100 text-red-800';
            case 'FLAGGED': return 'bg-orange-100 text-orange-800';
            case 'DISPUTED': return 'bg-purple-100 text-purple-800';
            default: return 'bg-gray-100 text-gray-800';
        }
    };

    const getCategoryColor = (category: string) => {
        const colors = {
            'GROCERIES': 'bg-blue-100 text-blue-800',
            'DINING': 'bg-orange-100 text-orange-800',
            'GAS': 'bg-yellow-100 text-yellow-800',
            'SHOPPING': 'bg-purple-100 text-purple-800',
            'BILLS': 'bg-red-100 text-red-800',
            'ENTERTAINMENT': 'bg-pink-100 text-pink-800',
            'TRANSPORTATION': 'bg-indigo-100 text-indigo-800',
            'TRANSFER': 'bg-green-100 text-green-800',
            'ATM': 'bg-gray-100 text-gray-800',
            'OTHER': 'bg-gray-100 text-gray-800'
        };
        return colors[category as keyof typeof colors] || 'bg-gray-100 text-gray-800';
    };

    if (loading && transactions.length === 0) {
        return (
            <div className="container mx-auto px-4 py-8">
                <div className="flex justify-center">
                    <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
                </div>
            </div>
        );
    }

    return (
        <div className="container mx-auto px-4 py-8">
            <div className="flex justify-between items-center mb-6">
                <div>
                    <h1 className="text-2xl font-bold text-gray-900">Transaction History</h1>
                    <p className="text-gray-600">{totalElements} transactions total</p>
                </div>
                <div className="flex space-x-3">
                    <button
                        onClick={() => setShowFilters(!showFilters)}
                        className="flex items-center px-4 py-2 border border-gray-300 rounded-md hover:bg-gray-50"
                    >
                        <FunnelIcon className="h-4 w-4 mr-2" />
                        Filters
                    </button>
                    <div className="relative">
                        <button className="flex items-center px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700">
                            <ArrowDownTrayIcon className="h-4 w-4 mr-2" />
                            Export
                        </button>
                        <div className="absolute right-0 mt-1 w-32 bg-white border border-gray-200 rounded-md shadow-lg z-10 hidden group-hover:block">
                            <button
                                onClick={() => exportTransactions('csv')}
                                className="block w-full text-left px-4 py-2 hover:bg-gray-50"
                            >
                                Export CSV
                            </button>
                            <button
                                onClick={() => exportTransactions('json')}
                                className="block w-full text-left px-4 py-2 hover:bg-gray-50"
                            >
                                Export JSON
                            </button>
                        </div>
                    </div>
                </div>
            </div>

            {/* Filters Panel */}
            {showFilters && (
                <div className="bg-white p-6 rounded-lg shadow mb-6">
                    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-1">Search</label>
                            <div className="relative">
                                <MagnifyingGlassIcon className="h-4 w-4 absolute left-3 top-3 text-gray-400" />
                                <input
                                    type="text"
                                    placeholder="Search transactions..."
                                    className="pl-10 w-full border border-gray-300 rounded-md px-3 py-2"
                                    value={filters.search}
                                    onChange={(e) => handleFilterChange('search', e.target.value)}
                                />
                            </div>
                        </div>

                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-1">Type</label>
                            <select
                                className="w-full border border-gray-300 rounded-md px-3 py-2"
                                value={filters.type}
                                onChange={(e) => handleFilterChange('type', e.target.value)}
                            >
                                <option value="">All Types</option>
                                {transactionTypes.map(type => (
                                    <option key={type} value={type}>{type}</option>
                                ))}
                            </select>
                        </div>

                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-1">Status</label>
                            <select
                                className="w-full border border-gray-300 rounded-md px-3 py-2"
                                value={filters.status}
                                onChange={(e) => handleFilterChange('status', e.target.value)}
                            >
                                <option value="">All Statuses</option>
                                {statuses.map(status => (
                                    <option key={status} value={status}>{status}</option>
                                ))}
                            </select>
                        </div>

                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-1">Category</label>
                            <select
                                className="w-full border border-gray-300 rounded-md px-3 py-2"
                                value={filters.category}
                                onChange={(e) => handleFilterChange('category', e.target.value)}
                            >
                                <option value="">All Categories</option>
                                {categories.map(category => (
                                    <option key={category} value={category}>{category}</option>
                                ))}
                            </select>
                        </div>

                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-1">Start Date</label>
                            <input
                                type="date"
                                className="w-full border border-gray-300 rounded-md px-3 py-2"
                                value={filters.startDate}
                                onChange={(e) => handleFilterChange('startDate', e.target.value)}
                            />
                        </div>

                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-1">End Date</label>
                            <input
                                type="date"
                                className="w-full border border-gray-300 rounded-md px-3 py-2"
                                value={filters.endDate}
                                onChange={(e) => handleFilterChange('endDate', e.target.value)}
                            />
                        </div>

                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-1">Min Amount</label>
                            <input
                                type="number"
                                placeholder="0.00"
                                className="w-full border border-gray-300 rounded-md px-3 py-2"
                                value={filters.minAmount}
                                onChange={(e) => handleFilterChange('minAmount', e.target.value)}
                            />
                        </div>

                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-1">Max Amount</label>
                            <input
                                type="number"
                                placeholder="0.00"
                                className="w-full border border-gray-300 rounded-md px-3 py-2"
                                value={filters.maxAmount}
                                onChange={(e) => handleFilterChange('maxAmount', e.target.value)}
                            />
                        </div>
                    </div>

                    <div className="flex justify-end mt-4 space-x-3">
                        <button
                            onClick={clearFilters}
                            className="px-4 py-2 border border-gray-300 rounded-md hover:bg-gray-50"
                        >
                            Clear Filters
                        </button>
                    </div>
                </div>
            )}

            {/* Transaction List */}
            <div className="bg-white rounded-lg shadow overflow-hidden">
                {transactions.length === 0 ? (
                    <div className="p-8 text-center text-gray-500">
                        <p>No transactions found</p>
                    </div>
                ) : (
                    <>
                        <div className="overflow-x-auto">
                            <table className="min-w-full divide-y divide-gray-200">
                                <thead className="bg-gray-50">
                                    <tr>
                                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                            Date & Description
                                        </th>
                                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                            Category
                                        </th>
                                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                            Amount
                                        </th>
                                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                            Status
                                        </th>
                                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                            Actions
                                        </th>
                                    </tr>
                                </thead>
                                <tbody className="bg-white divide-y divide-gray-200">
                                    {transactions.map((transaction) => (
                                        <tr key={transaction.id} className="hover:bg-gray-50">
                                            <td className="px-6 py-4">
                                                <div>
                                                    <div className="flex items-center">
                                                        <div className="text-sm font-medium text-gray-900">
                                                            {transaction.description}
                                                        </div>
                                                        {transaction.isRecurring && (
                                                            <span className="ml-2 inline-flex items-center px-2 py-0.5 rounded text-xs font-medium bg-blue-100 text-blue-800">
                                                                Recurring
                                                            </span>
                                                        )}
                                                        {transaction.isDisputed && (
                                                            <ExclamationTriangleIcon className="ml-2 h-4 w-4 text-red-500" />
                                                        )}
                                                    </div>
                                                    <div className="text-sm text-gray-500">
                                                        {new Date(transaction.timestamp).toLocaleDateString()} at{' '}
                                                        {new Date(transaction.timestamp).toLocaleTimeString()}
                                                    </div>
                                                    <div className="text-xs text-gray-400">
                                                        {transaction.referenceNumber}
                                                    </div>
                                                </div>
                                            </td>
                                            <td className="px-6 py-4">
                                                <div className="flex items-center space-x-2">
                                                    <span className={`inline-flex items-center px-2 py-1 rounded-full text-xs font-medium ${getCategoryColor(transaction.category || 'OTHER')}`}>
                                                        {transaction.category || 'OTHER'}
                                                    </span>
                                                    <select
                                                        className="text-xs border border-gray-300 rounded px-2 py-1"
                                                        value={transaction.category || ''}
                                                        onChange={(e) => categorizeTransaction(transaction.id, e.target.value)}
                                                    >
                                                        <option value="">Select...</option>
                                                        {categories.map(category => (
                                                            <option key={category} value={category}>{category}</option>
                                                        ))}
                                                    </select>
                                                </div>
                                            </td>
                                            <td className="px-6 py-4">
                                                <div className={`text-sm font-medium ${transaction.amount < 0 ? 'text-red-600' : 'text-green-600'}`}>
                                                    ${Math.abs(transaction.amount).toFixed(2)}
                                                </div>
                                                <div className="text-xs text-gray-500">
                                                    {transaction.transactionType}
                                                </div>
                                            </td>
                                            <td className="px-6 py-4">
                                                <span className={`inline-flex items-center px-2 py-1 rounded-full text-xs font-medium ${getStatusColor(transaction.status)}`}>
                                                    {transaction.status}
                                                </span>
                                            </td>
                                            <td className="px-6 py-4 text-sm font-medium space-x-2">
                                                {!transaction.isDisputed && transaction.status === 'COMPLETED' && (
                                                    <button
                                                        onClick={() => openDisputeModal(transaction)}
                                                        className="text-red-600 hover:text-red-900"
                                                    >
                                                        Dispute
                                                    </button>
                                                )}
                                                <button className="text-blue-600 hover:text-blue-900">
                                                    Details
                                                </button>
                                            </td>
                                        </tr>
                                    ))}
                                </tbody>
                            </table>
                        </div>

                        {/* Pagination */}
                        <div className="bg-white px-4 py-3 flex items-center justify-between border-t border-gray-200 sm:px-6">
                            <div className="flex-1 flex justify-between items-center">
                                <div>
                                    <p className="text-sm text-gray-700">
                                        Showing page {currentPage + 1} of {totalPages}
                                    </p>
                                </div>
                                <div className="flex space-x-2">
                                    <button
                                        onClick={() => setCurrentPage(Math.max(0, currentPage - 1))}
                                        disabled={currentPage === 0}
                                        className="relative inline-flex items-center px-3 py-2 border border-gray-300 text-sm font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50 disabled:opacity-50"
                                    >
                                        <ChevronLeftIcon className="h-4 w-4" />
                                        Previous
                                    </button>
                                    <button
                                        onClick={() => setCurrentPage(Math.min(totalPages - 1, currentPage + 1))}
                                        disabled={currentPage >= totalPages - 1}
                                        className="relative inline-flex items-center px-3 py-2 border border-gray-300 text-sm font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50 disabled:opacity-50"
                                    >
                                        Next
                                        <ChevronRightIcon className="h-4 w-4" />
                                    </button>
                                </div>
                            </div>
                        </div>
                    </>
                )}
            </div>

            {/* Dispute Modal */}
            {showDisputeModal && (
                <div className="fixed inset-0 bg-gray-600 bg-opacity-50 overflow-y-auto h-full w-full z-50">
                    <div className="relative top-20 mx-auto p-5 border w-96 shadow-lg rounded-md bg-white">
                        <div className="mt-3">
                            <h3 className="text-lg font-medium text-gray-900 mb-4">
                                Dispute Transaction
                            </h3>
                            <p className="text-sm text-gray-600 mb-4">
                                Transaction: {selectedTransaction?.description}
                            </p>
                            <p className="text-sm text-gray-600 mb-4">
                                Amount: ${selectedTransaction?.amount.toFixed(2)}
                            </p>
                            <div className="mb-4">
                                <label className="block text-sm font-medium text-gray-700 mb-2">
                                    Reason for dispute:
                                </label>
                                <textarea
                                    className="w-full border border-gray-300 rounded-md px-3 py-2"
                                    rows={4}
                                    value={disputeReason}
                                    onChange={(e) => setDisputeReason(e.target.value)}
                                    placeholder="Please explain why you're disputing this transaction..."
                                />
                            </div>
                            <div className="flex justify-end space-x-3">
                                <button
                                    onClick={() => {
                                        setShowDisputeModal(false);
                                        setDisputeReason('');
                                        setSelectedTransaction(null);
                                    }}
                                    className="px-4 py-2 border border-gray-300 rounded-md hover:bg-gray-50"
                                >
                                    Cancel
                                </button>
                                <button
                                    onClick={submitDispute}
                                    disabled={!disputeReason.trim()}
                                    className="px-4 py-2 bg-red-600 text-white rounded-md hover:bg-red-700 disabled:opacity-50"
                                >
                                    Submit Dispute
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
};

export default TransactionHistoryPage;