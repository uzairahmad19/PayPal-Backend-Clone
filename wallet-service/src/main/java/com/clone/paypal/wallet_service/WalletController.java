package com.clone.paypal.wallet_service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;

@RestController
@RequestMapping("/api/wallets")
public class WalletController {

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private KafkaProducerService kafkaProducerService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<Wallet> getWalletByUserId(@PathVariable Long userId) {
        return walletRepository.findByUserId(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Wallet> createWallet(@RequestBody Map<String, Long> request) {
        Long userId = request.get("userId");
        if (userId == null) {
            return ResponseEntity.badRequest().build();
        }

        // --- FIX: Implement "Get or Create" Logic ---
        Optional<Wallet> existingWallet = walletRepository.findByUserId(userId);
        if (existingWallet.isPresent()) {
            return ResponseEntity.ok(existingWallet.get());
        }

        Wallet wallet = new Wallet();
        wallet.setUserId(userId);
        wallet.setBalance(BigDecimal.ZERO);
        wallet.setCurrency("INR");
        Wallet savedWallet = walletRepository.save(wallet);
        return ResponseEntity.ok(savedWallet);
    }

    @PostMapping("/debit")
    public ResponseEntity<Void> debit(@RequestBody WalletTransactionRequest request) {
        Wallet wallet = walletRepository.findByUserId(request.getUserId()).orElse(null);
        if (wallet == null || wallet.getBalance().compareTo(request.getAmount()) < 0) {
            return ResponseEntity.badRequest().build();
        }
        wallet.setBalance(wallet.getBalance().subtract(request.getAmount()));
        walletRepository.save(wallet);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/add")
    public ResponseEntity<Void> addMoney(@RequestBody WalletTransactionRequest request) {
        Wallet wallet = walletRepository.findByUserId(request.getUserId()).orElse(null);
        if (wallet == null) {
            return ResponseEntity.badRequest().build();
        }
        wallet.setBalance(wallet.getBalance().add(request.getAmount()));
        walletRepository.save(wallet);

        // Send notification
        String message = String.format("You added %.2f to your wallet.", request.getAmount().doubleValue());
        kafkaProducerService.sendNotificationEvent(new NotificationRequest(request.getUserId(), message, "system"));

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/user/{userId}")
    @Transactional
    public ResponseEntity<Void> deleteWalletByUserId(@PathVariable Long userId) {
        Optional<Wallet> walletOptional = walletRepository.findByUserId(userId);
        if (walletOptional.isPresent()) {
            walletRepository.delete(walletOptional.get());
        }
        return ResponseEntity.ok().build();
    }
}