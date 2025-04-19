package com.bankapp.onlinebanking.repository;

import com.bankapp.onlinebanking.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByAccountHolderName(String accountHolderName);

    Optional<Account> findByUsername(String username);

    Optional<Account> findByAccountNumber(String accountNumber);

}
