package com.bankapp.onlinebanking.service;

import com.bankapp.onlinebanking.entity.Account;
import com.bankapp.onlinebanking.entity.Transaction;
import com.bankapp.onlinebanking.repository.AccountRepository;
import com.bankapp.onlinebanking.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private FraudDetectionService fraudDetectionService;

    @Autowired
    private NotificationService notificationService;

    @Transactional
    public Transaction createTransaction(Account sender, Account recipient, Double amount, String type,
            String description) {
        Transaction transaction = new Transaction();
        transaction.setSender(sender);
        transaction.setRecipient(recipient);
        transaction.setAmount(amount);
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setStatus("PENDING");
        transaction.setTransactionType(type);
        transaction.setDescription(description);
        transaction.setReferenceNumber(generateReferenceNumber());

        // Check for fraud
        boolean isFraudulent = fraudDetectionService.analyzeTransaction(transaction);

        if (isFraudulent) {
            transaction.setStatus("FLAGGED");
            // Send critical notification
            notificationService.createNotification(
                    sender,
                    "Suspicious transaction detected and blocked. Please contact support.",
                    "SECURITY",
                    "CRITICAL");
        } else {
            // Process transaction
            if ("TRANSFER".equals(type)) {
                if (sender.getBalance() < amount) {
                    transaction.setStatus("FAILED");
                    transaction.setDescription("Insufficient funds");

                    notificationService.createNotification(
                            sender,
                            "Transaction failed: Insufficient funds",
                            "TRANSACTION",
                            "WARNING");
                } else {
                    sender.setBalance(sender.getBalance() - amount);
                    recipient.setBalance(recipient.getBalance() + amount);
                    accountRepository.save(sender);
                    accountRepository.save(recipient);
                    transaction.setStatus("COMPLETED");

                    // Notify both parties
                    notificationService.createTransactionNotification(transaction);

                    if (recipient != null) {
                        // Create a different notification for recipient
                        notificationService.createNotification(
                                recipient,
                                String.format("You received $%.2f from %s",
                                        amount,
                                        sender.getAccountHolderName()),
                                "TRANSACTION",
                                "INFO");
                    }
                }
            }
            // Handle other transaction types...
        }

        return transactionRepository.save(transaction);
    }

    private String generateReferenceNumber() {
        return "TXN" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
