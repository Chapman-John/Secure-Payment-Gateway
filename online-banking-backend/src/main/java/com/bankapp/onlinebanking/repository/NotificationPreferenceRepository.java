package com.bankapp.onlinebanking.repository;

import com.bankapp.onlinebanking.entity.Account;
import com.bankapp.onlinebanking.entity.NotificationPreference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface NotificationPreferenceRepository extends JpaRepository<NotificationPreference, Long> {
    Optional<NotificationPreference> findByAccount(Account account);
}
