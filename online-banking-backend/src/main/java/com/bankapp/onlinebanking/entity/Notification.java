package com.bankapp.onlinebanking.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    private String message;
    private String notificationType; // TRANSACTION, SECURITY, SYSTEM, etc.
    private LocalDateTime timestamp = LocalDateTime.now();
    private Boolean isRead = false;
    private String severity; // INFO, WARNING, CRITICAL

    // For linking to specific entities
    private Long referenceId;
    private String referenceType; // TRANSACTION, LOGIN, etc.

    // Optional additional data as JSON
    @Column(columnDefinition = "TEXT")
    private String additionalData;
}
