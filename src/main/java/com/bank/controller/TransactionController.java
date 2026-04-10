package com.bank.controller;

import com.bank.dto.TransferRequest;
import com.bank.entity.Account;
import com.bank.entity.Transaction;
import com.bank.repository.AccountRepository;
import com.bank.repository.TransactionRepository;
import com.bank.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @Autowired                          // ✅ Fix 1: was missing @Autowired
    private AccountRepository accountRepository;

    @Autowired                          // ✅ Fix 2: txnRepo was never declared
    private TransactionRepository txnRepo;

    @PostMapping("/transfer")
    public ResponseEntity<String> transfer(@RequestBody TransferRequest request) {
        try {
            String result = transactionService.transfer(
                    request.getFromAccount(),
                    request.getToAccount(),
                    request.getAmount(),
                    request.getUserId()
            );
            return ResponseEntity.ok(result);

        } catch (RuntimeException e) {
            String msg = e.getMessage();
            if (msg != null && msg.startsWith("Unauthorized")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(msg);
            }
            return ResponseEntity.badRequest().body(msg);
        }
    }

    @GetMapping("/user/{userId}")       // ✅ renamed from /all to /user (matches frontend)
    public ResponseEntity<?> getAllTransactions(@PathVariable Long userId) {
        try {
            List<Account> accounts = accountRepository.findByUserId(userId);

            if (accounts.isEmpty()) {
                return ResponseEntity.ok(List.of()); // return empty list, not 500
            }

            List<String> accountNumbers = accounts.stream()
                    .map(Account::getAccountNumber)
                    .toList();

            List<Transaction> txns = txnRepo
                    .findBySenderAccountInOrReceiverAccountIn(accountNumbers, accountNumbers);

            return ResponseEntity.ok(txns);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Could not fetch transactions: " + e.getMessage());
        }
    }

    @GetMapping("/history/{account}")
    public ResponseEntity<?> getHistory(@PathVariable String account) {
        try {
            return ResponseEntity.ok(transactionService.getHistory(account));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Could not fetch history: " + e.getMessage());
        }
    }
}
