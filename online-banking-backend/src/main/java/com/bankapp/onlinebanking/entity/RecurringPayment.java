package com.bankapp.onlinebanking.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "recurring_payments")
@Data
public class RecurringPayment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;
    
    @ManyToOne
    @JoinColumn(name = "contact_id")
    private Contact contact;
    
    private Double amount;
    private String frequency; // DAILY, WEEKLY, MONTHLY, YEARLY
    private LocalDateTime nextPaymentDate;
    private LocalDateTime endDate;
    private String description;
    private Boolean isActive = true;
} 