package com.clone.paypal.wallet_service;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
    // A custom method to find a wallet by the user's ID
    Optional<Wallet> findByUserId(Long userId);
    void deleteByUserId(Long userId);
}