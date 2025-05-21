package com.bankapp.onlinebanking.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "notification_preferences")
@Data
public class NotificationPreference {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "account_id")
    private Account account;

    // WebSocket preferences
    private Boolean enableRealTimeNotifications = true;

    // Email preferences
    private Boolean enableEmailNotifications = true;
    private Boolean emailForTransactions = true;
    private Boolean emailForSecurity = true;
    private Boolean emailForSystem = false;
    private Double emailTransactionThreshold = 100.0; // Only email for transactions over this amount

    // SMS preferences
    private Boolean enableSmsNotifications = true;
    private Boolean smsForTransactions = true;
    private Boolean smsForSecurity = true;
    private Boolean smsForSystem = false;
    private Double smsTransactionThreshold = 500.0; // Only SMS for transactions over this amount
}