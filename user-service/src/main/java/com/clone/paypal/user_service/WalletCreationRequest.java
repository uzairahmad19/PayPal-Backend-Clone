package com.clone.paypal.user_service;

public class WalletCreationRequest {
    private Long userId;

    public WalletCreationRequest(Long userId) {
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
