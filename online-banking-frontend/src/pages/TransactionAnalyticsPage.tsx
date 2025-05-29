import { useState, useEffect } from 'react';
import {
    ChartBarIcon,
    CurrencyDollarIcon,
    CalendarIcon,
    ArrowTrendingUpIcon,
    ArrowTrendingDownIcon
} from '@heroicons/react/24/outline';
import api from '../services/api';

interface CategoryData {
    category: string;
    count: number;
    totalAmount: number;
}

interface MonthlyData {
    year: number;
    month: number;
    amount: number;
}

interface Analytics {
    totalTransactions: number;
    totalSpent: number;
    averageTransaction: number;
    monthlySpending: MonthlyData[];
    categoryBreakdown: CategoryData[];
}

interface RecurringTransaction {
    description: string;
    amount: number;
    merchantName: string;
    frequency: number;
    firstTransaction: string;
    lastTransaction: string;
}

const TransactionAnalyticsPage = () => {
    const [analytics, setAnalytics] = useState<Analytics | null>(null);
    const [recurringTransactions, setRecurringTransactions] = useState<RecurringTransaction[]>([]);
    const [loading, setLoading] = useState(true);
    const [dateRange, setDateRange] = useState({
        startDate: new Date(new Date().setMonth(new Date().getMonth() - 6)).toISOString().split('T')[0],
        endDate: new Date().toISOString().split('T')[0]
    });

    // In a real app, get this from auth context
    const accountId = 1;

    useEffect(() => {
        fetchAnalytics();
        fetchRecurringTransactions();
    }, [dateRange]);

    const fetchAnalytics = async () => {
        try {
            const params = new URLSearchParams({
                startDate: new Date(dateRange.startDate).toISOString(),
                endDate: new Date(dateRange.endDate).toISOString()
            });

            const response = await api.get(`/transactions/account/${accountId}/analytics?${params}`);
            setAnalytics(response.data);
        } catch (error) {
            console.error('Error fetching analytics:', error);
        }
    };

    const fetchRecurringTransactions = async () => {
        try {
            const response = await api.get(`/transactions/account/${accountId}/recurring`);
            setRecurringTransactions(response.data);
        } catch (error) {
            console.error('Error fetching recurring transactions:', error);
        } finally {
            setLoading(false);
        }
    };

    const formatCurrency = (amount: number) => {
        return new Intl.NumberFormat('en-US', {
            style: 'currency',
            currency: 'USD'
        }).format(amount);
    };

    const getMonthName = (monthNum: number) => {
        const months = [
            'Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun',
            'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'
        ];
        return months[monthNum - 1];
    };

    const getCategoryColor = (index: number) => {
        const colors = [
            'bg-blue-500', 'bg-green-500', 'bg-yellow-500', 'bg-red-500',
            'bg-purple-500', 'bg-pink-500', 'bg-indigo-500', 'bg-gray-500'
        ];
        return colors[index % colors.length];
    };

    const calculateTrend = () => {
        if (!analytics || analytics.monthlySpending.length < 2) return null;

        const sorted = [...analytics.monthlySpending].sort((a, b) =>
            a.year - b.year || a.month - b.month
        );

        const recent = sorted.slice(-2);
        if (recent.length < 2) return null;

        const change = recent[1].amount - recent[0].amount;
        const percentChange = (change / recent[0].amount) * 100;

        return {
            change,
            percentChange,
            isIncrease: change > 0
        };
    };

    const trend = calculateTrend();

    if (loading) {
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
                <h1 className="text-2xl font-bold text-gray-900">Transaction Analytics</h1>
                <div className="flex items-center space-x-4">
                    <div className="flex items-center space-x-2">
                        <label className="text-sm font-medium text-gray-700">From:</label>
                        <input
                            type="date"
                            value={dateRange.startDate}
                            onChange={(e) => setDateRange(prev => ({ ...prev, startDate: e.target.value }))}
                            className="border border-gray-300 rounded-md px-3 py-1 text-sm"
                        />
                    </div>
                    <div className="flex items-center space-x-2">
                        <label className="text-sm font-medium text-gray-700">To:</label>
                        <input
                            type="date"
                            value={dateRange.endDate}
                            onChange={(e) => setDateRange(prev => ({ ...prev, endDate: e.target.value }))}
                            className="border border-gray-300 rounded-md px-3 py-1 text-sm"
                        />
                    </div>
                </div>
            </div>

            {/* Summary Cards */}
            <div className="grid grid-cols-1 md:grid-cols-4 gap-6 mb-8">
                <div className="bg-white rounded-lg shadow p-6">
                    <div className="flex items-center">
                        <div className="flex-shrink-0">
                            <ChartBarIcon className="h-8 w-8 text-blue-600" />
                        </div>
                        <div className="ml-4">
                            <p className="text-sm font-medium text-gray-500">Total Transactions</p>
                            <p className="text-2xl font-semibold text-gray-900">
                                {analytics?.totalTransactions || 0}
                            </p>
                        </div>
                    </div>
                </div>

                <div className="bg-white rounded-lg shadow p-6">
                    <div className="flex items-center">
                        <div className="flex-shrink-0">
                            <CurrencyDollarIcon className="h-8 w-8 text-red-600" />
                        </div>
                        <div className="ml-4">
                            <p className="text-sm font-medium text-gray-500">Total Spent</p>
                            <p className="text-2xl font-semibold text-gray-900">
                                {formatCurrency(analytics?.totalSpent || 0)}
                            </p>
                        </div>
                    </div>
                </div>

                <div className="bg-white rounded-lg shadow p-6">
                    <div className="flex items-center">
                        <div className="flex-shrink-0">
                            <CalendarIcon className="h-8 w-8 text-green-600" />
                        </div>
                        <div className="ml-4">
                            <p className="text-sm font-medium text-gray-500">Average Transaction</p>
                            <p className="text-2xl font-semibold text-gray-900">
                                {formatCurrency(analytics?.averageTransaction || 0)}
                            </p>
                        </div>
                    </div>
                </div>

                <div className="bg-white rounded-lg shadow p-6">
                    <div className="flex items-center">
                        <div className="flex-shrink-0">
                            {trend?.isIncrease ? (
                                <ArrowTrendingUpIcon className="h-8 w-8 text-red-600" />
                            ) : (
                                <ArrowTrendingDownIcon className="h-8 w-8 text-green-600" />
                            )}
                        </div>
                        <div className="ml-4">
                            <p className="text-sm font-medium text-gray-500">Monthly Trend</p>
                            <p className={`text-2xl font-semibold ${trend?.isIncrease ? 'text-red-600' : 'text-green-600'}`}>
                                {trend ? `${trend.isIncrease ? '+' : ''}${trend.percentChange.toFixed(1)}%` : 'N/A'}
                            </p>
                        </div>
                    </div>
                </div>
            </div>

            <div className="grid grid-cols-1 lg:grid-cols-2 gap-8 mb-8">
                {/* Monthly Spending Chart */}
                <div className="bg-white rounded-lg shadow p-6">
                    <h2 className="text-lg font-semibold text-gray-900 mb-4">Monthly Spending</h2>
                    <div className="space-y-4">
                        {analytics?.monthlySpending?.map((month, index) => {
                            const maxAmount = Math.max(...(analytics.monthlySpending?.map(m => m.amount) || [1]));
                            const percentage = (month.amount / maxAmount) * 100;

                            return (
                                <div key={`${month.year}-${month.month}`} className="flex items-center">
                                    <div className="w-16 text-sm text-gray-600">
                                        {getMonthName(month.month)} {month.year}
                                    </div>
                                    <div className="flex-1 mx-4">
                                        <div className="bg-gray-200 rounded-full h-6 relative">
                                            <div
                                                className="bg-blue-600 h-6 rounded-full transition-all duration-300"
                                                style={{ width: `${percentage}%` }}
                                            />
                                            <div className="absolute inset-0 flex items-center justify-center text-xs font-medium text-white">
                                                {formatCurrency(month.amount)}
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            );
                        })}
                    </div>
                </div>

                {/* Category Breakdown */}
                <div className="bg-white rounded-lg shadow p-6">
                    <h2 className="text-lg font-semibold text-gray-900 mb-4">Spending by Category</h2>
                    <div className="space-y-3">
                        {analytics?.categoryBreakdown?.map((category, index) => {
                            const maxAmount = Math.max(...(analytics.categoryBreakdown?.map(c => c.totalAmount) || [1]));
                            const percentage = (category.totalAmount / maxAmount) * 100;

                            return (
                                <div key={category.category} className="flex items-center">
                                    <div className="w-20 text-sm text-gray-600">
                                        {category.category}
                                    </div>
                                    <div className="flex-1 mx-4">
                                        <div className="bg-gray-200 rounded-full h-4 relative">
                                            <div
                                                className={`h-4 rounded-full transition-all duration-300 ${getCategoryColor(index)}`}
                                                style={{ width: `${percentage}%` }}
                                            />
                                        </div>
                                    </div>
                                    <div className="w-24 text-sm text-gray-900 text-right">
                                        {formatCurrency(category.totalAmount)}
                                    </div>
                                    <div className="w-12 text-xs text-gray-500 text-right">
                                        ({category.count})
                                    </div>
                                </div>
                            );
                        })}
                    </div>
                </div>
            </div>

            {/* Recurring Transactions */}
            <div className="bg-white rounded-lg shadow">
                <div className="px-6 py-4 border-b border-gray-200">
                    <h2 className="text-lg font-semibold text-gray-900">Recurring Transactions</h2>
                    <p className="text-sm text-gray-600">Transactions that appear to repeat regularly</p>
                </div>
                <div className="overflow-x-auto">
                    {recurringTransactions.length === 0 ? (
                        <div className="p-8 text-center text-gray-500">
                            <p>No recurring transactions detected</p>
                        </div>
                    ) : (
                        <table className="min-w-full divide-y divide-gray-200">
                            <thead className="bg-gray-50">
                                <tr>
                                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                        Description
                                    </th>
                                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                        Amount
                                    </th>
                                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                        Frequency
                                    </th>
                                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                        Date Range
                                    </th>
                                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                        Actions
                                    </th>
                                </tr>
                            </thead>
                            <tbody className="bg-white divide-y divide-gray-200">
                                {recurringTransactions.map((transaction, index) => (
                                    <tr key={index} className="hover:bg-gray-50">
                                        <td className="px-6 py-4">
                                            <div className="text-sm font-medium text-gray-900">
                                                {transaction.description}
                                            </div>
                                            {transaction.merchantName && (
                                                <div className="text-sm text-gray-500">
                                                    {transaction.merchantName}
                                                </div>
                                            )}
                                        </td>
                                        <td className="px-6 py-4 text-sm text-gray-900">
                                            {formatCurrency(transaction.amount)}
                                        </td>
                                        <td className="px-6 py-4">
                                            <span className="inline-flex items-center px-2 py-1 rounded-full text-xs font-medium bg-blue-100 text-blue-800">
                                                {transaction.frequency}x
                                            </span>
                                        </td>
                                        <td className="px-6 py-4 text-sm text-gray-500">
                                            {new Date(transaction.firstTransaction).toLocaleDateString()} - {' '}
                                            {new Date(transaction.lastTransaction).toLocaleDateString()}
                                        </td>
                                        <td className="px-6 py-4 text-sm font-medium">
                                            <button className="text-blue-600 hover:text-blue-900 mr-3">
                                                Set Alert
                                            </button>
                                            <button className="text-green-600 hover:text-green-900">
                                                Schedule
                                            </button>
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    )}
                </div>
            </div>

            {/* Insights Section */}
            <div className="mt-8 bg-blue-50 rounded-lg p-6">
                <h3 className="text-lg font-semibold text-blue-900 mb-4">💡 Insights & Recommendations</h3>
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                    {analytics && analytics.categoryBreakdown.length > 0 && (
                        <div className="bg-white rounded-md p-4">
                            <h4 className="font-medium text-gray-900 mb-2">Top Spending Category</h4>
                            <p className="text-sm text-gray-600">
                                You spend the most on <strong>{analytics.categoryBreakdown[0]?.category}</strong> -
                                {formatCurrency(analytics.categoryBreakdown[0]?.totalAmount)} this period.
                            </p>
                        </div>
                    )}

                    {trend && (
                        <div className="bg-white rounded-md p-4">
                            <h4 className="font-medium text-gray-900 mb-2">Spending Trend</h4>
                            <p className="text-sm text-gray-600">
                                Your spending has {trend.isIncrease ? 'increased' : 'decreased'} by{' '}
                                <strong>{Math.abs(trend.percentChange).toFixed(1)}%</strong> compared to last month.
                            </p>
                        </div>
                    )}

                    {recurringTransactions.length > 0 && (
                        <div className="bg-white rounded-md p-4">
                            <h4 className="font-medium text-gray-900 mb-2">Recurring Transactions</h4>
                            <p className="text-sm text-gray-600">
                                We detected <strong>{recurringTransactions.length}</strong> recurring transactions.
                                Consider setting up automatic categorization or alerts.
                            </p>
                        </div>
                    )}

                    {analytics && analytics.averageTransaction > 100 && (
                        <div className="bg-white rounded-md p-4">
                            <h4 className="font-medium text-gray-900 mb-2">Transaction Size</h4>
                            <p className="text-sm text-gray-600">
                                Your average transaction is {formatCurrency(analytics.averageTransaction)}.
                                Consider reviewing larger transactions for potential savings.
                            </p>
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
};

export default TransactionAnalyticsPage;