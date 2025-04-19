package com.bankapp.onlinebanking.service;

import com.bankapp.onlinebanking.entity.Transaction;

public interface EmailService {
    void sendEmail(String to, String subject, String content);
    void sendTransactionNotification(String to, String subject, Transaction transaction);
    void sendTwoFactorCode(String to, String code);
} 