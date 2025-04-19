package com.bankapp.onlinebanking.service;

import com.bankapp.onlinebanking.entity.Account;
import com.bankapp.onlinebanking.entity.Transaction;
import com.bankapp.onlinebanking.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class FraudDetectionService {
    
    @Autowired
    private TransactionRepository transactionRepository;
    
    public boolean analyzeTransaction(Transaction transaction) {
        // Flag potentially fraudulent transactions
        if (isUnusualAmount(transaction)) {
            transaction.setIsFraudSuspected(true);
            transaction.setFraudReason("Unusual transaction amount");
            return true;
        }
        
        if (isUnusualLocation(transaction)) {
            transaction.setIsFraudSuspected(true);
            transaction.setFraudReason("Unusual transaction location");
            return true;
        }
        
        if (isRapidSuccessiveTransaction(transaction)) {
            transaction.setIsFraudSuspected(true);
            transaction.setFraudReason("Multiple rapid transactions");
            return true;
        }
        
        return false;
    }
    
    private boolean isUnusualAmount(Transaction transaction) {
        Account sender = transaction.getSender();
        Double avgAmount = transactionRepository.findAverageTransactionAmountForAccount(sender);
        
        // If this transaction is more than 3x the average, flag it
        return transaction.getAmount() > avgAmount * 3;
    }
    
    private boolean isUnusualLocation(Transaction transaction) {
        Account sender = transaction.getSender();
        String lastLocation = sender.getLastLoginIp();
        
        // Simple example - in a real app, use a geo-location service
        return lastLocation != null && !lastLocation.equals(transaction.getIpAddress());
    }
    
    private boolean isRapidSuccessiveTransaction(Transaction transaction) {
        Account sender = transaction.getSender();
        LocalDateTime fiveMinutesAgo = LocalDateTime.now().minusMinutes(5);
        
        List<Transaction> recentTransactions = transactionRepository
            .findBySenderAndTimestampAfter(sender, fiveMinutesAgo);
        
        // If there are more than 3 transactions in the last 5 minutes, flag it
        return recentTransactions.size() >= 3;
    }
} 