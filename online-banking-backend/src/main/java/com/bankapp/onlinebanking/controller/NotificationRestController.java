package com.bankapp.onlinebanking.controller;

import com.bankapp.onlinebanking.entity.Account;
import com.bankapp.onlinebanking.entity.Notification;
import com.bankapp.onlinebanking.repository.AccountRepository;
import com.bankapp.onlinebanking.repository.NotificationRepository;
import com.bankapp.onlinebanking.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationRestController {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/{accountId}")
    public ResponseEntity<List<Notification>> getNotifications(@PathVariable Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        List<Notification> notifications = notificationRepository.findByAccountOrderByTimestampDesc(account);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/{accountId}/unread")
    public ResponseEntity<List<Notification>> getUnreadNotifications(@PathVariable Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        List<Notification> notifications = notificationRepository
                .findByAccountAndIsReadFalseOrderByTimestampDesc(account);
        return ResponseEntity.ok(notifications);
    }

    @PutMapping("/{notificationId}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long notificationId) {
        notificationService.markAsRead(notificationId);
        return ResponseEntity.ok().build();
    }
}
