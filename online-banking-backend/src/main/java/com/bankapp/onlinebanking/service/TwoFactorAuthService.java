package com.bankapp.onlinebanking.service;

import com.bankapp.onlinebanking.entity.Account;
import com.bankapp.onlinebanking.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
public class TwoFactorAuthService {
    
    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private SmsService smsService;
    
    private static final int SECRET_LENGTH = 6;
    private static final int SECRET_EXPIRY_MINUTES = 5;
    
    public String generateAndSendSecret(Account account, String method) {
        String secret = generateSecret();
        account.setTwoFactorSecret(secret);
        account.setSecretExpiry(LocalDateTime.now().plusMinutes(SECRET_EXPIRY_MINUTES));
        accountRepository.save(account);
        
        if ("email".equals(method)) {
            emailService.sendTwoFactorCode(account.getEmail(), secret);
        } else if ("sms".equals(method)) {
            smsService.sendTwoFactorCode(account.getPhoneNumber(), secret);
        }
        
        return secret;
    }
    
    public boolean validateSecret(Account account, String providedSecret) {
        if (account.getTwoFactorSecret() == null || 
            account.getSecretExpiry().isBefore(LocalDateTime.now())) {
            return false;
        }
        
        boolean isValid = account.getTwoFactorSecret().equals(providedSecret);
        if (isValid) {
            // Clear the secret after successful validation
            account.setTwoFactorSecret(null);
            account.setSecretExpiry(null);
            accountRepository.save(account);
        }
        
        return isValid;
    }
    
    private String generateSecret() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[SECRET_LENGTH];
        random.nextBytes(bytes);
        // Generate a numeric code of length SECRET_LENGTH
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < SECRET_LENGTH; i++) {
            sb.append(Math.abs(bytes[i] % 10));
        }
        return sb.toString();
    }
} 