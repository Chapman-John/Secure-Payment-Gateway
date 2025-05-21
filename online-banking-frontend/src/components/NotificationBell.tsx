import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { BellIcon } from '@heroicons/react/24/outline';
import { useWebSocket, Notification } from '../services/websocket';
import api from '../services/api';

interface NotificationBellProps {
    userId: number | null;
}

const NotificationBell = ({ userId }: NotificationBellProps) => {
    const [unreadCount, setUnreadCount] = useState(0);
    const [showDropdown, setShowDropdown] = useState(false);
    const [localNotifications, setLocalNotifications] = useState<Notification[]>([]);
    const { connected, notifications } = useWebSocket(userId);

    useEffect(() => {
        if (userId) {
            // Fetch existing notifications
            api.get<Notification[]>(`/notifications/${userId}/unread`)
                .then(response => {
                    setLocalNotifications(response.data);
                    setUnreadCount(response.data.length);
                })
                .catch(error => console.error('Error fetching notifications:', error));
        }
    }, [userId]);

    useEffect(() => {
        if (notifications.length > 0) {
            setLocalNotifications(prev => [...notifications, ...prev]);
            setUnreadCount(prev => prev + notifications.length);
        }
    }, [notifications]);

    const handleMarkAsRead = (notificationId: number) => {
        api.put(`/notifications/${notificationId}/read`)
            .then(() => {
                setLocalNotifications(prev =>
                    prev.map(n => n.id === notificationId ? { ...n, isRead: true } : n)
                );
                setUnreadCount(prev => prev - 1);
            })
            .catch(error => console.error('Error marking notification as read:', error));
    };

    const toggleDropdown = () => {
        setShowDropdown(!showDropdown);
    };

    const getNotificationColor = (severity: string) => {
        switch (severity) {
            case 'CRITICAL': return 'bg-red-100 text-red-800';
            case 'WARNING': return 'bg-yellow-100 text-yellow-800';
            default: return 'bg-blue-100 text-blue-800';
        }
    };

    return (
        <div className="relative">
            <button
                className="relative p-1 rounded-full hover:bg-gray-100 focus:outline-none"
                onClick={toggleDropdown}
            >
                <BellIcon className="h-6 w-6 text-gray-600" />
                {unreadCount > 0 && (
                    <span className="absolute top-0 right-0 inline-flex items-center justify-center px-2 py-1 text-xs font-bold leading-none text-white transform translate-x-1/2 -translate-y-1/2 bg-red-600 rounded-full">
                        {unreadCount}
                    </span>
                )}
            </button>

            {showDropdown && (
                <div className="absolute right-0 mt-2 w-80 bg-white rounded-md shadow-lg z-50 overflow-hidden">
                    <div className="p-3 border-b border-gray-200">
                        <h3 className="text-lg font-medium text-gray-900">Notifications</h3>
                    </div>
                    <div className="max-h-96 overflow-y-auto">
                        {localNotifications.length === 0 ? (
                            <div className="p-4 text-center text-gray-500">
                                No new notifications
                            </div>
                        ) : (
                            localNotifications.map(notification => (
                                <div
                                    key={notification.id}
                                    className={`p-4 border-b border-gray-100 ${notification.isRead ? 'bg-white' : 'bg-blue-50'}`}
                                    onClick={() => handleMarkAsRead(notification.id)}
                                >
                                    <div className="flex items-start">
                                        <div className={`mt-1 mr-3 w-2 h-2 rounded-full ${notification.isRead ? 'bg-gray-300' : 'bg-blue-600'}`}></div>
                                        <div className="flex-1">
                                            <p className="text-sm font-medium text-gray-900">{notification.message}</p>
                                            <p className="text-xs text-gray-500">
                                                {new Date(notification.timestamp).toLocaleString()}
                                            </p>
                                            <span className={`mt-1 inline-block px-2 py-1 text-xs rounded-full ${getNotificationColor(notification.severity)}`}>
                                                {notification.notificationType}
                                            </span>
                                        </div>
                                    </div>
                                </div>
                            ))
                        )}
                    </div>
                    <div className="p-2 border-t border-gray-200 bg-gray-50">
                        <Link
                            to="/notifications"
                            className="block w-full text-center text-sm font-medium text-blue-600 hover:text-blue-800 py-1"
                        >
                            View All
                        </Link>
                    </div>
                </div>
            )}
        </div>
    );
};

export default NotificationBell;