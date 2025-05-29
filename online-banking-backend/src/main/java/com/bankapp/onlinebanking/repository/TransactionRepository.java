package com.bankapp.onlinebanking.repository;

import com.bankapp.onlinebanking.entity.Account;
import com.bankapp.onlinebanking.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    // Query methods for transaction history
    @Query("SELECT t FROM Transaction t WHERE (t.sender = :account OR t.recipient = :account) " +
            "AND (:search IS NULL OR LOWER(t.description) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR LOWER(t.merchantName) LIKE LOWER(CONCAT('%', :search, '%'))) " +
            "AND (:type IS NULL OR t.transactionType = :type) " +
            "AND (:status IS NULL OR t.status = :status) " +
            "AND (:category IS NULL OR t.category = :category) " +
            "AND (:startDate IS NULL OR t.timestamp >= :startDate) " +
            "AND (:endDate IS NULL OR t.timestamp <= :endDate) " +
            "AND (:minAmount IS NULL OR t.amount >= :minAmount) " +
            "AND (:maxAmount IS NULL OR t.amount <= :maxAmount) " +
            "ORDER BY t.timestamp DESC")
    Page<Transaction> findTransactionsWithFilters(
            @Param("account") Account account,
            @Param("search") String search,
            @Param("type") String type,
            @Param("status") String status,
            @Param("category") String category,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("minAmount") Double minAmount,
            @Param("maxAmount") Double maxAmount,
            Pageable pageable);

    // Find transactions for export
    @Query("SELECT t FROM Transaction t WHERE (t.sender = :account OR t.recipient = :account) " +
            "AND (:startDate IS NULL OR t.timestamp >= :startDate) " +
            "AND (:endDate IS NULL OR t.timestamp <= :endDate) " +
            "ORDER BY t.timestamp DESC")
    List<Transaction> findTransactionsForExport(
            @Param("account") Account account,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    // Recurring transaction detection
    @Query("SELECT t.description, t.amount, t.merchantName, COUNT(t) as frequency, " +
            "MIN(t.timestamp) as firstTransaction, MAX(t.timestamp) as lastTransaction " +
            "FROM Transaction t " +
            "WHERE t.sender = :account " +
            "AND t.timestamp >= :since " +
            "GROUP BY t.description, t.amount, t.merchantName " +
            "HAVING COUNT(t) >= :minOccurrences " +
            "ORDER BY COUNT(t) DESC")
    List<Object[]> findPotentialRecurringTransactions(
            @Param("account") Account account,
            @Param("since") LocalDateTime since,
            @Param("minOccurrences") Long minOccurrences);

    // Category analysis
    @Query("SELECT t.category, COUNT(t) as count, SUM(t.amount) as totalAmount " +
            "FROM Transaction t " +
            "WHERE (t.sender = :account OR t.recipient = :account) " +
            "AND t.category IS NOT NULL " +
            "AND (:startDate IS NULL OR t.timestamp >= :startDate) " +
            "AND (:endDate IS NULL OR t.timestamp <= :endDate) " +
            "GROUP BY t.category " +
            "ORDER BY SUM(t.amount) DESC")
    List<Object[]> findCategoryAnalysis(
            @Param("account") Account account,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    // Monthly spending analysis
    @Query("SELECT YEAR(t.timestamp), MONTH(t.timestamp), SUM(t.amount) " +
            "FROM Transaction t " +
            "WHERE t.sender = :account " +
            "AND t.timestamp >= :startDate " +
            "GROUP BY YEAR(t.timestamp), MONTH(t.timestamp) " +
            "ORDER BY YEAR(t.timestamp), MONTH(t.timestamp)")
    List<Object[]> findMonthlySpending(
            @Param("account") Account account,
            @Param("startDate") LocalDateTime startDate);

    // Find disputed transactions
    List<Transaction> findByIsDisputedTrueAndSender(Account sender);

    // Find transactions by category
    List<Transaction> findBySenderAndCategory(Account sender, String category);

    // Find recent transactions (for recurring detection)
    @Query("SELECT t FROM Transaction t WHERE t.sender = :account " +
            "AND t.description = :description " +
            "AND t.amount = :amount " +
            "AND t.timestamp >= :since " +
            "ORDER BY t.timestamp DESC")
    List<Transaction> findSimilarRecentTransactions(
            @Param("account") Account account,
            @Param("description") String description,
            @Param("amount") Double amount,
            @Param("since") LocalDateTime since);

    // Analytics queries
    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.sender = :account AND t.timestamp >= :startDate AND t.timestamp <= :endDate")
    Long countTransactionsByDateRange(@Param("account") Account account, @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.sender = :account AND t.timestamp >= :startDate AND t.timestamp <= :endDate")
    Double sumTransactionsByDateRange(@Param("account") Account account, @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}