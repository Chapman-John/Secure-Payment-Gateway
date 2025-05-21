package com.bankapp.onlinebanking.controller;

import com.bankapp.onlinebanking.entity.Account;
import com.bankapp.onlinebanking.service.AccountService;
import com.bankapp.onlinebanking.service.NotificationService;
import jakarta.servlet.http.HttpServletRequest;
import com.bankapp.onlinebanking.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    @Autowired
    private final AccountService accountService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private NotificationService notificationService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("/register")
    public ResponseEntity<Account> createAccount(@RequestBody Account account) {
        if (account.getUsername() == null || account.getPassword() == null ||
                account.getAccountHolderName() == null || account.getAccountHolderName().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        if (accountService.accountExists(account.getUsername())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }
        Account saveAccount = accountService.createAccount(account);
        return new ResponseEntity<>(saveAccount, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<Account> login(@RequestBody Map<String, String> credentials, HttpServletRequest request) {
        String username = credentials.get("username");
        String password = credentials.get("password");

        try {
            Account account = accountService.login(username, password);

            // Record login info
            account.setLastLoginIp(request.getRemoteAddr());
            account.setLastLoginTime(LocalDateTime.now());
            account.setLastLoginDevice(request.getHeader("User-Agent"));
            accountRepository.save(account);

            // Create login notification
            notificationService.createNotification(
                    account,
                    "New login to your account",
                    "SECURITY",
                    "INFO");

            return new ResponseEntity<>(account, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Account> getAccountById(@PathVariable Long id) {
        Account account = accountService.getAccountById(id);
        return new ResponseEntity<>(account, HttpStatus.OK);
    }

    @GetMapping("/{id}/balance")
    public ResponseEntity<Double> getAccountBalance(@PathVariable Long id) {
        double balance = accountService.getBalance(id);
        return new ResponseEntity<>(balance, HttpStatus.OK);
    }

    @PostMapping("/{id}/deposit")
    public ResponseEntity<Account> depositAmount(@PathVariable Long id, @RequestBody Map<String, Object> payload) {
        double amount = Double.parseDouble(payload.get("amount").toString());
        if (amount <= 0) {
            throw new IllegalArgumentException("Deposit amount must be greater than zero");
        }
        Account updatedAccount = accountService.depositAmount(id, amount);
        return new ResponseEntity<>(updatedAccount, HttpStatus.OK);
    }

    @PostMapping("/{id}/withdraw")
    public ResponseEntity<Account> withdrawAmount(@PathVariable Long id, @RequestBody Map<String, Object> payload) {
        double amount = Double.parseDouble(payload.get("amount").toString());
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdraw amount must be greater than zero");
        }
        Account updatedAccount = accountService.withdrawAmount(id, amount);
        return new ResponseEntity<>(updatedAccount, HttpStatus.OK);
    }

    @PostMapping("/{fromId}/transfer/{toId}")
    public ResponseEntity<String> transferMoney(
            @PathVariable Long fromId,
            @PathVariable Long toId,
            @RequestBody Map<String, Object> payload) {
        double amount = Double.parseDouble(payload.get("amount").toString());
        accountService.transferMoney(fromId, toId, amount);
        return new ResponseEntity<>("Transfer successful", HttpStatus.OK);
    }
}