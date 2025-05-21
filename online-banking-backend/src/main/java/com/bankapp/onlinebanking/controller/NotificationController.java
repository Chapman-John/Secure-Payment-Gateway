package com.bankapp.onlinebanking.controller;

import com.bankapp.onlinebanking.entity.Notification;
import com.bankapp.onlinebanking.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class NotificationController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/notification")
    @SendTo("/topic/notifications")
    public Notification broadcastNotification(Notification notification) {
        return notification;
    }

    // Send notification to a specific user
    public void sendNotificationToUser(Long userId, Notification notification) {
        messagingTemplate.convertAndSend("/topic/notifications/" + userId, notification);
    }
}
