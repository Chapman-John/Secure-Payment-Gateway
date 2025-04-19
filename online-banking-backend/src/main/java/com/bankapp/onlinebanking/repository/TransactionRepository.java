package com.bankapp.onlinebanking.repository;

import com.bankapp.onlinebanking.entity.Account;
import com.bankapp.onlinebanking.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findBySender(Account sender);
    List<Transaction> findByRecipient(Account recipient);
    List<Transaction> findBySenderAndTimestampAfter(Account sender, LocalDateTime timestamp);
    
    @Query("SELECT AVG(t.amount) FROM Transaction t WHERE t.sender = ?1")
    Double findAverageTransactionAmountForAccount(Account sender);
    
    List<Transaction> findByStatus(String status);
    List<Transaction> findByIsFraudSuspectedTrue();
} 