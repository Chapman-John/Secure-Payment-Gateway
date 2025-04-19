package com.bankapp.onlinebanking.entity;

import jakarta.persistence.*;
// import lombok.Getter;
// import lombok.Setter;
// import lombok.AllArgsConstructor;
// import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

// @Getter
// @Setter
// @AllArgsConstructor
// @NoArgsConstructor
@Entity
@Table(name = "accounts")
@Data
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // @Getter
    // @Setter
    private Long id;

    // @Column(name = "account_holder_name", nullable = false, length = 100)
    // @Getter
    // @Setter
    @NotBlank
    private String accountHolderName;

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    // @Column(nullable = false)
    // @Getter
    // @Setter
    private String accountNumber;

    private Double balance = 0.0;
    private String accountType;

    public Account() {
        this.balance = 0.0;
        this.accountType = "SAVINGS"; // Default account type
        this.accountNumber = generateAccountNumber(); // Will add helper method
    }

    public Account(String accountHolderName, double balance) {
        this.accountHolderName = accountHolderName;
        this.balance = balance;
        this.accountType = "SAVINGS"; // Default account type
        this.accountNumber = generateAccountNumber();
    }

    // Add new constructor with all fields
    public Account(String accountHolderName, String username, String password, 
                  String accountNumber, Double balance, String accountType) {
        this.accountHolderName = accountHolderName;
        this.username = username;
        this.password = password;
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.accountType = accountType;
    }

    // Helper method to generate account number
    private String generateAccountNumber() {
        // Simple implementation - you might want to make this more sophisticated
        return "ACC" + System.currentTimeMillis();
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAccountHolderName() {
        return this.accountHolderName;
    }

    public void setAccountHolderName(String accountHolderName) {
        this.accountHolderName = accountHolderName;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Double getBalance() {
        return this.balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public String getAccountNumber() {
        return this.accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getAccountType() {
        return this.accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

}
