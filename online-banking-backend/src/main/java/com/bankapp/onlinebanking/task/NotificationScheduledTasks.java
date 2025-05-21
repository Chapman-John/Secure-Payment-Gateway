package com.bankapp.onlinebanking.task;

import com.bankapp.onlinebanking.entity.Account;
import com.bankapp.onlinebanking.entity.Transaction;
import com.bankapp.onlinebanking.repository.AccountRepository;
import com.bankapp.onlinebanking.repository.TransactionRepository;
import com.bankapp.onlinebanking.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class NotificationScheduledTasks {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    // Run daily at midnight
    @Scheduled(cron = "0 0 0 * * ?")
    public void sendDailyAccountSummaries() {
        List<Account> accounts = accountRepository.findAll();

        for (Account account : accounts) {
            LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
            List<Transaction> dailyTransactions = transactionRepository
                    .findBySenderAndTimestampAfter(account, yesterday);

            if (!dailyTransactions.isEmpty()) {
                double totalSpent = dailyTransactions.stream()
                        .mapToDouble(Transaction::getAmount)
                        .sum();

                notificationService.createNotification(
                        account,
                        String.format("Daily Summary: %d transactions totaling $%.2f",
                                dailyTransactions.size(), totalSpent),
                        "SUMMARY",
                        "INFO");
            }
        }
    }

    // Run every hour to check for flagged transactions
    @Scheduled(fixedRate = 3600000)
    public void checkFlaggedTransactions() {
        List<Transaction> flaggedTransactions = transactionRepository
                .findByStatus("FLAGGED");

        for (Transaction transaction : flaggedTransactions) {
            // If flagged for more than 24 hours, create reminder notification
            if (transaction.getTimestamp().isBefore(LocalDateTime.now().minusHours(24))) {
                notificationService.createNotification(
                        transaction.getSender(),
                        "You have a flagged transaction that requires attention",
                        "TRANSACTION",
                        "WARNING");
            }
        }
    }
}