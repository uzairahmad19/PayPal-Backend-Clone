package com.clone.paypal.transaction_service;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findBySenderIdOrRecipientId(Long senderId, Long recipientId);
    void deleteBySenderIdOrRecipientId(Long senderId, Long recipientId);
}