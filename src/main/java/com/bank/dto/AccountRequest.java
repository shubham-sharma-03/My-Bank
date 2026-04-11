package com.bank.dto;

import java.math.BigDecimal;

public class AccountRequest {
    private Long userId;
    private String accountType;
    private BigDecimal initialBalance;  

    // Getters
    public Long getUserId() { return userId; }
    public String getAccountType() { return accountType; }
    public BigDecimal getInitialBalance() { return initialBalance; }

    // Setters
    public void setUserId(Long userId) { this.userId = userId; }
    public void setAccountType(String accountType) { this.accountType = accountType; }
    public void setInitialBalance(BigDecimal initialBalance) { this.initialBalance = initialBalance; }
}
