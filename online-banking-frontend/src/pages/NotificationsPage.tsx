import { useEffect, useState } from 'react';
import api from '../services/api';
import { Link } from 'react-router-dom';

interface Notification {
    id: number;
    message: string;
    notificationType: string;
    timestamp: string;
    isRead: boolean;
    severity: string;
}

const NotificationsPage = () => {
    const [notifications, setNotifications] = useState<Notification[]>([]);
    const [loading, setLoading] = useState(true);

    // In a real app, get this from auth context
    const userId = 1;

    useEffect(() => {
        setLoading(true);
        api.get<Notification[]>(`/notifications/${userId}`)
            .then(response => {
                setNotifications(response.data);
            })
            .catch(error => {
                console.error('Error fetching notifications:', error);
            })
            .finally(() => {
                setLoading(false);
            });
    }, [userId]);

    const handleMarkAsRead = (notificationId: number) => {
        api.put(`/notifications/${notificationId}/read`)
            .then(() => {
                setNotifications(prev =>
                    prev.map(n => n.id === notificationId ? { ...n, isRead: true } : n)
                );
            })
            .catch(error => {
                console.error('Error marking notification as read:', error);
            });
    };

    const getSeverityClass = (severity: string) => {
        switch (severity) {
            case 'CRITICAL':
                return 'bg-red-100 text-red-800 border-red-200';
            case 'WARNING':
                return 'bg-yellow-100 text-yellow-800 border-yellow-200';
            default:
                return 'bg-blue-100 text-blue-800 border-blue-200';
        }
    };

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
            <h1 className="text-2xl font-bold mb-6">Notifications</h1>

            <div className="bg-white rounded-lg shadow overflow-hidden">
                {notifications.length === 0 ? (
                    <div className="p-8 text-center text-gray-500">
                        <p>You have no notifications</p>
                    </div>
                ) : (
                    <div className="divide-y divide-gray-200">
                        {notifications.map(notification => (
                            <div
                                key={notification.id}
                                className={`p-4 transition-colors ${notification.isRead ? 'bg-white' : 'bg-blue-50'}`}
                                onClick={() => !notification.isRead && handleMarkAsRead(notification.id)}
                            >
                                <div className="flex items-start space-x-3">
                                    <div className={`flex-shrink-0 w-2 h-2 mt-2 rounded-full ${notification.isRead ? 'bg-gray-300' : 'bg-blue-600'}`}></div>
                                    <div className="flex-1">
                                        <p className="text-gray-900">{notification.message}</p>
                                        <div className="flex items-center mt-2">
                                            <span className={`px-2 py-1 text-xs rounded-full ${getSeverityClass(notification.severity)}`}>
                                                {notification.notificationType}
                                            </span>
                                            <span className="text-xs text-gray-500 ml-4">
                                                {new Date(notification.timestamp).toLocaleString()}
                                            </span>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        ))}
                    </div>
                )}
            </div>
            <div className="mt-6 text-center">
                <Link
                    to="/notifications/settings"
                    className="text-blue-600 hover:text-blue-800 font-medium"
                >
                    Manage Notification Settings
                </Link>
            </div>
        </div>
    );
};

export default NotificationsPage;