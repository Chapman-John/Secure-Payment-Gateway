package com.bankapp.onlinebanking.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.bankapp.onlinebanking.service.EmailService;
import com.bankapp.onlinebanking.entity.Transaction;

@Service
public class EmailServiceImpl implements EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

    @Override
    public void sendEmail(String to, String subject, String content) {
        logger.info("Sending email to: {}, Subject: {}, Content: {}", to, subject, content);
    }

    @Override
    public void sendTransactionNotification(String to, String subject, Transaction transaction) {
        logger.debug("Transaction object: {}", transaction);
        logger.debug("Transaction fields - status: {}, type: {}", 
            transaction.getStatus(), 
            transaction.getTransactionType());
            
        String content = String.format(
            "Transaction Details:\nAmount: $%.2f\nType: %s\nStatus: %s",
            transaction.getAmount(),
            transaction.getTransactionType(),
            transaction.getStatus()
        );
        sendEmail(to, subject, content);
    }

    @Override
    public void sendTwoFactorCode(String to, String code) {
        String subject = "Your Two-Factor Authentication Code";
        String content = String.format("Your verification code is: %s", code);
        sendEmail(to, subject, content);
    }
} 