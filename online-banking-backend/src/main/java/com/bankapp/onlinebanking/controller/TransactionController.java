package com.bankapp.onlinebanking.controller;

import com.bankapp.onlinebanking.entity.Account;
import com.bankapp.onlinebanking.entity.Transaction;
import com.bankapp.onlinebanking.repository.AccountRepository;
import com.bankapp.onlinebanking.repository.TransactionRepository;
import com.bankapp.onlinebanking.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    @GetMapping("/account/{accountId}")
    public ResponseEntity<Page<Transaction>> getTransactionsByAccount(
            @PathVariable Long accountId,
            Pageable pageable,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) Double minAmount,
            @RequestParam(required = false) Double maxAmount) {

        Page<Transaction> transactions = transactionService.getTransactionsByAccountWithFilters(
                accountId, pageable, search, type, status, category, startDate, endDate, minAmount, maxAmount);

        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getTransactionById(@PathVariable Long id) {
        Transaction transaction = transactionService.getTransactionById(id);
        return ResponseEntity.ok(transaction);
    }

    @PostMapping("/{id}/categorize")
    public ResponseEntity<Transaction> categorizeTransaction(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        String category = request.get("category");
        Transaction transaction = transactionService.categorizeTransaction(id, category);
        return ResponseEntity.ok(transaction);
    }

    @PostMapping("/{id}/dispute")
    public ResponseEntity<Transaction> disputeTransaction(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        String reason = request.get("reason");
        Transaction transaction = transactionService.disputeTransaction(id, reason);
        return ResponseEntity.ok(transaction);
    }

    @GetMapping("/account/{accountId}/export")
    public ResponseEntity<byte[]> exportTransactions(
            @PathVariable Long accountId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "csv") String format) {

        try {
            byte[] exportData = transactionService.exportTransactions(accountId, startDate, endDate, format);

            HttpHeaders headers = new HttpHeaders();
            if ("csv".equalsIgnoreCase(format)) {
                headers.setContentType(MediaType.parseMediaType("text/csv"));
                headers.setContentDispositionFormData("attachment", "transactions.csv");
            } else {
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.setContentDispositionFormData("attachment", "transactions.json");
            }

            return new ResponseEntity<>(exportData, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/account/{accountId}/recurring")
    public ResponseEntity<List<Map<String, Object>>> getRecurringTransactions(@PathVariable Long accountId) {
        List<Map<String, Object>> recurringTransactions = transactionService.detectRecurringTransactions(accountId);
        return ResponseEntity.ok(recurringTransactions);
    }

    @GetMapping("/account/{accountId}/categories")
    public ResponseEntity<Map<String, Object>> getTransactionCategories(@PathVariable Long accountId) {
        Map<String, Object> categories = transactionService.getTransactionCategorySummary(accountId);
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/account/{accountId}/analytics")
    public ResponseEntity<Map<String, Object>> getTransactionAnalytics(
            @PathVariable Long accountId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        Map<String, Object> analytics = transactionService.getTransactionAnalytics(accountId, startDate, endDate);
        return ResponseEntity.ok(analytics);
    }
}