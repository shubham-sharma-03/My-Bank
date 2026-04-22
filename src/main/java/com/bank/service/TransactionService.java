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
import java.util.List;


@Service
public class TransactionService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository txnRepo;
    private Account sender;


    public List<Transaction> getHistory(String accountNumber) {
        return txnRepo.findBySenderAccountOrReceiverAccount(accountNumber, accountNumber);
    }

    @Transactional
    public String transfer(String fromAcc, String toAcc, BigDecimal amount, Long userId) {

        Account sender = accountRepository.findByAccountNumber(fromAcc)
                .orElseThrow(() -> new RuntimeException("Sender not found"));

        Account receiver = accountRepository.findByAccountNumber(toAcc)
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        // ❌ prevent same account transfer
        if (fromAcc.equals(toAcc)) {
            throw new RuntimeException("Cannot transfer to same account");
        }

        // ❌ insufficient balance
        if (sender.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        // 💸 update balances
        sender.setBalance(sender.getBalance().subtract(amount));
        receiver.setBalance(receiver.getBalance().add(amount));

        accountRepository.save(sender);
        accountRepository.save(receiver);

        // 🔥 FIXED TRANSACTION ENTRY
        Transaction txn = new Transaction();
        txn.setSenderAccount(fromAcc);   // ✅ correct sender
        txn.setReceiverAccount(toAcc);   // ✅ correct receiver
        txn.setAmount(amount);
        txn.setType(TransactionType.TRANSFER);
        txn.setStatus(TransactionStatus.COMPLETED);

        txnRepo.save(txn);

        return "Transfer successful";
    }
}
