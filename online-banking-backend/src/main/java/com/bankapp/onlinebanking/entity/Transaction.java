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
    private String status; // PENDING, COMPLETED, FAILED, FLAGGED, DISPUTED
    private String description;
    private String transactionType; // TRANSFER, DEPOSIT, WITHDRAWAL
    private String referenceNumber;

    // categorization
    private String category; // GROCERIES, GAS, DINING, SHOPPING, BILLS, TRANSFER, etc.
    private String subcategory; // ONLINE, IN-STORE, etc.
    private Boolean isRecurring = false;
    private String recurringPattern; // WEEKLY, MONTHLY, QUARTERLY, etc.

    // Merchant information
    private String merchantName;
    private String merchantCategory;
    private String merchantLocation;

    // Dispute information
    private Boolean isDisputed = false;
    private String disputeReason;
    private LocalDateTime disputeDate;
    private String disputeStatus; // PENDING, RESOLVED, REJECTED
    private String disputeResolution;

    // For fraud detection
    private String ipAddress;
    private String deviceInfo;
    private String location;
    private Boolean isFraudSuspected = false;
    private String fraudReason;

    // Additional metadata
    @Column(columnDefinition = "TEXT")
    private String notes;
    private String tags; // Comma-separated tags for flexible categorization

    // Balance after transaction (for account reconciliation)
    private Double balanceAfter;

    // Getters and Setters (keeping existing ones and adding new ones)
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Boolean getIsDisputed() {
        return isDisputed;
    }

    public void setIsDisputed(Boolean isDisputed) {
        this.isDisputed = isDisputed;
    }

    public String getDisputeReason() {
        return disputeReason;
    }

    public void setDisputeReason(String disputeReason) {
        this.disputeReason = disputeReason;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public Boolean getIsRecurring() {
        return isRecurring;
    }

    public void setIsRecurring(Boolean isRecurring) {
        this.isRecurring = isRecurring;
    }
}