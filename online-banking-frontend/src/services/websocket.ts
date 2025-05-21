import { useEffect, useState } from 'react';
import SockJS from 'sockjs-client';
import { Client, StompSubscription } from '@stomp/stompjs';

export interface Notification {
    id: number;
    message: string;
    notificationType: string;
    timestamp: string;
    isRead: boolean;
    severity: 'INFO' | 'WARNING' | 'CRITICAL';
    referenceId?: number;
    referenceType?: string;
}

export const useWebSocket = (userId: number | null) => {
    const [client, setClient] = useState<Client | null>(null);
    const [notifications, setNotifications] = useState<Notification[]>([]);
    const [connected, setConnected] = useState(false);
    const [subscription, setSubscription] = useState<StompSubscription | null>(null);

    useEffect(() => {
        // Only connect if we have a userId
        if (!userId) return;

        // Clean up previous client if it exists
        if (client) {
            if (subscription) {
                subscription.unsubscribe();
            }
            client.deactivate();
        }

        const newClient = new Client({
            webSocketFactory: () => new SockJS('/ws'),
            debug: (str) => {
                console.log('STOMP: ' + str);
            },
            reconnectDelay: 5000, // Attempt to reconnect after 5 seconds
            heartbeatIncoming: 4000,
            heartbeatOutgoing: 4000,
            onConnect: () => {
                setConnected(true);
                console.log('Connected to WebSocket');

                try {
                    // Subscribe to user-specific notifications
                    const sub = newClient.subscribe(`/topic/notifications/${userId}`, (message) => {
                        try {
                            const notification: Notification = JSON.parse(message.body);
                            setNotifications((prev) => [notification, ...prev]);
                        } catch (error) {
                            console.error('Error parsing notification message:', error);
                        }
                    });

                    setSubscription(sub);
                } catch (error) {
                    console.error('Error subscribing to notifications:', error);
                }
            },
            onDisconnect: () => {
                setConnected(false);
                console.log('Disconnected from WebSocket');
            },
            onStompError: (frame) => {
                console.error('STOMP error', frame);
            }
        });

        try {
            newClient.activate();
            setClient(newClient);
        } catch (error) {
            console.error('Error activating WebSocket client:', error);
        }

        return () => {
            if (subscription) {
                try {
                    subscription.unsubscribe();
                } catch (error) {
                    console.error('Error unsubscribing:', error);
                }
            }
            if (newClient) {
                try {
                    newClient.deactivate();
                } catch (error) {
                    console.error('Error deactivating client:', error);
                }
            }
        };
    }, [userId]);

    const sendMessage = (destination: string, body: any) => {
        if (client && connected) {
            try {
                client.publish({
                    destination,
                    body: JSON.stringify(body)
                });
                return true;
            } catch (error) {
                console.error('Error sending message:', error);
                return false;
            }
        }
        return false;
    };

    return { connected, notifications, sendMessage };
};