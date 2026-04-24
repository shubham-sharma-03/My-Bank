package com.bank.repository;

import com.bank.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    List<Transaction> findBySenderAccountOrReceiverAccount(String sender, String receiver);
    List<Transaction> findBySenderAccountInOrReceiverAccountIn(List<String> sender, List<String> receiver);
    
}
