package com.bankapp.onlinebanking.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "contacts")
@Data
public class Contact {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;
    
    private String name;
    private String accountNumber;
    private String email;
    private String phoneNumber;
    private Boolean isFavorite = false;
    private String notes;
} 