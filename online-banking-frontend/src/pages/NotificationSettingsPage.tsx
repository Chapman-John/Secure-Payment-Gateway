import { useEffect, useState } from 'react';
import api from '../services/api';

interface NotificationPreference {
    id: number;
    enableRealTimeNotifications: boolean;
    enableEmailNotifications: boolean;
    emailForTransactions: boolean;
    emailForSecurity: boolean;
    emailForSystem: boolean;
    emailTransactionThreshold: number;
    enableSmsNotifications: boolean;
    smsForTransactions: boolean;
    smsForSecurity: boolean;
    smsForSystem: boolean;
    smsTransactionThreshold: number;
}

const NotificationSettingsPage = () => {
    const [preferences, setPreferences] = useState<NotificationPreference | null>(null);
    const [loading, setLoading] = useState(true);
    const [saving, setSaving] = useState(false);
    const [message, setMessage] = useState({ text: '', type: '' });

    // In a real app, get the userId from auth context
    const userId = 1;

    useEffect(() => {
        api.get<NotificationPreference>(`/notifications/preferences/${userId}`)
            .then(response => {
                setPreferences(response.data);
            })
            .catch(error => {
                console.error('Error fetching notification preferences:', error);
            })
            .finally(() => {
                setLoading(false);
            });
    }, [userId]);

    const handleToggleChange = (field: keyof NotificationPreference) => {
        if (!preferences) return;

        setPreferences({
            ...preferences,
            [field]: !preferences[field]
        });
    };

    const handleNumberChange = (field: keyof NotificationPreference, value: string) => {
        if (!preferences) return;

        setPreferences({
            ...preferences,
            [field]: parseFloat(value)
        });
    };

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();
        if (!preferences) return;

        setSaving(true);
        api.put(`/notifications/preferences/${userId}`, preferences)
            .then(() => {
                setMessage({ text: 'Preferences saved successfully!', type: 'success' });
                setTimeout(() => setMessage({ text: '', type: '' }), 3000);
            })
            .catch(error => {
                console.error('Error saving preferences:', error);
                setMessage({ text: 'Failed to save preferences', type: 'error' });
            })
            .finally(() => {
                setSaving(false);
            });
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

    if (!preferences) {
        return (
            <div className="container mx-auto px-4 py-8">
                <div className="bg-red-100 text-red-800 p-4 rounded-lg">
                    Failed to load notification preferences
                </div>
            </div>
        );
    }

    return (
        <div className="container mx-auto px-4 py-8">
            <h1 className="text-2xl font-bold mb-6">Notification Settings</h1>

            {message.text && (
                <div className={`mb-6 p-4 rounded-lg ${message.type === 'success' ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'}`}>
                    {message.text}
                </div>
            )}

            <form onSubmit={handleSubmit} className="bg-white rounded-lg shadow overflow-hidden">
                <div className="p-6 border-b border-gray-200">
                    <h2 className="text-xl font-semibold mb-4">Real-time Notifications</h2>
                    <div className="flex items-center">
                        <input
                            type="checkbox"
                            id="enableRealTimeNotifications"
                            checked={preferences.enableRealTimeNotifications}
                            onChange={() => handleToggleChange('enableRealTimeNotifications')}
                            className="h-5 w-5 text-blue-600"
                        />
                        <label htmlFor="enableRealTimeNotifications" className="ml-2 text-gray-900">
                            Enable real-time notifications in the app
                        </label>
                    </div>
                </div>

                <div className="p-6 border-b border-gray-200">
                    <h2 className="text-xl font-semibold mb-4">Email Notifications</h2>
                    <div className="space-y-4">
                        <div className="flex items-center">
                            <input
                                type="checkbox"
                                id="enableEmailNotifications"
                                checked={preferences.enableEmailNotifications}
                                onChange={() => handleToggleChange('enableEmailNotifications')}
                                className="h-5 w-5 text-blue-600"
                            />
                            <label htmlFor="enableEmailNotifications" className="ml-2 text-gray-900">
                                Enable email notifications
                            </label>
                        </div>

                        <div className="ml-7 space-y-3">
                            <div className="flex items-center">
                                <input
                                    type="checkbox"
                                    id="emailForTransactions"
                                    checked={preferences.emailForTransactions}
                                    onChange={() => handleToggleChange('emailForTransactions')}
                                    disabled={!preferences.enableEmailNotifications}
                                    className="h-4 w-4 text-blue-600"
                                />
                                <label htmlFor="emailForTransactions" className="ml-2 text-gray-700">
                                    Transaction notifications
                                </label>
                            </div>

                            <div className="flex items-center">
                                <input
                                    type="checkbox"
                                    id="emailForSecurity"
                                    checked={preferences.emailForSecurity}
                                    onChange={() => handleToggleChange('emailForSecurity')}
                                    disabled={!preferences.enableEmailNotifications}
                                    className="h-4 w-4 text-blue-600"
                                />
                                <label htmlFor="emailForSecurity" className="ml-2 text-gray-700">
                                    Security alerts
                                </label>
                            </div>

                            <div className="flex items-center">
                                <input
                                    type="checkbox"
                                    id="emailForSystem"
                                    checked={preferences.emailForSystem}
                                    onChange={() => handleToggleChange('emailForSystem')}
                                    disabled={!preferences.enableEmailNotifications}
                                    className="h-4 w-4 text-blue-600"
                                />
                                <label htmlFor="emailForSystem" className="ml-2 text-gray-700">
                                    System notifications
                                </label>
                            </div>

                            <div className="flex items-center">
                                <label htmlFor="emailTransactionThreshold" className="mr-2 text-gray-700">
                                    Only email for transactions over $
                                </label>
                                <input
                                    type="number"
                                    id="emailTransactionThreshold"
                                    value={preferences.emailTransactionThreshold}
                                    onChange={(e) => handleNumberChange('emailTransactionThreshold', e.target.value)}
                                    disabled={!preferences.enableEmailNotifications || !preferences.emailForTransactions}
                                    className="w-24 border border-gray-300 rounded px-3 py-1"
                                    min="0"
                                    step="0.01"
                                />
                            </div>
                        </div>
                    </div>
                </div>

                <div className="p-6 border-b border-gray-200">
                    <h2 className="text-xl font-semibold mb-4">SMS Notifications</h2>
                    <div className="space-y-4">
                        <div className="flex items-center">
                            <input
                                type="checkbox"
                                id="enableSmsNotifications"
                                checked={preferences.enableSmsNotifications}
                                onChange={() => handleToggleChange('enableSmsNotifications')}
                                className="h-5 w-5 text-blue-600"
                            />
                            <label htmlFor="enableSmsNotifications" className="ml-2 text-gray-900">
                                Enable SMS notifications
                            </label>
                        </div>

                        <div className="ml-7 space-y-3">
                            <div className="flex items-center">
                                <input
                                    type="checkbox"
                                    id="smsForTransactions"
                                    checked={preferences.smsForTransactions}
                                    onChange={() => handleToggleChange('smsForTransactions')}
                                    disabled={!preferences.enableSmsNotifications}
                                    className="h-4 w-4 text-blue-600"
                                />
                                <label htmlFor="smsForTransactions" className="ml-2 text-gray-700">
                                    Transaction notifications
                                </label>
                            </div>

                            <div className="flex items-center">
                                <input
                                    type="checkbox"
                                    id="smsForSecurity"
                                    checked={preferences.smsForSecurity}
                                    onChange={() => handleToggleChange('smsForSecurity')}
                                    disabled={!preferences.enableSmsNotifications}
                                    className="h-4 w-4 text-blue-600"
                                />
                                <label htmlFor="smsForSecurity" className="ml-2 text-gray-700">
                                    Security alerts
                                </label>
                            </div>

                            <div className="flex items-center">
                                <input
                                    type="checkbox"
                                    id="smsForSystem"
                                    checked={preferences.smsForSystem}
                                    onChange={() => handleToggleChange('smsForSystem')}
                                    disabled={!preferences.enableSmsNotifications}
                                    className="h-4 w-4 text-blue-600"
                                />
                                <label htmlFor="smsForSystem" className="ml-2 text-gray-700">
                                    System notifications
                                </label>
                            </div>

                            <div className="flex items-center">
                                <label htmlFor="smsTransactionThreshold" className="mr-2 text-gray-700">
                                    Only SMS for transactions over $
                                </label>
                                <input
                                    type="number"
                                    id="smsTransactionThreshold"
                                    value={preferences.smsTransactionThreshold}
                                    onChange={(e) => handleNumberChange('smsTransactionThreshold', e.target.value)}
                                    disabled={!preferences.enableSmsNotifications || !preferences.smsForTransactions}
                                    className="w-24 border border-gray-300 rounded px-3 py-1"
                                    min="0"
                                    step="0.01"
                                />
                            </div>
                        </div>
                    </div>
                </div>

                <div className="p-6 bg-gray-50">
                    <button
                        type="submit"
                        className="w-full bg-blue-600 text-white py-2 px-4 rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 transition-colors"
                        disabled={saving}
                    >
                        {saving ? 'Saving...' : 'Save Preferences'}
                    </button>
                </div>
            </form>
        </div>
    );
};

export default NotificationSettingsPage;