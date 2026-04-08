package com.bank.controller;

import com.bank.dto.AccountRequest;
import com.bank.entity.Account;
import com.bank.repository.AccountRepository;
import com.bank.service.AccountService;
import com.bank.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionService transactionService;

    @GetMapping("/user/{userId}")
    public List<Account> getUserAccounts(@PathVariable Long userId) {
        return accountService.getAccountsByUserId(userId);
    }

    @GetMapping("/account/{accountNumber}")
    public ResponseEntity<?> getAccountDetails(@PathVariable String accountNumber) {

        Account acc = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        Map<String, Object> response = new HashMap<>();
        response.put("name", acc.getUser().getName());
        response.put("accountType", acc.getAccountType());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/create")
    public Account createAccount(@RequestBody AccountRequest request) {
        return accountService.createAccount(
                request.getUserId(),
                request.getAccountType(),
                request.getInitialBalance()
        );
    }

    @PutMapping("/deposit/{accountNumber}")
    public Account deposit(@PathVariable String accountNumber,
                           @RequestParam BigDecimal amount) {
        return accountService.deposit(accountNumber, amount);
    }

    @PostMapping("/transfer")
    public ResponseEntity<?> transfer(@RequestBody Map<String, Object> req) {

        String from = (String) req.get("fromAccount");
        String to = (String) req.get("toAccount");
        BigDecimal amount = new BigDecimal(req.get("amount").toString());
        Long userId = Long.parseLong(req.get("userId").toString());

        return ResponseEntity.ok(transactionService.transfer(from, to, amount, userId));
    }

    @DeleteMapping("/delete/{accountNumber}")
    public ResponseEntity<String> deleteAccount(@PathVariable String accountNumber) {
        accountService.deleteAccount(accountNumber);
        return ResponseEntity.ok("Account deleted successfully");
    }
}