package com.bank.service;

import com.bank.entity.Account;
import com.bank.entity.Transaction;
import com.bank.entity.TransactionStatus;
import com.bank.entity.TransactionType;
import com.bank.repository.AccountRepository;
import com.bank.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;


@Service
public class TransactionService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository txnRepo;
    private Account sender;

    @Transactional
    public String transfer(String from, String to, BigDecimal amount, Long userId) {
        System.out.println("FROM = " + from);
        System.out.println("TO = " + to);
        Account sender = accountRepository.findByAccountNumber(from)
                .orElseThrow(() -> new RuntimeException("Sender account not found"));

        Account receiver = accountRepository.findByAccountNumber(to)
                .orElseThrow(() -> new RuntimeException("Receiver account not found"));

        if (sender.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        // Deduct
        sender.setBalance(sender.getBalance().subtract(amount));

        // Add
        receiver.setBalance(receiver.getBalance().add(amount));

        accountRepository.save(sender);
        accountRepository.save(receiver);

        return "Transfer successful";
    }
}