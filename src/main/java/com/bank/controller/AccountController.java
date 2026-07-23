package com.bank.controller;

import com.bank.dto.AccountRequest;
import com.bank.entity.Account;
import com.bank.repository.AccountRepository;
import com.bank.service.AccountService;
import com.bank.service.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;
    private final AccountRepository accountRepository;
    private final TransactionService transactionService;

    public AccountController(AccountService accountService,
                             AccountRepository accountRepository,
                             TransactionService transactionService) {
        this.accountService = accountService;
        this.accountRepository = accountRepository;
        this.transactionService = transactionService;
    }

    // 📄 GET ALL ACCOUNTS FOR A USER
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserAccounts(@PathVariable Long userId) {
        try {
            List<Account> accounts = accountService.getAccountsByUserId(userId);
            return ResponseEntity.ok(accounts);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Could not fetch accounts: " + e.getMessage());
        }
    }

    // 📄 GET SINGLE ACCOUNT DETAILS
    @GetMapping("/account/{accountNumber}")
    public ResponseEntity<?> getAccountDetails(@PathVariable String accountNumber) {
        try {
            Account acc = accountRepository.findByAccountNumber(accountNumber)
                    .orElseThrow(() -> new RuntimeException("Account not found"));

            Map<String, Object> response = new HashMap<>();
            response.put("accountNumber", acc.getAccountNumber());
            response.put("accountType", acc.getAccountType());
            response.put("balance", acc.getBalance());
            response.put("userId", acc.getUser() != null ? acc.getUser().getId() : null);

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Could not fetch account: " + e.getMessage());
        }
    }

    // ➕ CREATE NEW ACCOUNT
    @PostMapping("/create")
    public ResponseEntity<?> createAccount(@RequestBody AccountRequest request) {
        try {
            Account account = accountService.createAccount(
                    request.getUserId(),
                    request.getAccountType(),
                    request.getInitialBalance()
            );
            return ResponseEntity.ok(account);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Could not create account: " + e.getMessage());
        }
    }

    // 💰 DEPOSIT
    @PutMapping("/deposit/{accountNumber}")
    public ResponseEntity<?> deposit(@PathVariable String accountNumber,
                                     @RequestParam BigDecimal amount) {
        try {
            Account account = accountService.deposit(accountNumber, amount);
            return ResponseEntity.ok(account);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Deposit failed: " + e.getMessage());
        }
    }

    // 💸 TRANSFER (note: TransactionController also has /api/transactions/transfer —
    // this is a duplicate route under /api/accounts/transfer; kept as-is for compatibility
    // in case anything else in the app still calls this path)
    @PostMapping("/transfer")
    public ResponseEntity<?> transfer(@RequestBody Map<String, Object> req) {
        try {
            String from = (String) req.get("fromAccount");
            String to = (String) req.get("toAccount");

            if (from == null || to == null || req.get("amount") == null || req.get("userId") == null) {
                return ResponseEntity.badRequest().body("Missing required fields");
            }

            BigDecimal amount = new BigDecimal(req.get("amount").toString());
            Long userId = Long.parseLong(req.get("userId").toString());

            Object result = transactionService.transfer(from, to, amount, userId);
            return ResponseEntity.ok(result);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Transfer failed: " + e.getMessage());
        }
    }
}