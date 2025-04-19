package com.bankapp.onlinebanking.service;

import com.bankapp.onlinebanking.entity.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private SmsService smsService;
    
    public void notifyTransactionUpdate(Transaction transaction) {
        // Send real-time WebSocket notification
        String destination = "/topic/transactions/" + transaction.getSender().getId();
        messagingTemplate.convertAndSend(destination, transaction);
        
        // Send email notification if significant amount
        if (transaction.getAmount() > 1000) {
            emailService.sendTransactionNotification(
                transaction.getSender().getEmail(),
                "Large Transaction Alert",
                transaction
            );
        }
        
        // Send SMS for all transactions
        smsService.sendTransactionAlert(
            transaction.getSender().getPhoneNumber(),
            transaction
        );
    }
} 