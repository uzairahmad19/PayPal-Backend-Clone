package com.clone.paypal.wallet_service;

import java.math.BigDecimal;

public class TransactionRequest {
    private Long userId;
    private BigDecimal amount;

    // Add this empty constructor
    public TransactionRequest() {}

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public TransactionRequest(Long userId, BigDecimal amount) {
        this.userId = userId;
        this.amount = amount;
    }

    // Getters and Setters...
}