package com.clone.paypal.notification_service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
public class KafkaConsumerService {
    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerService.class);

    @Autowired
    private NotificationRepository notificationRepository;

    @KafkaListener(topics = "notification_topic", groupId = "notification_group")
    public void consume(NotificationRequest notificationRequest) {
        logger.info("Consumed Kafka message -> Sending notification to user {}: '{}' of type '{}'",
                notificationRequest.getUserId(), notificationRequest.getMessage(), notificationRequest.getType());

        Notification notification = new Notification();
        notification.setUserId(notificationRequest.getUserId());
        notification.setMessage(notificationRequest.getMessage());
        notification.setTimestamp(LocalDateTime.now(ZoneId.of("Asia/Kolkata")));
        notification.setRead(false);
        notification.setType(notificationRequest.getType()); // Set the notification type
        notificationRepository.save(notification);
    }
}