import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Navbar from './components/Navbar';
import AccountsList from './components/AccountsList';
import NotificationsPage from './pages/NotificationsPage';
import NotificationSettingsPage from './pages/NotificationSettingsPage';

// Create simple components for Dashboard and Transactions
const Dashboard = () => (
  <div className="container mx-auto px-8 py-6">
    <h1 className="text-3xl font-bold text-gray-800 mb-8">Welcome Back</h1>
    <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
      <div className="bg-white rounded-xl shadow-lg p-8">
        <h2 className="text-2xl font-semibold text-gray-700 mb-6">Quick Overview</h2>
        <div className="space-y-6">
          <div className="flex justify-between items-center">
            <span className="text-gray-600 text-lg">Total Balance</span>
            <span className="text-3xl font-bold text-blue-600">$12,345.67</span>
          </div>
          <div className="flex justify-between items-center">
            <span className="text-gray-600 text-lg">Recent Activity</span>
            <span className="text-blue-600 hover:text-blue-700 cursor-pointer">View All →</span>
          </div>
        </div>
      </div>
      <div className="bg-white rounded-xl shadow-lg p-8">
        <h2 className="text-2xl font-semibold text-gray-700 mb-6">Quick Actions</h2>
        <div className="grid grid-cols-2 gap-6">
          <button className="p-6 bg-blue-50 rounded-xl text-blue-700 hover:bg-blue-100 transition-colors font-medium">
            Transfer Money
          </button>
          <button className="p-6 bg-blue-50 rounded-xl text-blue-700 hover:bg-blue-100 transition-colors font-medium">
            Pay Bills
          </button>
          <button className="p-6 bg-blue-50 rounded-xl text-blue-700 hover:bg-blue-100 transition-colors font-medium">
            Mobile Deposit
          </button>
          <button className="p-6 bg-blue-50 rounded-xl text-blue-700 hover:bg-blue-100 transition-colors font-medium">
            Send Money
          </button>
        </div>
      </div>
    </div>
  </div>
);

const Transactions = () => (
  <div className="container mx-auto px-8 py-6">
    <h1 className="text-3xl font-bold text-gray-800 mb-8">Recent Transactions</h1>
    <div className="bg-white rounded-xl shadow-lg overflow-hidden">
      <div className="p-6 border-b border-gray-200 hover:bg-gray-50 transition-colors">
        <div className="flex justify-between items-center">
          <div>
            <p className="font-semibold text-gray-800 text-lg">Grocery Store</p>
            <p className="text-gray-600">Feb 15, 2024</p>
          </div>
          <span className="text-red-600 font-semibold text-lg">-$82.45</span>
        </div>
      </div>
      <div className="p-6 border-b border-gray-200 hover:bg-gray-50 transition-colors">
        <div className="flex justify-between items-center">
          <div>
            <p className="font-semibold text-gray-800 text-lg">Direct Deposit</p>
            <p className="text-gray-600">Feb 14, 2024</p>
          </div>
          <span className="text-green-600 font-semibold text-lg">+$2,450.00</span>
        </div>
      </div>
    </div>
  </div>
);

function App() {
  return (
    <div className="min-h-screen bg-gray-50">
      <Router>
        <Navbar />
        <main className="pt-20">
          <Routes>
            <Route path="/" element={<Dashboard />} />
            <Route path="/accounts" element={<AccountsList />} />
            <Route path="/transactions" element={<Transactions />} />
            <Route path="/notifications" element={<NotificationsPage />} />
            <Route path="/notifications/settings" element={<NotificationSettingsPage />} />
          </Routes>
        </main>
      </Router>
    </div>
  );
}

export default App;
