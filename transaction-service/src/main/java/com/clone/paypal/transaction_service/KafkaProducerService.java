package com.clone.paypal.transaction_service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {
    private static final String TOPIC = "notification_topic";
    @Autowired
    private KafkaTemplate<String, NotificationRequest> kafkaTemplate;

    public void sendNotificationEvent(NotificationRequest request) {
        kafkaTemplate.send(TOPIC, request);
    }
}
