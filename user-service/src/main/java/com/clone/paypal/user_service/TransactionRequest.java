package com.clone.paypal.user_service;

import java.math.BigDecimal;

public class TransactionRequest {
    private Long senderId;
    private String recipientEmail; // Using email to find the recipient user
    private BigDecimal amount;
    private String description;

    // Constructors
    public TransactionRequest() {}

    public TransactionRequest(Long senderId, String recipientEmail, BigDecimal amount, String description) {
        this.senderId = senderId;
        this.recipientEmail = recipientEmail;
        this.amount = amount;
        this.description = description;
    }

    // Getters and Setters
    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public String getRecipientEmail() {
        return recipientEmail;
    }

    public void setRecipientEmail(String recipientEmail) {
        this.recipientEmail = recipientEmail;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}