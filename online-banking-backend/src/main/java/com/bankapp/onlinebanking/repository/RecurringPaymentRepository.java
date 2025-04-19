package com.bankapp.onlinebanking.repository;

import com.bankapp.onlinebanking.entity.Account;
import com.bankapp.onlinebanking.entity.RecurringPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RecurringPaymentRepository extends JpaRepository<RecurringPayment, Long> {
    List<RecurringPayment> findByAccount(Account account);
    List<RecurringPayment> findByAccountAndIsActiveTrue(Account account);
    List<RecurringPayment> findByNextPaymentDateBeforeAndIsActiveTrue(LocalDateTime date);
} 