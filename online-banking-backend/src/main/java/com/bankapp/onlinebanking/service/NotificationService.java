package com.bankapp.onlinebanking.service;

import com.bankapp.onlinebanking.controller.NotificationController;
import com.bankapp.onlinebanking.entity.Account;
import com.bankapp.onlinebanking.entity.Notification;
import com.bankapp.onlinebanking.entity.NotificationPreference;
import com.bankapp.onlinebanking.entity.Transaction;
import com.bankapp.onlinebanking.repository.NotificationPreferenceRepository;
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

    @Autowired
    private NotificationPreferenceRepository preferenceRepository;

    public Notification createNotification(Account account, String message, String type, String severity) {
        // Create and save notification
        Notification notification = new Notification();
        notification.setAccount(account);
        notification.setMessage(message);
        notification.setNotificationType(type);
        notification.setSeverity(severity);
        notification = notificationRepository.save(notification);

        // Get user preferences
        NotificationPreference preference = preferenceRepository
                .findByAccount(account)
                .orElseGet(() -> {
                    // Create default preferences if none exist
                    NotificationPreference defaultPref = new NotificationPreference();
                    defaultPref.setAccount(account);
                    return preferenceRepository.save(defaultPref);
                });

        // Send via WebSocket if enabled
        if (preference.getEnableRealTimeNotifications()) {
            notificationController.sendNotificationToUser(account.getId(), notification);
        }

        // Send email if enabled and meets criteria
        if (preference.getEnableEmailNotifications()) {
            boolean shouldSendEmail = false;

            if ("SECURITY".equals(type) && preference.getEmailForSecurity()) {
                shouldSendEmail = true;
            } else if ("TRANSACTION".equals(type) && preference.getEmailForTransactions()) {
                // Check if transaction amount exceeds threshold (if we have that info)
                if (notification.getAdditionalData() != null && notification.getAdditionalData().contains("amount")) {
                    try {
                        double amount = Double.parseDouble(
                                notification.getAdditionalData().substring(
                                        notification.getAdditionalData().indexOf("amount") + 7,
                                        notification.getAdditionalData().indexOf("}",
                                                notification.getAdditionalData().indexOf("amount"))));
                        shouldSendEmail = amount >= preference.getEmailTransactionThreshold();
                    } catch (Exception e) {
                        shouldSendEmail = true; // If can't parse, just send to be safe
                    }
                } else {
                    shouldSendEmail = true; // No amount info, just send
                }
            } else if ("SYSTEM".equals(type) && preference.getEmailForSystem()) {
                shouldSendEmail = true;
            }

            if (shouldSendEmail && account.getEmail() != null) {
                emailService.sendEmail(account.getEmail(), "Bank Notification: " + type, message);
            }
        }

        // Similar logic for SMS
        if (preference.getEnableSmsNotifications() &&
                (("CRITICAL".equals(severity) && preference.getSmsForSecurity()) ||
                        ("TRANSACTION".equals(type) && preference.getSmsForTransactions()))) {

            if (account.getPhoneNumber() != null) {
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