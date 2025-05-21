package com.bankapp.onlinebanking.repository;

import com.bankapp.onlinebanking.entity.Account;
import com.bankapp.onlinebanking.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByAccountOrderByTimestampDesc(Account account);

    List<Notification> findByAccountAndIsReadFalseOrderByTimestampDesc(Account account);

    long countByAccountAndIsReadFalse(Account account);
}
