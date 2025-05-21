package com.bankapp.onlinebanking.service;

import com.bankapp.onlinebanking.controller.NotificationController;
import com.bankapp.onlinebanking.entity.Account;
import com.bankapp.onlinebanking.entity.Notification;
import com.bankapp.onlinebanking.entity.Transaction;
import com.bankapp.onlinebanking.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    @Autowired
    private NotificationController notificationController;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private SmsService smsService;

    public Notification createNotification(Account account, String message, String type, String severity) {
        Notification notification = new Notification();
        notification.setAccount(account);
        notification.setMessage(message);
        notification.setNotificationType(type);
        notification.setSeverity(severity);

        // Save to database
        notification = notificationRepository.save(notification);

        // Send via WebSocket
        notificationController.sendNotificationToUser(account.getId(), notification);

        // For important notifications, also send via email/SMS
        if ("WARNING".equals(severity) || "CRITICAL".equals(severity)) {
            if (account.getEmail() != null) {
                emailService.sendEmail(account.getEmail(), "Important Bank Alert", message);
            }

            if (account.getPhoneNumber() != null && "CRITICAL".equals(severity)) {
                smsService.sendSms(account.getPhoneNumber(), message);
            }
        }

        return notification;
    }

    public Notification createTransactionNotification(Transaction transaction) {
        String message = String.format("%s of $%.2f %s",
                transaction.getTransactionType(),
                transaction.getAmount(),
                "COMPLETED".equals(transaction.getStatus()) ? "was completed"
                        : "is " + transaction.getStatus().toLowerCase());

        Notification notification = new Notification();
        notification.setAccount(transaction.getSender());
        notification.setMessage(message);
        notification.setNotificationType("TRANSACTION");
        notification.setSeverity("INFO");
        notification.setReferenceId(transaction.getId());
        notification.setReferenceType("TRANSACTION");

        // Save and send
        notification = notificationRepository.save(notification);
        notificationController.sendNotificationToUser(transaction.getSender().getId(), notification);

        return notification;
    }

    public void markAsRead(Long notificationId) {
        notificationRepository.findById(notificationId).ifPresent(notification -> {
            notification.setIsRead(true);
            notificationRepository.save(notification);
        });
    }
}