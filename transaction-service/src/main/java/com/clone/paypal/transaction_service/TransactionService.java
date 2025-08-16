package com.clone.paypal.transaction_service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
public class TransactionService {
    @Autowired private TransactionRepository transactionRepository;
    @Autowired private RestTemplate restTemplate;
    @Autowired private KafkaProducerService kafkaProducerService;

    private final String walletServiceUrl = "http://WALLET-SERVICE/api/wallets";
    private final String userServiceUrl = "http://USER-SERVICE/api/users";

    public Transaction performTransaction(Long senderId, String recipientEmail, BigDecimal amount, String description) {
        Transaction transaction = new Transaction();
        transaction.setSenderId(senderId);
        transaction.setRecipientId(null);
        transaction.setAmount(amount);
        transaction.setDescription(description);
        transaction.setTimestamp(LocalDateTime.now(ZoneId.of("Asia/Kolkata")));

        User recipientUser = null;
        try {
            recipientUser = restTemplate.getForObject(userServiceUrl + "/email/" + recipientEmail, User.class);
        } catch (HttpClientErrorException.NotFound e) {
            transaction.setStatus("FAILED: Recipient user not found");
            return transactionRepository.save(transaction);
        } catch (Exception e) {
            transaction.setStatus("FAILED: Error fetching recipient user: " + e.getMessage());
            return transactionRepository.save(transaction);
        }

        if (recipientUser == null || recipientUser.getId() == null) {
            transaction.setStatus("FAILED: Recipient user not found or invalid");
            return transactionRepository.save(transaction);
        }

        Long recipientId = recipientUser.getId();
        transaction.setRecipientId(recipientId);

        try {
            restTemplate.postForObject(walletServiceUrl + "/debit", new WalletTransactionRequest(senderId, amount), Void.class);
            restTemplate.postForObject(walletServiceUrl + "/credit", new WalletTransactionRequest(recipientId, amount), Void.class);
            transaction.setStatus("COMPLETED");

            String sentMsg = String.format("You sent %.2f to user %s.", amount.doubleValue(), recipientEmail);
            kafkaProducerService.sendNotificationEvent(new NotificationRequest(senderId, sentMsg, "Transaction"));

            String receivedMsg = String.format("You received %.2f from user %d.", amount.doubleValue(), senderId);
            kafkaProducerService.sendNotificationEvent(new NotificationRequest(recipientId, receivedMsg, "Transaction"));
        } catch (Exception e) {
            transaction.setStatus("FAILED: " + e.getMessage());
        }
        return transactionRepository.save(transaction);
    }
}