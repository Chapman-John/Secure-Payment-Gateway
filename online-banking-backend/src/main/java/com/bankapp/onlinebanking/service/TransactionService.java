package com.bankapp.onlinebanking.service;

import com.bankapp.onlinebanking.entity.Account;
import com.bankapp.onlinebanking.entity.Transaction;
import com.bankapp.onlinebanking.repository.AccountRepository;
import com.bankapp.onlinebanking.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

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

    // Category mapping for auto-categorization
    private static final Map<String, String> MERCHANT_CATEGORIES = Map.of(
            "walmart", "GROCERIES",
            "target", "SHOPPING",
            "mcdonalds", "DINING",
            "starbucks", "DINING",
            "shell", "GAS",
            "exxon", "GAS",
            "amazon", "SHOPPING",
            "netflix", "ENTERTAINMENT",
            "spotify", "ENTERTAINMENT",
            "uber", "TRANSPORTATION");

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

        // Auto-categorize transaction
        transaction.setCategory(autoCategorizeTransaction(description));

        // Extract merchant name if possible
        transaction.setMerchantName(extractMerchantName(description));

        // Check for fraud
        boolean isFraudulent = fraudDetectionService.analyzeTransaction(transaction);

        if (isFraudulent) {
            transaction.setStatus("FLAGGED");
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
                    if (recipient != null) {
                        recipient.setBalance(recipient.getBalance() + amount);
                        accountRepository.save(recipient);
                    }
                    transaction.setBalanceAfter(sender.getBalance());
                    accountRepository.save(sender);
                    transaction.setStatus("COMPLETED");

                    // Check if this might be a recurring transaction
                    checkRecurringPattern(transaction);

                    // Notify both parties
                    notificationService.createTransactionNotification(transaction);

                    if (recipient != null) {
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
        }

        return transactionRepository.save(transaction);
    }

    public Page<Transaction> getTransactionsByAccountWithFilters(
            Long accountId, Pageable pageable, String search, String type,
            String status, String category, LocalDateTime startDate,
            LocalDateTime endDate, Double minAmount, Double maxAmount) {

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        return transactionRepository.findTransactionsWithFilters(
                account, search, type, status, category,
                startDate, endDate, minAmount, maxAmount, pageable);
    }

    public Transaction getTransactionById(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
    }

    @Transactional
    public Transaction categorizeTransaction(Long transactionId, String category) {
        Transaction transaction = getTransactionById(transactionId);
        transaction.setCategory(category);
        return transactionRepository.save(transaction);
    }

    @Transactional
    public Transaction disputeTransaction(Long transactionId, String reason) {
        Transaction transaction = getTransactionById(transactionId);
        transaction.setIsDisputed(true);
        transaction.setDisputeReason(reason);
        transaction.setDisputeDate(LocalDateTime.now());
        transaction.setDisputeStatus("PENDING");
        transaction.setStatus("DISPUTED");

        // Notify account holder
        notificationService.createNotification(
                transaction.getSender(),
                "Your transaction dispute has been submitted and is under review",
                "TRANSACTION",
                "INFO");

        return transactionRepository.save(transaction);
    }

    public byte[] exportTransactions(Long accountId, LocalDateTime startDate,
            LocalDateTime endDate, String format) throws Exception {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        List<Transaction> transactions = transactionRepository.findTransactionsForExport(
                account, startDate, endDate);

        if ("csv".equalsIgnoreCase(format)) {
            return exportToCsv(transactions);
        } else {
            return exportToJson(transactions);
        }
    }

    public List<Map<String, Object>> detectRecurringTransactions(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        LocalDateTime threeMonthsAgo = LocalDateTime.now().minusMonths(3);
        List<Object[]> results = transactionRepository.findPotentialRecurringTransactions(
                account, threeMonthsAgo, 3L);

        List<Map<String, Object>> recurringTransactions = new ArrayList<>();
        for (Object[] result : results) {
            Map<String, Object> recurring = new HashMap<>();
            recurring.put("description", result[0]);
            recurring.put("amount", result[1]);
            recurring.put("merchantName", result[2]);
            recurring.put("frequency", result[3]);
            recurring.put("firstTransaction", result[4]);
            recurring.put("lastTransaction", result[5]);
            recurringTransactions.add(recurring);
        }

        return recurringTransactions;
    }

    public Map<String, Object> getTransactionCategorySummary(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        List<Object[]> categoryData = transactionRepository.findCategoryAnalysis(
                account, startOfMonth, null);

        Map<String, Object> summary = new HashMap<>();
        List<Map<String, Object>> categories = new ArrayList<>();

        for (Object[] data : categoryData) {
            Map<String, Object> category = new HashMap<>();
            category.put("category", data[0]);
            category.put("count", data[1]);
            category.put("totalAmount", data[2]);
            categories.add(category);
        }

        summary.put("categories", categories);
        summary.put("period", "Current Month");
        return summary;
    }

    public Map<String, Object> getTransactionAnalytics(Long accountId,
            LocalDateTime startDate,
            LocalDateTime endDate) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (startDate == null) {
            startDate = LocalDateTime.now().minusMonths(6);
        }
        if (endDate == null) {
            endDate = LocalDateTime.now();
        }

        Map<String, Object> analytics = new HashMap<>();

        // Basic stats
        Long totalTransactions = transactionRepository.countTransactionsByDateRange(account, startDate, endDate);
        Double totalSpent = transactionRepository.sumTransactionsByDateRange(account, startDate, endDate);

        analytics.put("totalTransactions", totalTransactions);
        analytics.put("totalSpent", totalSpent != null ? totalSpent : 0.0);
        analytics.put("averageTransaction",
                totalTransactions > 0 ? (totalSpent != null ? totalSpent : 0.0) / totalTransactions : 0.0);

        // Monthly breakdown
        List<Object[]> monthlyData = transactionRepository.findMonthlySpending(account, startDate);
        List<Map<String, Object>> monthlySpending = new ArrayList<>();

        for (Object[] data : monthlyData) {
            Map<String, Object> month = new HashMap<>();
            month.put("year", data[0]);
            month.put("month", data[1]);
            month.put("amount", data[2]);
            monthlySpending.add(month);
        }

        analytics.put("monthlySpending", monthlySpending);

        // Category breakdown
        List<Object[]> categoryData = transactionRepository.findCategoryAnalysis(account, startDate, endDate);
        List<Map<String, Object>> categoryBreakdown = new ArrayList<>();

        for (Object[] data : categoryData) {
            Map<String, Object> category = new HashMap<>();
            category.put("category", data[0]);
            category.put("count", data[1]);
            category.put("totalAmount", data[2]);
            categoryBreakdown.add(category);
        }

        analytics.put("categoryBreakdown", categoryBreakdown);

        return analytics;
    }

    // Helper methods
    private String generateReferenceNumber() {
        return "TXN" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private String autoCategorizeTransaction(String description) {
        if (description == null)
            return "OTHER";

        String lowerDesc = description.toLowerCase();

        for (Map.Entry<String, String> entry : MERCHANT_CATEGORIES.entrySet()) {
            if (lowerDesc.contains(entry.getKey())) {
                return entry.getValue();
            }
        }

        // Additional pattern matching
        if (lowerDesc.contains("grocery") || lowerDesc.contains("supermarket")) {
            return "GROCERIES";
        } else if (lowerDesc.contains("gas") || lowerDesc.contains("fuel")) {
            return "GAS";
        } else if (lowerDesc.contains("restaurant") || lowerDesc.contains("cafe")) {
            return "DINING";
        } else if (lowerDesc.contains("transfer")) {
            return "TRANSFER";
        } else if (lowerDesc.contains("atm") || lowerDesc.contains("withdrawal")) {
            return "ATM";
        }

        return "OTHER";
    }

    private String extractMerchantName(String description) {
        if (description == null)
            return null;

        // Simple extraction - in a real app, you'd use more sophisticated parsing
        String[] parts = description.split(" ");
        return parts.length > 0 ? parts[0] : description;
    }

    private void checkRecurringPattern(Transaction transaction) {
        LocalDateTime twoMonthsAgo = LocalDateTime.now().minusMonths(2);
        List<Transaction> similarTransactions = transactionRepository.findSimilarRecentTransactions(
                transaction.getSender(),
                transaction.getDescription(),
                transaction.getAmount(),
                twoMonthsAgo);

        if (similarTransactions.size() >= 2) {
            transaction.setIsRecurring(true);
            transaction.setRecurringPattern("MONTHLY"); // Simplified pattern detection
        }
    }

    private byte[] exportToCsv(List<Transaction> transactions) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(baos);

        // CSV Header
        writer.println("Date,Description,Amount,Type,Category,Status,Reference");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        for (Transaction transaction : transactions) {
            writer.printf("%s,%s,%.2f,%s,%s,%s,%s%n",
                    transaction.getTimestamp().format(formatter),
                    escapeCommas(transaction.getDescription()),
                    transaction.getAmount(),
                    transaction.getTransactionType(),
                    transaction.getCategory() != null ? transaction.getCategory() : "OTHER",
                    transaction.getStatus(),
                    transaction.getReferenceNumber());
        }

        writer.flush();
        writer.close();
        return baos.toByteArray();
    }

    private byte[] exportToJson(List<Transaction> transactions) throws Exception {
        // Simple JSON export - in production, use Jackson or similar
        StringBuilder json = new StringBuilder();
        json.append("[\n");

        for (int i = 0; i < transactions.size(); i++) {
            Transaction t = transactions.get(i);
            json.append("  {\n");
            json.append("    \"date\": \"").append(t.getTimestamp()).append("\",\n");
            json.append("    \"description\": \"").append(escapeJson(t.getDescription())).append("\",\n");
            json.append("    \"amount\": ").append(t.getAmount()).append(",\n");
            json.append("    \"type\": \"").append(t.getTransactionType()).append("\",\n");
            json.append("    \"category\": \"").append(t.getCategory() != null ? t.getCategory() : "OTHER")
                    .append("\",\n");
            json.append("    \"status\": \"").append(t.getStatus()).append("\",\n");
            json.append("    \"reference\": \"").append(t.getReferenceNumber()).append("\"\n");
            json.append("  }");
            if (i < transactions.size() - 1) {
                json.append(",");
            }
            json.append("\n");
        }

        json.append("]");
        return json.toString().getBytes();
    }

    private String escapeCommas(String value) {
        if (value == null)
            return "";
        return value.contains(",") ? "\"" + value.replace("\"", "\"\"") + "\"" : value;
    }

    private String escapeJson(String value) {
        if (value == null)
            return "";
        return value.replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
    }
}