package com.bankapp.onlinebanking.repository;

import com.bankapp.onlinebanking.entity.Account;
import com.bankapp.onlinebanking.entity.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {
    List<Contact> findByAccount(Account account);
    List<Contact> findByAccountAndIsFavoriteTrue(Account account);
    Contact findByAccountAndAccountNumber(Account account, String accountNumber);
} 