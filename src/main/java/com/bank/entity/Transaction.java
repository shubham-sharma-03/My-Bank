package com.bank.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

// ✅ NO spring import here at all

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String senderAccount;
    private String receiverAccount;

    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private TransactionType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus status;

    private LocalDateTime createdAt = LocalDateTime.now();

    public Transaction() {}

    // ===== GETTERS =====
    public Long getId() { return id; }
    public String getSenderAccount() { return senderAccount; }
    public String getReceiverAccount() { return receiverAccount; }
    public BigDecimal getAmount() { return amount; }
    public TransactionType getType() { return type; }
    public TransactionStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    // ===== SETTERS =====
    public void setSenderAccount(String senderAccount) { this.senderAccount = senderAccount; }
    public void setReceiverAccount(String receiverAccount) { this.receiverAccount = receiverAccount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public void setType(TransactionType type) { this.type = type; }
    public void setStatus(TransactionStatus status) { this.status = status; } // ✅ ONE setter only

    public void setSenderAccount(Account sender) {

    }

    public void setReceiverAccount(Account receiver) {
    }
}