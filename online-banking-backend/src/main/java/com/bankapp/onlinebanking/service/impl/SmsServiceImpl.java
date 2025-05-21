package com.bankapp.onlinebanking.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.bankapp.onlinebanking.service.SmsService;
import com.bankapp.onlinebanking.entity.Transaction;

@Service
public class SmsServiceImpl implements SmsService {
    private static final Logger logger = LoggerFactory.getLogger(SmsServiceImpl.class);

    @Override
    public void sendTransactionAlert(String phoneNumber, Transaction transaction) {
        String message = String.format(
                "Transaction Alert:\nAmount: $%.2f\nType: %s\nStatus: %s",
                transaction.getAmount(),
                transaction.getTransactionType(),
                transaction.getStatus());
        sendSms(phoneNumber, message);
    }

    @Override
    public void sendTwoFactorCode(String phoneNumber, String code) {
        String message = String.format("Your verification code is: %s", code);
        sendSms(phoneNumber, message);
    }

    @Override
    public void sendSms(String phoneNumber, String message) {
        // In production, you would integrate with a real SMS service like Twilio
        logger.info("Sending SMS to: {}, Message: {}", phoneNumber, message);
    }
}