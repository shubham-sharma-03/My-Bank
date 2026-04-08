package com.bank.service;

import com.bank.entity.Account;
import com.bank.entity.Transaction;
import com.bank.entity.TransactionStatus;
import com.bank.entity.TransactionType;
import com.bank.entity.User;
import com.bank.repository.AccountRepository;
import com.bank.repository.TransactionRepository;
import com.bank.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository txnRepo;

    @Autowired
    private UserRepository userRepository;

    // 🆕 CREATE ACCOUNT
    public Account createAccount(Long userId, String accountType, BigDecimal initialBalance) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        Account account = new Account();
        account.setUser(user);
        account.setAccountType(accountType);
        account.setBalance(initialBalance != null ? initialBalance : BigDecimal.ZERO);
        account.setAccountNumber("ACC" + System.currentTimeMillis());

        return accountRepository.save(account);
    }

    // 💰 DEPOSIT + SAVE TRANSACTION
    @Transactional
    public Account deposit(String accountNumber, BigDecimal amount) {

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Invalid deposit amount");
        }

        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found: " + accountNumber));

        // Ensure balance is not null
        if (account.getBalance() == null) {
            account.setBalance(BigDecimal.ZERO);
        }

        // Update balance
        account.setBalance(account.getBalance().add(amount));
        accountRepository.save(account);

        // 🧾 Save transaction
        Transaction txn = new Transaction();
        txn.setSenderAccount(accountNumber);
        txn.setReceiverAccount(accountNumber);
        txn.setAmount(amount);
        txn.setType(TransactionType.CREDIT);
        txn.setStatus(TransactionStatus.COMPLETED);

        txnRepo.save(txn);

        return account;
    }

    // 📜 GET USER ACCOUNTS
    public List<Account> getAccountsByUserId(Long userId) {
        return accountRepository.findByUserId(userId);
    }
    public void deleteAccount(String accountNumber) {

        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        // Optional: prevent deleting account with balance
        if (account.getBalance() != null && account.getBalance().compareTo(BigDecimal.ZERO) > 0) {
            throw new RuntimeException("Account must be empty before deletion");
        }

        accountRepository.delete(account);
    }

}