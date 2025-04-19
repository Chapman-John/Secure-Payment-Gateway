package com.bankapp.onlinebanking.repository;

import com.bankapp.onlinebanking.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    Account findByAccountHolderName(String accountHolderName);

    Account findByUsername(String username);

}
