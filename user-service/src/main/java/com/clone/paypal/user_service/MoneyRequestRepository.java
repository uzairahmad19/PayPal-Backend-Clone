package com.clone.paypal.user_service;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MoneyRequestRepository extends JpaRepository<MoneyRequest, Long> {
    List<MoneyRequest> findByRequesterIdOrRecipientIdOrderByTimestampDesc(Long requesterId, Long recipientId);
    List<MoneyRequest> findByRecipientIdAndStatusOrderByTimestampDesc(Long recipientId, String status);
    List<MoneyRequest> findByRequesterIdAndStatusOrderByTimestampDesc(Long requesterId, String status);
}
