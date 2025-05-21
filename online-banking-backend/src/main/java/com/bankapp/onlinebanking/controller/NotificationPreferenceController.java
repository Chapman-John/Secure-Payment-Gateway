package com.bankapp.onlinebanking.controller;

import com.bankapp.onlinebanking.entity.Account;
import com.bankapp.onlinebanking.entity.NotificationPreference;
import com.bankapp.onlinebanking.repository.AccountRepository;
import com.bankapp.onlinebanking.repository.NotificationPreferenceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications/preferences")
public class NotificationPreferenceController {

    @Autowired
    private NotificationPreferenceRepository preferenceRepository;

    @Autowired
    private AccountRepository accountRepository;

    @GetMapping("/{accountId}")
    public ResponseEntity<NotificationPreference> getPreferences(@PathVariable Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        NotificationPreference preferences = preferenceRepository.findByAccount(account)
                .orElseGet(() -> {
                    NotificationPreference newPrefs = new NotificationPreference();
                    newPrefs.setAccount(account);
                    return preferenceRepository.save(newPrefs);
                });

        return ResponseEntity.ok(preferences);
    }

    @PutMapping("/{accountId}")
    public ResponseEntity<NotificationPreference> updatePreferences(
            @PathVariable Long accountId,
            @RequestBody NotificationPreference preferences) {

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        NotificationPreference existingPrefs = preferenceRepository.findByAccount(account)
                .orElseGet(() -> {
                    NotificationPreference newPrefs = new NotificationPreference();
                    newPrefs.setAccount(account);
                    return newPrefs;
                });

        // Update preferences
        existingPrefs.setEnableRealTimeNotifications(preferences.getEnableRealTimeNotifications());
        existingPrefs.setEnableEmailNotifications(preferences.getEnableEmailNotifications());
        existingPrefs.setEmailForTransactions(preferences.getEmailForTransactions());
        existingPrefs.setEmailForSecurity(preferences.getEmailForSecurity());
        existingPrefs.setEmailForSystem(preferences.getEmailForSystem());
        existingPrefs.setEmailTransactionThreshold(preferences.getEmailTransactionThreshold());
        existingPrefs.setEnableSmsNotifications(preferences.getEnableSmsNotifications());
        existingPrefs.setSmsForTransactions(preferences.getSmsForTransactions());
        existingPrefs.setSmsForSecurity(preferences.getSmsForSecurity());
        existingPrefs.setSmsForSystem(preferences.getSmsForSystem());
        existingPrefs.setSmsTransactionThreshold(preferences.getSmsTransactionThreshold());

        existingPrefs = preferenceRepository.save(existingPrefs);

        return ResponseEntity.ok(existingPrefs);
    }
}