package com.bank.controller;

import com.bank.dto.TransferRequest;
import com.bank.entity.Transaction;
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
            // ✅ Return 403 for ownership violation, 400 for others
            String msg = e.getMessage();
            if (msg != null && msg.startsWith("Unauthorized")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(msg);
            }
            return ResponseEntity.badRequest().body(msg);
        }
    }
@Autowired
private TransactionRepository txnRepo;
    // 📜 TRANSACTION HISTORY
    @GetMapping("/history/{account}")
    public List<Transaction> getHistory(@PathVariable String account) {
        return txnRepo.findBySenderAccountOrReceiverAccount(account, account);
    }
}