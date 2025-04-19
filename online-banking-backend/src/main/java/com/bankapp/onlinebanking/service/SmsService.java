package com.bankapp.onlinebanking.service;

import com.bankapp.onlinebanking.entity.Transaction;

public interface SmsService {
    void sendTransactionAlert(String phoneNumber, Transaction transaction);
    void sendTwoFactorCode(String phoneNumber, String code);
} 