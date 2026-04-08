package com.bank.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String accountNumber;
    private String accountType;

    public String getAccountNumber() {
        return accountNumber;
    }

    public Long getId() {
        return id;
    }

    public String getAccountType() {
        return accountType;
    }

    public User getUser() {
        return user;
    }

    private BigDecimal balance;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore  // ✅ ignore user to avoid circular reference, but keep other fields
    private User user;
    public Account() {
    }


    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public BigDecimal getBalance() {
        return balance;
    }
}