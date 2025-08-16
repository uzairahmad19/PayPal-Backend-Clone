package com.clone.paypal.notification_service;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserIdOrderByTimestampDesc(Long userId);
    void deleteByUserId(Long userId);
    long countByUserIdAndReadIsFalse(Long userId);
}