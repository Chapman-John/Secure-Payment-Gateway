package com.bankapp.onlinebanking.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Data
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "sender_id")
    private Account sender;
    
    @ManyToOne
    @JoinColumn(name = "recipient_id")
    private Account recipient;
    
    private Double amount;
    private LocalDateTime timestamp;
    private String status; // PENDING, COMPLETED, FAILED, FLAGGED
    private String description;
    private String transactionType; // TRANSFER, DEPOSIT, WITHDRAWAL
    private String referenceNumber;
    
    // For fraud detection
    private String ipAddress;
    private String deviceInfo;
    private String location;
    private Boolean isFraudSuspected = false;
    private String fraudReason;

    // Getters and Setters
    public Account getSender() {
        return sender;
    }

    public void setSender(Account sender) {
        this.sender = sender;
    }

    public Account getRecipient() {
        return recipient;
    }

    public void setRecipient(Account recipient) {
        this.recipient = recipient;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public Boolean getIsFraudSuspected() {
        return isFraudSuspected;
    }

    public void setIsFraudSuspected(Boolean isFraudSuspected) {
        this.isFraudSuspected = isFraudSuspected;
    }

    public String getFraudReason() {
        return fraudReason;
    }

    public void setFraudReason(String fraudReason) {
        this.fraudReason = fraudReason;
    }
} 