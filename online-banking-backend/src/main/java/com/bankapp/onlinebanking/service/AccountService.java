package com.bankapp.onlinebanking.service;

import com.bankapp.onlinebanking.entity.Account;
import com.bankapp.onlinebanking.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class AccountService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    @Autowired
    private NotificationService notificationService;

    @Autowired
    public AccountService(AccountRepository accountRepository, PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Account createAccount(Account account) {
        if (accountRepository.findByUsername(account.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }
        // Encode the password before saving
        account.setPassword(passwordEncoder.encode(account.getPassword()));
        return accountRepository.save(account);
    }

    public Account login(String username, String password) {
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));

        // Use password encoder to verify the password
        if (!passwordEncoder.matches(password, account.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }
        return account;
    }

    // Remaining methods stay the same
    public Boolean accountExists(String username) {
        return accountRepository.findByUsername(username).isPresent();
    }

    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    public Account getAccountById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found"));
    }

    public double getBalance(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        return account.getBalance();
    }

    public Account depositAmount(Long id, double amount) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        account.setBalance(account.getBalance() + amount);
        // Create notification
        notificationService.createNotification(
                account,
                String.format("$%.2f was deposited to your account", amount),
                "TRANSACTION",
                "INFO");

        return accountRepository.save(account);
    }

    public Account withdrawAmount(Long id, double amount) {
        Account account = getAccountById(id);
        if (account.getBalance() < amount) {
            throw new RuntimeException("Insufficient funds in account with ID: " + id);
        }
        account.setBalance(account.getBalance() - amount);
        // Create notification
        notificationService.createNotification(
                account,
                String.format("$%.2f was withdrawn from your account", amount),
                "TRANSACTION",
                "INFO");

        return accountRepository.save(account);
    }

    public void transferMoney(Long fromAccountId, Long toAccountId, double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Transfer amount must be greater than zero");
        }

        Account fromAccount = accountRepository.findById(fromAccountId)
                .orElseThrow(() -> new RuntimeException("Sender account not found"));

        Account toAccount = accountRepository.findById(toAccountId)
                .orElseThrow(() -> new RuntimeException("Receiver account not found"));

        if (fromAccount.getBalance() < amount) {
            throw new RuntimeException("Insufficient funds in sender's account");
        }

        fromAccount.setBalance(fromAccount.getBalance() - amount);
        toAccount.setBalance(toAccount.getBalance() + amount);

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        notificationService.createNotification(
                fromAccount,
                String.format("$%.2f was transferred to account ending in %s",
                        amount, toAccount.getAccountNumber().substring(toAccount.getAccountNumber().length() - 4)),
                "TRANSACTION",
                "INFO");

        notificationService.createNotification(
                toAccount,
                String.format("$%.2f was received from account ending in %s",
                        amount, fromAccount.getAccountNumber().substring(fromAccount.getAccountNumber().length() - 4)),
                "TRANSACTION",
                "INFO");
    }

}